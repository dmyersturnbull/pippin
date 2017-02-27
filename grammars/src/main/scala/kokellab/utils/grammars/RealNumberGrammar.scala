package kokellab.utils.grammars

import org.parboiled2._
import breeze.stats.distributions
import breeze.stats.distributions.{RandBasis, ThreadLocalRandomGenerator}
import org.apache.commons.math3.random.{MersenneTwister, RandomGenerator}


object RealNumberGrammar {

	val defaultFunctionMap: Map[String, Seq[Double] => Double] = Map(
		"bool" -> (a => if (a(0) == 0) 0 else 1),
		"sgn" -> (a => a(0).compareTo(0)),
		"sin" -> (a => math.sin(a(0))),
		"cos" -> (a => math.cos(a(0))),
		"sinh" -> (a => math.sinh(a(0))),
		"cosh" -> (a => math.cosh(a(0))),
		"asin" -> (a => math.asin(a(0))),
		"acos" -> (a => math.acos(a(0))),
		"tan" -> (a => math.tan(a(0))),
		"atan" -> (a => math.atan(a(0))),
		"atan2" -> (a => math.atan2(a(0), a(1))),
		"sqrt" -> (a => math.sqrt(a(0))),
		"cbrt" -> (a => math.cbrt(a(0))),
		"√" -> (a => math.sqrt(a(0))),
		"ln" -> (a => math.log(a(0))),
		"exp" -> (a => math.exp(a(0))),
		"pow" -> (a => math.pow(a(0), a(1))),
		"abs" -> (a => math.abs(a(0))),
		"round" -> (a => math.round(a(0))),
		"ceil" -> (a => math.ceil(a(0))),
		"floor" -> (a => math.floor(a(0))),
		"min" -> (a => a.min),
		"max" -> (a => a.max),
		"mean" -> mean,
		"stddev" -> stddev,
		"skewness" -> skewness,
		"geomean" -> geomean
	)

	def stochasticFunctionMap(randBasis: RandBasis): Map[String, Seq[Double] => Double] = {
		Map(
			"unifR" -> (a => new distributions.Uniform(a(0), a(1))(randBasis).sample()), // min, max
			"normR" -> (a => new distributions.Gaussian(a(0), a(1))(randBasis).sample()), // mean/ std
			"gammaR" -> (a => new distributions.Gamma(a(0), a(1))(randBasis).sample()), // shape, scale
			"betaR" -> (a => new distributions.Beta(a(0), a(1))(randBasis).sample()), // n(a), n(b)
			"expR" -> (a => new distributions.Exponential(a(0))(randBasis).sample()),
			"poissonR" -> (a => new distributions.Poisson(a(0))(randBasis).sample()),
			"intR" -> (a => randBasis.randInt(a(0).toInt, a(1).toInt).sample())
		)
	}

	def eval(expression: String, randBasis: Option[RandBasis] = None) = {
		val fns = if (randBasis.isDefined) defaultFunctionMap ++ stochasticFunctionMap(randBasis.get)
		else defaultFunctionMap
		val fixed = expression.replaceAllLiterally(" ", "")
		val parser = new RealNumberGrammar(fixed, fns)
		try {
			parser.line.run().get
		} catch {
			case e: ParseError =>
				throw new GrammarException(s"The expression $expression could not be parsed",
					Some(parser.formatError(e, new ErrorFormatter(showExpected = true, showFrameStartOffset = true, showLine = true, showPosition = true, showTraces = true))), Some(e))
		}
	}

	private def mean(a: Seq[Double]): Double = a.sum / a.size
	private def stddev(a: Seq[Double]): Double = math.sqrt(a.map(d => d*d).sum - math.pow(mean(a), 2))
	private def skewness(a: Seq[Double]): Double = {
		val m = mean(a)
		mean(a map (d => math.pow(d-m, 3))) / math.pow(mean(a map (d => (d-m)*(d-m))), 1.5)
	}
	private def geomean(a: Seq[Double]): Double = math.pow(a.product, 1.0 / a.size)

}

class RealNumberGrammar(var input: ParserInput,
						functions: Map[String, Seq[Double] => Double] = RealNumberGrammar.defaultFunctionMap
					   ) extends Parser {

	def line = rule { expression ~ EOI }

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

	/**
	  * This is the evaluated value of a function call
	  */
	def function: Rule1[Double] = rule {
		capture(functionName) ~ parameterList ~> (((fn: String), (e: Seq[Double])) => {
			if (functions contains fn) {
				try {
					functions(fn)(e)
				} catch {
					case error: NoSuchElementException => throw new GrammarException(s"Not enough arguments for function $fn: ${error.getMessage}")
				}
			} else throw new GrammarException(s"Function $fn is not defined")
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