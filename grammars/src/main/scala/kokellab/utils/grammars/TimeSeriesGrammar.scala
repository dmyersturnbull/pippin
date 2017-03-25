package kokellab.utils.grammars

import breeze.stats.distributions.RandBasis

import scala.collection._
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex.Match

object TimeSeriesGrammar {

	def build[A : ClassTag](expression: String, start: Int, stop: Int, converter: Double => A,
			  randBasis: Option[RandBasis] = None, defaultValue: Byte = 0, tolerance: Double = BooleanRealNumberGrammar.DEFAULT_TOLERANCE
			 ): Array[A] = {
		val (value, interval) = extract(expression)
		new TimeSeriesGrammar(value, start, stop, converter, randBasis, interval, defaultValue, tolerance).build()
	}

	private val mainPattern = """ *(.*?) *(?: +(?:evaluate every)|@ +(\d+))?""".r

	private def extract(expression: String): (String, Int) = expression match {
		case mainPattern(value: String, interval) =>
			val intervalInt = if (interval == null) 1
			else Try(interval.toInt) match {
				case Success(v) => if (v > 0) v else throw new GrammarException("Evaluation interval $interval (\"evaluate every\" or \"@\" must be a positive integer")
				case Failure(_) => throw new GrammarException("Evaluation interval $interval (\"evaluate every\" or \"@\" must be a positive integer")
			}
			(value, intervalInt)
	}
}

class TimeSeriesGrammar[@specialized(Float, Double, Byte, Short, Int) A : ClassTag](expression: String, start: Int, stop: Int, converter: Double => A,
						randBasis: Option[RandBasis] = None, evaluationInterval: Int = 1, defaultValue: Double = 0, tolerance: Double = BooleanRealNumberGrammar.DEFAULT_TOLERANCE) {

	private val tPattern = """\$t(?![\[A-Za-z0-9_])""".r
	private val tArrayPattern = """\$t\[([^\]]+)\]""".r

	def build(): Array[A] = {

		if (Try(converter(expression.toDouble)).isSuccess) {
			return Array.fill[A](stop - start)(converter(expression.toDouble))
		}

		// we're going to build this up and access it with $t[index] as needed
		var values: Array[A] = Array.ofDim[A](stop - start)

		// replaces any single $t[index] with index evaluated by the RealNumberGrammar, whose result is truncated to Int

		def arrayAccessReplacer = (m: Match) => {
			// NOTE: truncates to Int!
			val index = RealNumberGrammar.eval(m.group(1), randBasis).toInt
			if (index > -1 && index < values.length) values(index).toString
			else defaultValue.toString
		}

		val substitute = if (expression contains '[') {
			(string: String, i: Int) => {
				// substitute $t and then $t[index]
				// the order is essential so that the array index can reference $t
				val tReplaced = tPattern.replaceAllIn(string, i.toString)
				tArrayPattern.replaceAllIn(tReplaced, arrayAccessReplacer)
			}
		} else if (expression contains '$') {
			(string: String, i: Int) => tPattern.replaceAllIn(string, i.toString)
		} else {
			(string: String, i: Int) => string
		}

		// calculates the if-elif-else expression for an index, replacing $t and $t[index] as needed
		val calculate: Int => Double = if (expression containsSlice "if") {
			i: Int => IfElseRealNumberGrammar.eval(substitute(expression, i), tolerance = tolerance, randBasis = randBasis).getOrElse(defaultValue)
		} else {
			i: Int => RealNumberGrammar.eval(substitute(expression, i), randBasis = randBasis)
		}

		var lastValue: A = converter(0.0)
		for (i <- start until stop) {
			values(i) = {
				if (i % evaluationInterval == 0 || i == start) converter{
					lastValue = values.last
					calculate(i)
				} else lastValue
			}
		}

		values
	}

}
