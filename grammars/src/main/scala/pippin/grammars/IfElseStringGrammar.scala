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
		call[Option[String], IfElseStringGrammar](e => new IfElseStringGrammar(e, defaultFunctionMap), p => p.ifElseLine.run().get, expression)
	}

}

class IfElseStringGrammar(override val input: ParserInput, override val functions: Map[String, Seq[Int] => Int]) extends BooleanIntegerGrammar(input, functions) {

	def ifElseLine: Rule1[Option[String]] = rule { ifElse ~ EOI }

	def ifElse: Rule1[Option[String]] = rule { someValue | ifElifElse }

	def ifElifElse: Rule1[Option[String]] = rule {
		ifElif ~ elseExpr ~> ((a: Option[String], b: Option[String]) => if (a.isDefined) a else if (b.isDefined) b else None)
	}

	def elseExpr: Rule1[Option[String]] = rule {
		optional("else:" ~ ifElse) ~> ((value: Option[Option[String]]) => if (value.isDefined) value.get else None)
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
		booleanExpression ~ ":" ~ ifElse ~> ((boolean: Boolean, value: Option[String]) => if (boolean) value else None)
	}

}
