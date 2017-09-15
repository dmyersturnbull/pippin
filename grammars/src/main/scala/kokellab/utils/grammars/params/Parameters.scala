package kokellab.utils.grammars.params

case class DollarSignSub(key: Param, values: List[String], isList: Boolean)


object DollarSignParams {

	private val paramPattern = """\$\{?(?:\.{3})?[A-Za-z0-9_]*(?:\[[\$A-Za-z0-9_]+\])?\}?""".r

	def substitute(expression: String, substitutions: Map[String, String]) =
		substitutions.foldLeft(expression)((e, s) => e.replaceAllLiterally(s._1, s._2))

	def find(expression: String, predefinedNames: Set[String]): Set[DollarSignParam] = {
		assert(predefinedNames forall (_.startsWith("$")))
		_find(expression) map (s => DollarSignParam(s, predefinedNames.contains(s) || s.startsWith("$[")))
	}.toSet

	def _find(expression: String): Iterator[String] = for (m <- paramPattern.findAllMatchIn(expression)) yield m.group(0)

}

trait Param {
	def name: String
}

case class DollarSignParam(override val name: String, isPredefined: Boolean) extends Param {
	assert(name.startsWith("$"), "Parameters must start with a $")
	val isList: Boolean = name contains "..."
	val isArrayAccess: Boolean = name contains "["
	override val toString: String = name
}
