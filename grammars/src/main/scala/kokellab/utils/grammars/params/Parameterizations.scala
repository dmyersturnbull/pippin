package kokellab.utils.grammars.params

import kokellab.utils.grammars.{GrammarException, GridRangeGrammar, PointLike}


class GridParameterizer[A](
		val grammarFn: String => Option[A],
		val quote: Boolean = false
) {

	val rangeParameterizer = new RangeParameterizer(quote = quote)

	def mapValuesOntoGrid(
							 range: String,
							 valueExpression: String,
							 substitutions: Map[DollarSignParam, DollarSignSub],
							 nRows: Int, nColumns: Int
						 ): Map[PointLike, A] = {
		val cells: Seq[PointLike] = GridRangeGrammar.eval(range, nRows, nColumns)
		val tuple = rangeParameterizer.mapIndexToValue(cells map (_.index), valueExpression, substitutions) match {
			case Left(singleExpression) =>
				cells flatMap { cell => {
					val replaced = DollarSignParams.substitute(singleExpression, Map("$r" -> cell.row.toString, "$c" -> cell.column.toString, "$i" -> cell.index.toString))
					grammarFn(replaced) map { ans => cell -> ans }
				}
				}
			case Right(manyValues) =>
				cells zip (manyValues map (v => grammarFn(v).get))
		}
		tuple.toMap
	}

}


class RangeParameterizer(
	val quote: Boolean = false
) {

	/**
	  *
	  * @param range
	  * @param valueExpression
	  * @return just a String if the value applies to everything in the range; a list otherwise
	  */
	def mapIndexToValue(
		range: Seq[Int],
		valueExpression: String,
		substitutions: Map[DollarSignParam, DollarSignSub]
	): Either[String, Seq[String]] = {

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
			Left(DollarSignParams.substitute(valueExpression, replaced))

		} else if (valueParams.size == 1 && valueParams.head.isList) {  // case B
			Right(substitutions.head._2.values)

		} else {
			val sub = "\n[" + (substitutions map {case (key, value) => s"$key = $value"} mkString "\n") + "\n]"
			throw new GrammarException(s"Substitutions in '$sub' have the wrong type (list or non-list) for expression '$valueExpression'")
		}
	}

}
