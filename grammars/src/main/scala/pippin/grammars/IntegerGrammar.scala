package pippin.grammars

import org.parboiled2._

object IntegerGrammar {

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
		call[Int, IntegerGrammar](e => new IntegerGrammar(e, defaultFunctionMap), p => p.integerLine.run().get, expression)
	}

}

class IntegerGrammar(val input: ParserInput, val functions: Map[String, Seq[Int] => Int] = IntegerGrammar.defaultFunctionMap) extends Parser {

	def integerLine: Rule1[Int] = rule { expression ~ EOI }

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
		capture(optional(anyOf("-−")) ~ integer) ~> ((s: String) => s.toInt)
	}

	def integer: Rule0 = rule {
		oneOrMore(CharPredicate.Digit)
	}

	def fraction: Rule0 = rule { ch('.') ~ oneOrMore(CharPredicate.Digit) }

}
