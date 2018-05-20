package kokellab.utils.grammars

import kokellab.utils.grammars.squints.{SiPrefix, Squinter}
import squants.time.Time


object MillisecondRangeGrammar {

	/**
	  * Time SI prefix handling.
	  * Allows integral seconds, minutes, and hours, allowing milli and kilo, and assuming millseconds if no units are given.
	  */
	lazy val squinter: Squinter[Time] = new Squinter(
		Time.apply(_),
		Set("ms", "s", "m", "h"),
		numberParser = _.toInt,
		defaultUnit = "ms",
		numberPattern = Squinter.nonnegativeDoublePattern,
		siPrefixes = List(SiPrefix.milli, SiPrefix.kilo)
	)

	def eval(expression: String): (Int, Int) = {
		def squintedMillis(s: String): Int = squinter(s).toMilliseconds.toInt
		expression match {
			case rangePattern(a, b) =>
				(squintedMillis(a), squintedMillis(b))
			case _ => throw new GrammarException(s"Bad range expression $expression")
		}
	}

	private val timePattern = """((?:[\$A-Za-z_0-9]|(?:\d+(?:E[\+\-]?\d+)?)) *(?:[mk]?[smh])?)"""
	private val rangePattern = (timePattern + """ *[-–] *""" + timePattern).r

}
