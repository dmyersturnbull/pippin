package kokellab.utils.grammars

import java.util.regex.Pattern

import scala.util.matching.Regex

object DollarSignParams {

	private val paramPattern = """\$\{?(?:\.{3})?[A-Za-z0-9_]*(?:\[[\$A-Za-z0-9_]+\])?\}?""".r

	def substitute(expression: String, substitutions: Map[String, String]) =
		substitutions.foldLeft(expression)((e, s) => e.replaceAllLiterally(s._1, s._2))

	def find(expression: String, predefinedNames: Set[String]): Iterator[DollarSignParam] = {
		assert(predefinedNames forall (_.startsWith("$")))
		_find(expression) map (s => DollarSignParam(s, predefinedNames.contains(s) || s.startsWith("$[")))
	}

	def _find(expression: String): Iterator[String] = for (m <- paramPattern.findAllMatchIn(expression)) yield m.group(0)

}

object DollarSignSubstitutions {

	def parse(text: String, pars: Set[DollarSignParam], lengths: Map[String, Int], pattern: Pattern): Seq[DollarSignSub] = {

		text.split("\n") map { s =>
			require(s contains '=')
			val key = s.substring(0, s.indexOf('='))
			val value = s.substring(s.indexOf('='))
			require(pars exists (p => p.name == key))
			val param = pars.find(p => p.name == key).get
			if (param.isList) {
				require((value contains "[") && (value endsWith "]"))
				val zs = value.substring(2, value.length - 1) split "," map (_.trim)
				assert(lengths contains param.name)
				require(lengths(param.name) == zs.length)
				require(zs forall (z => pattern.matcher(z).matches))
				ListSub(param, zs.toList)
			} else {
				SimpleSub(param, value)
			}
		}
	}

}

sealed trait DollarSignSub
case class SimpleSub(key: Param, value: String) extends DollarSignSub
case class ListSub(key: Param, values: List[String]) extends DollarSignSub


trait Param {
	def name: String
}

case class DollarSignParam(override val name: String, isPredefined: Boolean) extends Param {
	assert(name.startsWith("$"), "Parameters must start with a $")
	val isList: Boolean = name contains "..."
	val isArrayAccess: Boolean = name contains "["
	override val toString: String = name
}