package kokellab.utils.grammars.squints

import java.util.regex.Pattern
import scala.util.{Try, Success, Failure}

import scala.util.Try

/**
  * Utility to modify amounts with SI prefixes. Ex:
  * <code>
  * val squinter = new Squinter(Time.apply(_))
  * squinter("5 Gs") // 5 gigaseconds
  * </code>
  * @param parser Probably the apply method of a squants quantity
  * @tparam A The resulting type, probably a squants type
  */
class Squinter[A](parser: String => Try[A]) extends (String => A) {

	private val pattern = ("""((?:\d+)|(?:\d*\.\d+)) * ([""" + (SiPrefix.symbolToPrefix.keys mkString "|") + """])? *([A-Za-z/]+)""").r

	override def apply(s: String): A = {
		s match {
			case pattern(amount: String, prefix: String, unit: String) =>
				val value: Double = (SiPrefix.symbolToPrefix get prefix map (d => d.factor * amount.toDouble)) getOrElse 1.0
				parser(s"$value $unit") match {
					case Success(v) => v
					case Failure(e) =>
						parser(s) match {
							case Success(v2) => v2
							case Failure(e2) => throw new IllegalArgumentException(s"Could not parse expression $s", e)
						}
				}
		}
	}

}
