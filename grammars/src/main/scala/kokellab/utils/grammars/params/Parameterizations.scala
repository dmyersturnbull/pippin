package kokellab.utils.grammars.params

import kokellab.utils.grammars.{GrammarException, GridRangeGrammar, PointLike}

trait Parameterizer

class GridParameterizer() extends Parameterizer {

	private val rangeParameterizer = new RangeParameterizer()

	def mapToValue(
			cells: Seq[PointLike],
			valueExpression: String,
			substitutions: Map[DollarSignParam, DollarSignSub]
	): Map[PointLike, String] = {
		val valuesAndPoints: Seq[(PointLike, String)] = cells zip rangeParameterizer.mapToValue(cells map (_.index), valueExpression, substitutions)
		valuesAndPoints map { case (cell, value) =>
			cell -> fill(cell, value)
		}
	}.toMap

	private def fill(cell: PointLike, value: String): String =
		DollarSignParams.substitute(value, Map("$r" -> cell.row.toString, "$c" -> cell.column.toString, "$i" -> cell.index.toString))

}


class RangeParameterizer() extends Parameterizer {

	def mapToValue(
		range: Seq[Int],
		valueExpression: String,
		substitutions: Map[DollarSignParam, DollarSignSub]
	): Seq[String] = {

		val valueParams = DollarSignParams.find(valueExpression, Set.empty)
		val lengths = (valueParams map {p =>
			if (p.isList) p.name -> range.size else p.name -> 1
		}).toMap

		/*
		Note that we've checked the lengths above. We don't need to verify the substitutions object after that.
		There are two cases:
		    A) There are no list types $...x is defined in the expression. Then we replace all of the parameters and return.
		    B) There is exactly one list type $...x defined in the expression. Then we substitute each array index for each range index.
		 */

		if (valueParams forall (p => !p.isList)) {  // case A
			val replaced = substitutions map (k => (k._1.name, k._2.values.head))
			range map (x => DollarSignParams.substitute(valueExpression, replaced))

		} else if (valueParams.size == 1 && valueParams.head.isList) {  // case B
			substitutions.head._2.values

		} else {
			val sub = "\n[" + (substitutions map {case (key, value) => s"$key = $value"} mkString "\n") + "\n]"
			throw new GrammarException(s"Substitutions in '$sub' have the wrong type (list or non-list) for expression '$valueExpression'")
		}
	}

}
