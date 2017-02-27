package kokellab.utils.grammars

import breeze.stats.distributions.RandBasis
import org.parboiled2._

object IfElseGrammar {

	def eval(expression: String, tolerance: Double = BooleanGrammar.DEFAULT_TOLERANCE, randBasis: Option[RandBasis] = None) = {
		val functions = if (randBasis.isDefined) RealNumberGrammar.defaultFunctionMap ++ RealNumberGrammar.stochasticFunctionMap(randBasis.get)
		else RealNumberGrammar.defaultFunctionMap
		val parser = new IfElseGrammar(GrammarUtils.replaceCommon(expression), tolerance, randBasis, functions)
		GrammarUtils.wrapGrammarException(expression, parser, () => parser.ifElseLine.run().get)
	}
}

class IfElseGrammar(override val input: ParserInput,
					tolerance: Double = BooleanGrammar.DEFAULT_TOLERANCE, randBasis: Option[RandBasis] = None,
					functions: Map[String, Seq[Double] => Double] = RealNumberGrammar.defaultFunctionMap
				   ) extends BooleanGrammar(input, tolerance, randBasis, functions) {

	def ifElseLine: Rule1[Option[Double]] = rule { (someExpression | ifElifElse) ~ EOI }

	def ifElifElse: Rule1[Option[Double]] = rule {
		ifElif ~ optional("else:" ~ expression) ~> ((a: Option[Double], b: Option[Double]) => if (a.isDefined) a else if (b.isDefined) b else None)
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
		booleanExpression ~ ":" ~ expression ~> ((boolean: Boolean, value: Double) => if (boolean) Some(value) else None)
	}

}
