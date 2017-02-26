package kokellab.utils.grammars

import org.parboiled2._

object BooleanGrammar {
	def eval(expression: String, tolerance: Double = 0.000001, seed: Option[Int] = None) = {
		val fns = if (seed.isDefined) RealNumberGrammar.defaultFunctionMap ++ RealNumberGrammar.stochasticFunctionMap(seed.get)
		else RealNumberGrammar.defaultFunctionMap
		val fixed = Map(" " -> "", "!=" -> "≠", "<=" -> "≤", ">=" -> "≥", "==" -> "=").foldLeft(expression) ((e, s) => e.replaceAllLiterally(s._1, s._2))
		val parser = new BooleanGrammar(fixed, tolerance, seed, fns)
		try {
			parser.line.run().get
		} catch {
			case e: ParseError =>
				throw new GrammarException(s"The expression $expression could not be parsed",
					Some(parser.formatError(e, new ErrorFormatter(showExpected = true, showFrameStartOffset = true, showLine = true, showPosition = true, showTraces = true))), Some(e))
		}
	}
}

class BooleanGrammar(val input: ParserInput, tolerance: Double = 0.000001, seed: Option[Int] = None, functions: Map[String, Seq[Double] => Double] = RealNumberGrammar.defaultFunctionMap) extends Parser {

	def line = rule { booleanExpression ~ EOI }

	def booleanExpression = rule {
		wrappedJunction ~ zeroOrMore(
			("and" | "∧") ~ wrappedJunction ~> ((_: Boolean) && _)
				| ("or" | "∨") ~ wrappedJunction ~> ((_: Boolean) || _)
				| ("nor" | "⊽") ~ wrappedJunction ~> ((a: Boolean, b: Boolean) => !(a || b))
				| ("nand" | "⊼") ~ wrappedJunction ~> ((a: Boolean, b: Boolean) => !(a && b))
				| ("xor" | "⊻") ~ wrappedJunction ~> ((_: Boolean) ^ _)
		)
	}

	def wrappedJunction: Rule1[Boolean] = rule {
		('(' ~ booleanExpression ~ ')') | junction
	}

	def junction: Rule1[Boolean] = rule {
		expression ~ oneOrMore(condition) ~> ((firstValue: Double, conditions: Seq[Condition]) => {
			var lhs = firstValue
			conditions forall {condition =>
				val satisfied = condition.operation(lhs)
				lhs = condition.rhs
				satisfied
			}
		})
	}

	def condition: Rule1[Condition] = rule {
		capture(anyOf("=≠<>≤≥≈≉")) ~ expression ~> ((op: String, b: Double) => Condition(b, op match {
			case "=" => (a: Double) => a == b
			case "≠" => (a: Double) => a != b
			case "<" => (a: Double) => a < b
			case ">" => (a: Double) => a > b
			case "≤" => (a: Double) => a <= b
			case "≥" => (a: Double) => a >= b
			case "≈" => (a: Double) => 2 * math.abs(a-b) / (a+b) < tolerance
			case "≉" => (a: Double) => 2 * math.abs(a-b) / (a+b) >= tolerance
		}))
	}

	/**
	  * A boolean operator with its right-hand side.
	  * @param rhs The right-hand side value
	  * @param operation A function that maps a left-hand side value to a boolean (equivalent to previousValue operation nextValue)
	  */
	case class Condition(rhs: Double, operation: Double => Boolean)

	def expression: Rule1[Double] = rule {
		term ~ zeroOrMore(
			'+' ~ term ~> ((_: Double) + _)
				| (ch('-')|'−') ~ term ~> ((_: Double) - _)
		)
	}

	def term: Rule1[Double] = rule {
		factor ~ zeroOrMore(
			(ch('*') | '×') ~ factor ~> ((_: Double) * _)
				| '/' ~ factor ~> ((_: Double) / _)
				| '%' ~ factor ~> ((_: Double) % _)
		)
	}

	def factor = rule {
		number | parentheses | function
	}

	def function: Rule1[Double] = rule {
		capture(functionName) ~ parameterList ~> (((fn: String), (e: Seq[Double])) => {
			if (functions contains fn) {
				try {
					functions(fn)(e)
				} catch {
					case error: NoSuchElementException => throw new IllegalArgumentException(s"Not enough arguments for function $fn", error)
				}
			} else throw new IllegalArgumentException(s"Function $fn is not defined") // TODO better error
		})
	}

	def functionName: Rule0 = rule {
		oneOrMore(CharPredicate.Alpha) ~ optional(oneOrMore(CharPredicate.AlphaNum))
	}

	def parameterList: Rule1[Seq[Double]] = rule {
		'(' ~ oneOrMore(expression).separatedBy(",") ~ ')'
	}

	def parentheses = rule { '(' ~ expression ~ ')' }

	def number: Rule1[Double] = rule {
		capture(optional(anyOf("-−")) ~ (floatingPoint | integer)) ~> ((s: String) => s.toDouble)
	}

	def integer: Rule0 = rule {
		oneOrMore(CharPredicate.Digit)
	}

	def floatingPoint: Rule0 = rule {
		oneOrMore(CharPredicate.Digit) ~  ch('.') ~ oneOrMore(CharPredicate.Digit)
	}

	def fraction: Rule0 = rule { ch('.') ~ oneOrMore(CharPredicate.Digit) }

}
