package kokellab.utils.grammars.squints

import squants.MetricSystem

import scala.util.Try

object SiPrefix {
	val prefixes: Set[SiPrefix] = Set(
		SiPrefix("yotta", "Y", MetricSystem.Yotta),
		SiPrefix("zetta", "Z", MetricSystem.Zetta),
		SiPrefix("exa", "E", MetricSystem.Exa),
		SiPrefix("peta", "P", MetricSystem.Peta),
		SiPrefix("tera", "T", MetricSystem.Tera),
		SiPrefix("giga", "G", MetricSystem.Giga),
		SiPrefix("mega", "M", MetricSystem.Mega),
		SiPrefix("kilo", "k", MetricSystem.Kilo),
		SiPrefix("hecto", "h", MetricSystem.Hecto),
		SiPrefix("deca", "da", MetricSystem.Deca),
		SiPrefix("deci", "d", MetricSystem.Deci),
		SiPrefix("centi", "c", MetricSystem.Centi),
		SiPrefix("milli", "m", MetricSystem.Milli),
		SiPrefix("micro", "µ", MetricSystem.Micro),
		SiPrefix("nano", "n", MetricSystem.Nano),
		SiPrefix("pico", "p", MetricSystem.Pico),
		SiPrefix("femto", "f", MetricSystem.Femto),
		SiPrefix("atto", "a", MetricSystem.Atto),
		SiPrefix("zepto", "z", MetricSystem.Zepto),
		SiPrefix("yocto", "y", MetricSystem.Yocto)
	)
	lazy val nameToPrefix: Map[String, SiPrefix] = (prefixes map (p => p.name -> p)).toMap
	lazy val symbolToPrefix: Map[String, SiPrefix] = (prefixes map (p => p.symbol -> p)).toMap
	lazy val bothToPrefix = nameToPrefix ++ symbolToPrefix
	def convert(nameOrSymbol: String): Try[Double] = {
		Try(bothToPrefix(nameOrSymbol)) map (_.factor)
	}
}

sealed case class SiPrefix(name: String, symbol: String, factor: Double)
