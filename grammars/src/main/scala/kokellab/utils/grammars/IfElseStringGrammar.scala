package kokellab.utils.grammars

import org.parboiled2._

/**
  * An if-elif-else grammar with string-type values and integer-only math.
  * The math forbids stochastic functions and functions or operators that could yield fractional results; this includes division (/).
  */
object IfElseStringGrammar {

	val defaultFunctionMap: Map[String, Seq[Int] => Int] = Map(
		"bool" -> (a => if (a(0) == 0) 0 else 1),
		"sgn" -> (a => a(0).compareTo(0)),
		"pow" -> (a => {
			if (a(1) >= 0) math.pow(a(0), a(1)).toInt
			else throw new GrammarException("pow(r, e) cannot use a negative exponent because only integers are allowed")
		}),
		"abs" -> (a => math.abs(a(0))),
		"min" -> (a => a.min),
		"max" -> (a => a.max)
	)

	def eval(expression: String) = {
		val fixed = Map(" " -> "", "!=" -> "≠", "<=" -> "≤", ">=" -> "≥", "==" -> "=").foldLeft(expression) ((e, s) => e.replaceAllLiterally(s._1, s._2))
		val parser = new IfElseStringGrammar(fixed, defaultFunctionMap)
		try {
			parser.line.run().get
		} catch {
			case e: ParseError =>
				throw new GrammarException(s"The expression $expression could not be parsed",
					Some(parser.formatError(e, new ErrorFormatter(showExpected = true, showFrameStartOffset = true, showLine = true, showPosition = true, showTraces = true))), Some(e))
		}
	}

}

class IfElseStringGrammar(val input: ParserInput, val functions: Map[String, Seq[Int] => Int]) extends Parser {

	def line: Rule1[Option[String]] = rule { (ifElifElse | someValue) ~ EOI }

	def ifElifElse: Rule1[Option[String]] = rule {
		ifElif ~ optional("else:" ~ value) ~> ((a: Option[String], b: Option[String]) => if (a.isDefined) a else if (b.isDefined) b else None)
	}

	def ifElif: Rule1[Option[String]] = rule {
		"if" ~ conditionRule ~ elifs ~> ((a: Option[String], b: Option[String]) => if (a.isDefined) a else if (b.isDefined) b else None)
	}

	/**
	  * Collapses any number of elif statements and returns the first one that matches, or None otherwise
	  */
	def elifs: Rule1[Option[String]] = rule {
		zeroOrMore("elif" ~ conditionRule) ~> ((list: Seq[Option[String]]) => list.flatten.headOption)
	}

	/**
	  * Just to be used in if-elif-else
	  */
	def someValue: Rule1[Option[String]] = rule { value ~> ((s: String) => Some(s)) }

	def value: Rule1[String] = rule { '"' ~ capture(zeroOrMore(noneOf("\""))) ~ '"'}

	def conditionRule: Rule1[Option[String]] = rule {
		booleanExpression ~ ":" ~ value ~> ((boolean: Boolean, value: String) => if (boolean) Some(value) else None)
	}

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

	/**
	  * A logical junction of booleans in the form val_1 operation_1 val_2 operation_2 ... operation_{n-1} val_n
	  * Ex: 5 < 10 = 10 > 1
	  */
	def junction: Rule1[Boolean] = rule {
		expression ~ oneOrMore(condition) ~> ((firstValue: Int, conditions: Seq[Condition]) => {
			var lhs = firstValue
			conditions forall {condition =>
				val satisfied = condition.operation(lhs)
				lhs = condition.rhs
				satisfied
			}
		})
	}

	def condition: Rule1[Condition] = rule {
		capture(anyOf("=≠<>≤≥")) ~ expression ~> ((op: String, b: Int) => Condition(b, op match {
			case "=" => (a: Int) => a == b
			case "≠" => (a: Int) => a != b
			case "<" => (a: Int) => a < b
			case ">" => (a: Int) => a > b
			case "≤" => (a: Int) => a <= b
			case "≥" => (a: Int) => a >= b
		}))
	}

	/**
	  * A boolean operator with its right-hand side.
	  * @param rhs The right-hand side value
	  * @param operation A function that maps a left-hand side value to a boolean (equivalent to previousValue operation nextValue)
	  */
	case class Condition(rhs: Int, operation: Int => Boolean)

	def expression: Rule1[Int] = rule {
		term ~ zeroOrMore(
			'+' ~ term ~> ((_: Int) + _)
				| (ch('-')|'−') ~ term ~> ((_: Int) - _)
		)
	}

	def term: Rule1[Int] = rule {
		factor ~ zeroOrMore(
			(ch('*') | '×') ~ factor ~> ((_: Int) * _)
				| '%' ~ factor ~> ((_: Int) % _)
		)
	}

	def factor = rule {
		number | parentheses | function
	}

	def function: Rule1[Int] = rule {
		capture(functionName) ~ parameterList ~> (((fn: String), (e: Seq[Int])) => {
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

	def parameterList: Rule1[Seq[Int]] = rule {
		'(' ~ oneOrMore(expression).separatedBy(",") ~ ')'
	}

	def parentheses = rule { '(' ~ expression ~ ')' }

	def number: Rule1[Int] = rule {
		capture(optional(anyOf("-−")) ~ (floatingPoint | integer)) ~> ((s: String) => s.toInt)
	}

	def integer: Rule0 = rule {
		oneOrMore(CharPredicate.Digit)
	}

	def floatingPoint: Rule0 = rule {
		oneOrMore(CharPredicate.Digit) ~  ch('.') ~ oneOrMore(CharPredicate.Digit)
	}

	def fraction: Rule0 = rule { ch('.') ~ oneOrMore(CharPredicate.Digit) }

}
