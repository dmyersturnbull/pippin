package kokellab.utils.grammars

import java.util.regex.Pattern

import breeze.stats.distributions.RandBasis

import scala.collection._
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex.Match

object TimeSeriesGrammar {

	def build(expression: String, start: Int, stop: Int,
			  randBasis: Option[RandBasis] = None, defaultValue: Double = 0.0, tolerance: Double = BooleanGrammar.DEFAULT_TOLERANCE
			 ): Seq[Double] = {
		val (value, interval) = extract(expression)
		new TimeSeriesGrammar(value, start, stop, randBasis, evaluationInterval = interval).build()
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

class TimeSeriesGrammar(expression: String, start: Int, stop: Int,
						randBasis: Option[RandBasis] = None, evaluationInterval: Int = 1, defaultValue: Double = 0.0, tolerance: Double = BooleanGrammar.DEFAULT_TOLERANCE) {

	private val tPattern = """\$t(?![\[A-Za-z0-9_])""".r
	private val tArrayPattern = """\$t\[([^\]]+)\]""".r

	def build(): Seq[Double] = {

		// we're going to build this up and access it with $t[index] as needed
		var values = mutable.ListBuffer.empty[Double]

		// replaces any single $t[index] with index evaluated by the RealNumberGrammar, whose result is truncated to Int
		def arrayAccessReplacer = (m: Match) => {
			// NOTE: truncates to Int!
			val index = RealNumberGrammar.eval(m.group(1), randBasis).toInt
			if (index > -1 && index < values.size) values(index).toString
			else defaultValue.toString
		}

		def substitute = (string: String, i: Int) => {
			// substitute $t and then $t[index]
			// the order is essential so that the array index can reference $t
			val tReplaced = tPattern.replaceAllIn(string, i.toString)
			tArrayPattern.replaceAllIn(tReplaced, arrayAccessReplacer)
		}

		// calculates the if-elif-else expression for an index, replacing $t and $t[index] as needed
		def calculate(i: Int): Double =
			IfElseGrammar.eval(substitute(expression, i), tolerance = tolerance, randBasis = randBasis).getOrElse(defaultValue)

		var lastValue = 0.0
		for (i <- start to stop) {
			values += {
				if (i == start || i % evaluationInterval == 0)
					calculate(i)
				else lastValue
			}
			lastValue = values.last
		}

		values
	}


}
