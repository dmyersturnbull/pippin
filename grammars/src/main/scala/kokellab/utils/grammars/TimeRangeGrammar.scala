package kokellab.utils.grammars

import kokellab.utils.grammars.squints.{SiPrefix, Squinter}
import squants.time.Time

object TimeRangeGrammar {

	/**
	  * Time SI prefix handling.
	  * Allows integral seconds, minutes, and hours, allowing milli and kilo, and assuming millseconds if no units are given.
	  */
	def evalMillis(expression: String): (Long, Long) = {
		new TimeRangeGrammar(_.toMilliseconds.toLong, Set("ms", "s", "m", "h"), Set("m", "k"), Some("ms")).eval(expression)
	}

}

class TimeRangeGrammar(converter: Time => Long, units: Set[String], siPrefixes: Set[String], defaultUnit: Option[String]) {

	private lazy val squinter: Squinter[Time] = new Squinter(
		Time.apply(_),
		units,
		numberParser = _.toLong,
		defaultUnit = "ms",
		numberPattern = Squinter.nonnegativeDoublePattern,
		siPrefixes = List(SiPrefix.milli, SiPrefix.kilo)
	)

	def eval(expression: String): (Long, Long) = {
		def squintedMillis(s: String): Long = converter(squinter(s))
		expression match {
			case rangePattern(a, b) =>
				(squintedMillis(a), squintedMillis(b))
			case _ => throw new GrammarException(s"Bad range expression $expression")
		}
	}

//	private val timePattern = """((?:[\$A-Za-z_0-9]|(?:\d+(?:E[\+\-]?\d+)?)) *(?:[mk]?[smh])?)"""
	private val timePattern = """((?:[\$A-Za-z_0-9]|(?:\d+(?:E[\+\-]?\d+)?)) *(?:[""" + siPrefixes.mkString + "]?[" + units.mkString + "])?)"
	private val rangePattern = (timePattern + """ *[-–] *""" + timePattern).r

}
