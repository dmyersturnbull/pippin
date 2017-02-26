package kokellab.utils.grammars

import java.util.regex.Pattern

import scala.collection._
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex.Match

object TimeSeriesGrammar {

	def build(expression: String, start: Int, stop: Int,
			  seed: Option[Int], defaultValue: Double = 0.0, tolerance: Double = IfElseGrammar.DEFAULT_TOLERANCE
			 ): Seq[Double] = {
		val (value, interval) = extract(expression)
		new TimeSeriesGrammar(value, start, stop, seed = seed, evaluationInterval = interval).build()
	}

	private val mainPattern = """ *(.*?) *(?: +(?:evaluate every)|@ +(\d+))?""".r

	private def extract(expression: String): (String, Int) = expression match {
		case mainPattern(value, interval) =>
			val intervalInt = if (interval.isEmpty) 1
			else Try(interval.toInt) match {
				case Success(v) => if (v > 0) v else throw new GrammarException("Evaluation interval $interval (\"evaluate every\" or \"@\" must be a positive integer")
				case Failure(v) => throw new GrammarException("Evaluation interval $interval (\"evaluate every\" or \"@\" must be a positive integer")
			}
			(value, intervalInt)
	}
}

class TimeSeriesGrammar(expression: String, start: Int, stop: Int,
		seed: Option[Int] = None, evaluationInterval: Int = 1, defaultValue: Double = 0.0, tolerance: Double = IfElseGrammar.DEFAULT_TOLERANCE) {

	private val tPattern = """\$t[^A-Za-z0-9_]""".r
	private val tArrayPattern = """\$t\[([^\]]+)\]""".r

	def build(): Seq[Double] = {

		// we're going to build this up and access it with $t[index] as needed
		var values = mutable.ListBuffer.empty[Double]

		// replaces any single $t[index] with index evaluated by the RealNumberGrammar, whose result is truncated to Int
		def arrayAccessReplacer = (m: Match) => {
			// NOTE: truncates to Int!
			val index = RealNumberGrammar.eval(m.group(1), seed).toInt
			if (index < values.size) values(index).toString
			else throw new GrammarException("Array access for time (t) $index exceeds current index ${values.size - 1}: Only preceeding values can be accessed")
		}

		def substitute = (string: String, i: Int) => {
			// substitute $t and then $t[index]
			// the order is essential so that the array index can reference $t
			val tReplaced = tPattern.replaceAllIn(string, i.toString)
			tArrayPattern.replaceAllIn(tReplaced, arrayAccessReplacer)
		}

		// calculates the if-elif-else expression for an index, replacing $t and $t[index] as needed
		def calculate(i: Int): Double =
			IfElseGrammar.eval(substitute(expression, i), tolerance = tolerance, seed = seed).getOrElse(defaultValue)

		var lastValue = 0.0

		for (i <- start to stop) {
			values(i) = if (i == start || i % evaluationInterval == 0)
				calculate(i)
			else lastValue
			lastValue = values.last
		}

		values
	}


}
