package kokellab.utils.grammars.squints

import java.util.regex.Pattern

import squants.mass.SubstanceConcentration
import squants.time.Time
import squants.{Dimension, Quantity}

import scala.util.{Failure, Success, Try}
import scala.util.Try

/**
  * Utility to modify amounts with SI prefixes. Ex:
  * <code>
  * val squinter = new Squinter(Time.apply(_), Set("s"), _.toDouble)
  * squinter("5 Gs") // 5 gigaseconds
  * </code>
  * @param parser Probably the apply method of a squants quantity
  * @tparam A The resulting type, probably a squants type
  */
class Squinter[A <: Quantity[A]](
									parser: String => Try[A],
									allowedUnits: Set[String],
									numberParser: String => Double = _.toDouble,
									defaultUnit: String = "",
									numberPattern: String = Squinter.doublePattern,
									siPrefixes: List[SiPrefix] = SiPrefix.prefixes
								) extends (String => A) {

	private val prefixMap = (siPrefixes map (p => p.symbol -> p)).toMap

	private val pattern = ("(" + numberPattern + ") *([" + (prefixMap.keys mkString "|") + "]?)((?:" + (allowedUnits mkString "|") + ")?)").r

	override def apply(s: String): A = {
		val united = if (allowedUnits exists (u => s endsWith u)) s else s + " " + defaultUnit
		united match {
			case pattern(amount: String, prefix: String, unit: String) =>
				val value: Double = (prefixMap get prefix map (d => d.factor * amount.toDouble)) getOrElse 1.0
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

object Squinter {

	val doublePattern = """(?:(?:[-−]?\d+)|(?:\d*\.\d+))(?:[Ee][\+\-]?\d+)?"""
	val nonnegativeDoublePattern = """(?:(?:\d+)|(?:\d*\.\d+))(?:[Ee][\+\-]?\d+)?"""

	/**
	  * Allows integral seconds, minutes, and hours, allowing milli and kilo, and assuming millseconds if no units are given.
	  */
	lazy val milliseconds: Squinter[Time] = new Squinter(Time.apply(_), Set("s", "m", "h"), numberParser = _.toInt, defaultUnit = "ms", numberPattern = nonnegativeDoublePattern,
		siPrefixes = List(SiPrefix.milli, SiPrefix.kilo))

	/**
	  * Allows molar units (M or mol/L) nano, micro, and milli, assuming micromolars if no units are given.
	  */
	lazy val micromolars: Squinter[SubstanceConcentration] = new Squinter(SubstanceConcentration.apply(_), Set("M", "mol/L"), numberParser = _.toDouble, defaultUnit = "µM", numberPattern = nonnegativeDoublePattern,
		siPrefixes = SiPrefix.between("nano", "milli"))

}
