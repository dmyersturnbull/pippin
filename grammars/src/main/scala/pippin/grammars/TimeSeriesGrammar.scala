package pippin.grammars

import breeze.stats.distributions.RandBasis

import scala.collection._
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex.Match

class EvaluationIntervalException(message: String, verboseMessage: Option[String] = None, underlying: Option[Exception] = None)
	extends GrammarException(message, verboseMessage, underlying)

object TimeSeriesGrammar {

	def build[A : ClassTag](
			expression: String, start: Int, stop: Int, converter: Double => A,
			randBasis: Option[RandBasis] = None,
			defaultValue: Double = 0, outOfBoundsValue: Double = Double.NaN,
			tolerance: Double = BooleanRealNumberGrammar.DEFAULT_TOLERANCE
	): IterableOnce[A] = {
		val (value, interval) = extract(expression)
		new TimeSeriesGrammar(value, start, stop, converter, randBasis, interval, defaultValue, outOfBoundsValue = outOfBoundsValue, tolerance = tolerance).build()
	}

	// we allow a negative and decimal points in the evaluation interval to catch those errors intelligently
	private val mainPattern = """ *(.*?) *(?: *(?:evaluate every)|@ *(\-?\d+(?:\.\d+)?))?""".r

	private def extract(expression: String): (String, Int) = expression match {
		case mainPattern(value: String, interval) =>
			val intervalInt = if (interval == null) 1
			else Try(interval.toInt) match {
				case Success(v) => if (v > 0) {
					checkEvaluationIntervalDividesDivisor(expression)
					v
				} else throw new EvaluationIntervalException("Evaluation interval $interval (\"evaluate every\" or \"@\" must be a positive integer")
				case Failure(_) => throw new EvaluationIntervalException("Evaluation interval $interval (\"evaluate every\" or \"@\" must be a positive integer")
			}
			(value, intervalInt)
	}

	private def checkEvaluationIntervalDividesDivisor(expression: String): Unit = expression match {
		case divisorAndEvaluationPattern(modulus: String, interval: String) =>
			if (modulus.toInt % interval.toInt != 0) {
				throw new EvaluationIntervalException(
					s"Evaluation interval $interval does not divide modulus $modulus",
					Some("Evaluation interval $interval does not divide modulus $modulus used in modulo operation in expression '$expression'")
				)
			}
		case _ => // no evaluation interval, so it's defined to be 1
	}

	private val divisorAndEvaluationPattern = """.*%(\d+).*@ *(\d+) *""".r

}

class TimeSeriesGrammar[@specialized(Float, Double, Byte, Short, Int) A : ClassTag](
			expression: String, start: Int, stop: Int, converter: Double => A,
			randBasis: Option[RandBasis] = None, evaluationInterval: Int = 1,
			defaultValue: Double = 0, outOfBoundsValue: Double = Double.NaN,
			tolerance: Double = BooleanRealNumberGrammar.DEFAULT_TOLERANCE
) {

	if (evaluationInterval > stop - start) {
		throw new EvaluationIntervalException(s"Evaluation interval $evaluationInterval is longer than the block (start=$start, stop=$stop)")
	}

	private val tPattern = """\$t(?![\[A-Za-z0-9_])""".r
	private val tArrayPattern = """\$t\[([^\]]+)\]""".r

	private def arrayAccessReplacer(values: Array[A])(m: Match): String = {
		// we could accept even more sophisticated indexing, such as if-else or real numbers
		// but this is safer and more efficient
		val index = IntegerGrammar.eval(m.group(1))
		val result = if (index >= start && index < values.length * evaluationInterval) values(index / evaluationInterval).toString
		else outOfBoundsValue.toString
		result
	}

	private def fullReplacer(values: Array[A]) = {
		val r = arrayAccessReplacer(values) _
		(i: Int) => {
			// substitute $t and then $t[index]
			// the order is essential so that the array index can reference $t
			val tReplaced = tPattern.replaceAllIn(expression, i.toString)
			tArrayPattern.replaceAllIn(tReplaced, r)
		}
	}

	private val simpleReplacer = (i: Int) => tPattern.replaceAllIn(expression, i.toString)

	def build(): IterableOnce[A] = {

		if (Try(converter(expression.toDouble)).isSuccess) {
			Iterator.fill[A](stop - start)(converter(expression.toDouble))
		} else if (expression contains '[') {
			buildWithArrayAccess()
		} else {
			buildStreaming()
		}

	}

	private def buildWithArrayAccess(): Array[A] = {

		// we're going to build this up and access it with $t[index] as needed
		val values: Array[A] = Array.ofDim[A]((stop - start) / evaluationInterval)

		val replacer = fullReplacer(values)

		// calculates the if-elif-else expression for an index, replacing $t and $t[index] as needed
		val calculate: Int => Double = if (expression containsSlice "if") {
			i: Int => IfElseRealNumberGrammar.eval(replacer(i), tolerance = tolerance, randBasis = randBasis).getOrElse(defaultValue)
		} else {
			i: Int => RealNumberGrammar.eval(replacer(i), randBasis = randBasis)
		}

		var lastValue: A = converter(0.0)
		for (i <- start until stop) yield {
			lastValue = if (i % evaluationInterval == 0 || i == start) converter(calculate(i))
			else lastValue
			values(i / evaluationInterval) = lastValue
			lastValue
		}

		values
	}

	private def buildStreaming(): IterableOnce[A] = {

		val replacer = simpleReplacer

		// send to IfElse grammar liberally to make errors easier; ex: send "5 else 2" to IfElse
		val calculate: Int => Double = if ((expression containsSlice "if") || (expression containsSlice "else") || (expression containsSlice "elif")) {
			i: Int => IfElseRealNumberGrammar.eval(replacer(i), tolerance = tolerance, randBasis = randBasis).getOrElse(defaultValue)
		} else {
			i: Int => RealNumberGrammar.eval(replacer(i), randBasis = randBasis)
		}

		var lastValue: A = converter(0.0)
		for (i <- start until stop) yield {
			lastValue = if (i % evaluationInterval == 0 || i == start) converter(calculate(i))
			else lastValue
			lastValue
		}

	}


}
