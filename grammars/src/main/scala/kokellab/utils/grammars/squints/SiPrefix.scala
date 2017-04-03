package kokellab.utils.grammars.squints

import squants.MetricSystem

import scala.util.Try

object SiPrefix {

	val yotta = SiPrefix("yotta", "Y", MetricSystem.Yotta)
	val zetta = SiPrefix("zetta", "Z", MetricSystem.Zetta)
	val exa = SiPrefix("exa", "E", MetricSystem.Exa)
	val peta = SiPrefix("peta", "P", MetricSystem.Peta)
	val tera = SiPrefix("tera", "T", MetricSystem.Tera)
	val giga = SiPrefix("giga", "G", MetricSystem.Giga)
	val mega = SiPrefix("mega", "M", MetricSystem.Mega)
	val kilo = SiPrefix("kilo", "k", MetricSystem.Kilo)
	val hecto = SiPrefix("hecto", "h", MetricSystem.Hecto)
	val deca = SiPrefix("deca", "da", MetricSystem.Deca)
	val deci = SiPrefix("deci", "d", MetricSystem.Deci)
	val centi = SiPrefix("centi", "c", MetricSystem.Centi)
	val milli = SiPrefix("milli", "m", MetricSystem.Milli)
	val micro = SiPrefix("micro", "µ", MetricSystem.Micro)
	val nano = SiPrefix("nano", "n", MetricSystem.Nano)
	val pico = SiPrefix("pico", "p", MetricSystem.Pico)
	val femto = SiPrefix("femto", "f", MetricSystem.Femto)
	val atto = SiPrefix("atto", "a", MetricSystem.Atto)
	val zepto = SiPrefix("zepto", "z", MetricSystem.Zepto)
	val yocto = SiPrefix("yocto", "y", MetricSystem.Yocto)

	val prefixes: List[SiPrefix] = List(
		yotta, zetta, exa, peta, tera, giga, mega, kilo, hecto, deca,
		deci, centi, milli, micro, nano, pico, femto, atto, zepto, yocto
	).reverse

	lazy val nameToPrefix: Map[String, SiPrefix] = (prefixes map (p => p.name -> p)).toMap
	lazy val symbolToPrefix: Map[String, SiPrefix] = (prefixes map (p => p.symbol -> p)).toMap
	lazy val bothToPrefix = nameToPrefix ++ symbolToPrefix

	def between(start: String, end: String): List[SiPrefix] = between(bothToPrefix(start), bothToPrefix(end))

	def between(start: SiPrefix, end: SiPrefix): List[SiPrefix] =
		prefixes slice (prefixes indexOf start, (prefixes indexOf end) + 1)

	def between(startLogLevel: Int, endLogLevel: Int): List[SiPrefix] =
		prefixes slice (10 + startLogLevel, 10 + endLogLevel)

	def byLogLevel(log: Byte) = prefixes(10 + log)

	def convert(nameOrSymbol: String): Try[Double] = {
		Try(bothToPrefix(nameOrSymbol)) map (_.factor)
	}

	def convertFromSuffix(expression: String, expectedUnits: Set[String]) = {
		val pattern = ("""((?:\d+)|(?:\d*\.\d+)) * ([""" + (SiPrefix.symbolToPrefix.keys mkString "|") + """])?([A-Za-z/]+)""").r
	}
}

sealed case class SiPrefix(name: String, symbol: String, factor: Double)
