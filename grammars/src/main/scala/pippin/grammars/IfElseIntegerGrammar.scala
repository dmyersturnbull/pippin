package kokellab.utils.grammars

import org.parboiled2.{ParserInput, Rule1}

/**
  * An if-elif-else grammar with string-type values and integer-only math.
  * The math forbids stochastic functions and functions or operators that could yield fractional results; this includes division (/).
  */
object IfElseIntegerGrammar {

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

	def eval(expression: String): Option[Int] = {
		call[Option[Int], IfElseIntegerGrammar](e => new IfElseIntegerGrammar(e, defaultFunctionMap), p => p.ifElseLine.run().get, expression)
	}

}

class IfElseIntegerGrammar(override val input: ParserInput, override val functions: Map[String, Seq[Int] => Int]) extends BooleanIntegerGrammar(input, functions) {

	def ifElseLine: Rule1[Option[Int]] = rule { ifElse ~ EOI }

	def ifElse: Rule1[Option[Int]] = rule { someExpression | ifElifElse }

	def ifElifElse: Rule1[Option[Int]] = rule {
		ifElif ~ elseExpr ~> ((a: Option[Int], b: Option[Int]) => if (a.isDefined) a else if (b.isDefined) b else None)
	}

	def elseExpr: Rule1[Option[Int]] = rule {
		optional("else:" ~ ifElse) ~> ((value: Option[Option[Int]]) => if (value.isDefined) value.get else None)
	}

	def ifElif: Rule1[Option[Int]] = rule {
		"if" ~ conditionRule ~ elifs ~> ((a: Option[Int], b: Option[Int]) => if (a.isDefined) a else if (b.isDefined) b else None)
	}

	/**
	  * Collapses any number of elif statements and returns the first one that matches, or None otherwise
	  */
	def elifs: Rule1[Option[Int]] = rule {
		zeroOrMore("elif" ~ conditionRule) ~> ((list: Seq[Option[Int]]) => list.flatten.headOption)
	}

	/**
	  * Just to be used in if-elif-else
	  */
	def someExpression: Rule1[Option[Int]] = rule { expression ~> ((d: Int) => Some(d)) }

	def conditionRule: Rule1[Option[Int]] = rule {
		booleanExpression ~ ":" ~ ifElse ~> ((boolean: Boolean, value: Option[Int]) => if (boolean) value else None)
	}

}