package pippin.grammars.params

import pippin.core
import pippin.core.RegexUtils._


object DollarSignParams {

	def substitute(expression: String, substitutions: Map[String, String]) =
		substitutions.foldLeft(expression)((e, s) => e.replace(s._1, s._2))

	def find(expression: String, predefinedNames: Set[String]): Set[DollarSignParam] = {
		assert(predefinedNames forall (_.startsWith("$")))
		_find(expression) map (s => DollarSignParam(s, (predefinedNames contains s) || (s startsWith "$[")))
	}.toSet

	def _find(expression: String): Iterator[String] = for (m <- paramPattern.findAllMatchIn(expression)) yield m.group(0)

}


case class DollarSignSub(key: DollarSignParam, values: List[String]) {
	val isList = key.isList
	require(values.nonEmpty, s"Substitution for $key is empty")
	require(isList || (values.size < 2), s"Substitution for $key contains ${values.size} elements but is not a list")
	def only: String = core.only(values)
	override def toString: String = s"${getClass.getSimpleName}($key=${values mkString ","})"
	def valueToText: String = {
		if (isList) s"[${values.mkString(",")}]"
		else values.head
	}
}

case class DollarSignParam(name: String, isPredefined: Boolean) {
	require(name.startsWith("$"), s"Parameter $name does not start with a $$")
	def isValid: Boolean = paramPattern matches name
	def isList: Boolean = name contains "..."
	def isArrayAccess: Boolean = name contains '['
	override val toString: String = name
}

class Ization(subs: Seq[DollarSignSub]) {
	private def bad = (subs groupBy (_.key.name) filter { case (name, values) => values.size != 1 }).keys
	if (bad.nonEmpty) throw new IllegalArgumentException(s"Parameterization contains duplicate keys $bad")
	val asMap: Map[DollarSignParam, DollarSignSub] = (subs map (s => s.key -> s)).toMap
	protected val lookup: Map[String, DollarSignSub] = (subs map (s => s.key.name -> s)).toMap
	def apply(key: String): DollarSignSub = lookup(key)
	def apply(key: DollarSignParam): DollarSignSub = asMap(key)
	def keys: Set[DollarSignParam] = asMap.keySet
	def values: Set[DollarSignSub] = asMap.values.toSet
	def names: Set[String] = asMap.keySet map (_.name)
	def get(key: String): Option[DollarSignSub] = lookup.get(key)
	def get(key: DollarSignParam): Option[DollarSignSub] = asMap.get(key)
	def contains(key: String): Boolean = lookup contains key
	def contains(key: DollarSignParam) = asMap contains key
	override def toString: String = s"${getClass.getSimpleName}(${subs mkString ", "})"
}
