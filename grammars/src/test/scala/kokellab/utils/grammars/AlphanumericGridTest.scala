package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class AlphanumericGridTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property("Index, rows, and columns") {
		val grid = AlphanumericGrid(4, 6)
		import grid.Point
		Point(1, 1).index should equal (1)
		Point(1, 2).index should equal (2)
		Point(1, 6).index should equal (6)
		Point(2, 1).index should equal (7)
		Point(4, 6).index should equal (4*6)
		Point(1, 1) should equal (new Point(1))
		Point(1, 2) should equal (new Point(2))
		Point(1, 6) should equal (new Point(6))
		Point(2, 1) should equal (new Point(7))
	}

}
