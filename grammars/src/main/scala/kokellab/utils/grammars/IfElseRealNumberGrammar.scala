package kokellab.utils.grammars

import breeze.stats.distributions.RandBasis
import org.parboiled2._

object IfElseRealNumberGrammar {

	def eval(expression: String, tolerance: Double = BooleanRealNumberGrammar.DEFAULT_TOLERANCE, randBasis: Option[RandBasis] = None): Option[Double] = {
		val functions = if (randBasis.isDefined) RealNumberGrammar.defaultFunctionMap ++ RealNumberGrammar.stochasticFunctionMap(randBasis.get)
		else RealNumberGrammar.defaultFunctionMap
		val parser = new IfElseRealNumberGrammar(GrammarUtils.replaceCommon(expression), tolerance, randBasis, functions)
		GrammarUtils.wrapGrammarException(expression, parser, () => parser.ifElseLine.run().get)
	}
}

class IfElseRealNumberGrammar(override val input: ParserInput,
		tolerance: Double = BooleanRealNumberGrammar.DEFAULT_TOLERANCE, randBasis: Option[RandBasis] = None,
		functions: Map[String, Seq[Double] => Double] = RealNumberGrammar.defaultFunctionMap
) extends BooleanRealNumberGrammar(input, tolerance, randBasis, functions) {

	def ifElseLine: Rule1[Option[Double]] = rule { ifElse ~ EOI }

	def ifElse: Rule1[Option[Double]] = rule { someExpression | ifElifElse }

	def ifElifElse: Rule1[Option[Double]] = rule {
		ifElif ~ elseExpr ~> ((a: Option[Double], b: Option[Double]) => if (a.isDefined) a else if (b.isDefined) b else None)
	}

	def elseExpr: Rule1[Option[Double]] = rule {
		optional("else:" ~ ifElse) ~> ((value: Option[Option[Double]]) => if (value.isDefined) value.get else None)
	}

	def ifElif: Rule1[Option[Double]] = rule {
		"if" ~ conditionRule ~ elifs ~> ((a: Option[Double], b: Option[Double]) => if (a.isDefined) a else if (b.isDefined) b else None)
	}

	/**
	  * Collapses any number of elif statements and returns the first one that matches, or None otherwise
	  */
	def elifs: Rule1[Option[Double]] = rule {
		zeroOrMore("elif" ~ conditionRule) ~> ((list: Seq[Option[Double]]) => list.flatten.headOption)
	}

	/**
	  * Just to be used in if-elif-else
	  */
	def someExpression: Rule1[Option[Double]] = rule { expression ~> ((d: Double) => Some(d)) }

	def conditionRule: Rule1[Option[Double]] = rule {
		booleanExpression ~ ":" ~ ifElse ~> ((boolean: Boolean, value: Option[Double]) => if (boolean && value.isDefined) Some(value.get) else None)
	}

}
