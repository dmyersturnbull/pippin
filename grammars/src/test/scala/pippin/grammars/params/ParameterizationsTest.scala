package kokellab.utils.grammars.params

import kokellab.utils.grammars.{AlphanumericGrid, IfElseIntegerGrammar, PointLike}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class ParameterizationsTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Parameterize grid") {
		val parameterizer = new GridParameterizer()
		val param = DollarSignParam("$x", false)
		val sub = DollarSignSub(param, List("7"))
		val grid = AlphanumericGrid(8, 12)
		val results: Map[PointLike, String] = parameterizer.mapToValue(
			Seq(grid.Point(1,2), grid.Point(1,3), grid.Point(1,4)),
			"i=$i, r=$r, c=$c, x=$x",
			Map(param -> sub)
		)
		results.values.toList should equal (List("i=2, r=1, c=2, x=7", "i=3, r=1, c=3, x=7", "i=4, r=1, c=4, x=7"))
	}

	property(s"Parameterize range") {
		val parameterizer = new RangeParameterizer()
		val param = DollarSignParam("$x", false)
		val sub = DollarSignSub(param, List("7"))
		val results: Seq[String] = parameterizer.mapToValue(Range(30, 35), "t=$t, x=$x", Map(param -> sub))
		results.toList should equal (List("t=$t, x=7", "t=$t, x=7", "t=$t, x=7", "t=$t, x=7", "t=$t, x=7"))
	}

	val p1 = DollarSignParam("$abc", false)
	val x1 = Map(p1 -> DollarSignSub(p1, List("50")))

	val p2 = DollarSignParam("$...abc", false)
	val x2 = Map(p2 -> DollarSignSub(p2, List(10, 11, 12, 13, 14, 15) map (_.toString)))

	val p3 = DollarSignParam("$abc", false)
	val x3 = Map(p3 -> DollarSignSub(p1, List("10")))

	property(s"mapIndexToValue") {
		val parameterizer = new RangeParameterizer()
		parameterizer.mapToValue(Seq(0, 1, 2, 3, 4, 5), "$t+$abc", x1) should equal (List("$t+50", "$t+50", "$t+50", "$t+50", "$t+50", "$t+50"))
		parameterizer.mapToValue(Seq(0, 1, 2, 3, 4, 5), "$...abc", x2) should equal (List("10", "11", "12", "13", "14", "15"))
	}


	property("map values onto grid") {
		val parameterizer = new GridParameterizer()
		val grid = AlphanumericGrid(4, 8)
		val cells1 = Seq(grid.Point(1,1), grid.Point(1,2), grid.Point(1,3), grid.Point(1,4), grid.Point(1,5))
		val cells2 = Seq(grid.Point(1,1), grid.Point(1,2), grid.Point(2,1), grid.Point(2,2), grid.Point(3,1), grid.Point(3,2))
		parameterizer.mapToValue(cells1, "$c+$abc", x1) map (z => z._1.index -> z._2) should equal (Map(5 -> "5+50", 1 -> "1+50", 2 -> "2+50", 3 -> "3+50", 4 -> "4+50"))
		parameterizer.mapToValue(cells2, "$...abc", x2) map (z => (z._1.row, z._1.column) -> z._2) should equal (
			Map((3,1) -> "14", (1,1) -> "10", (3,2) -> "15", (2,2) -> "13", (1,2) -> "11", (2,1) -> "12")
		)
	}


}
