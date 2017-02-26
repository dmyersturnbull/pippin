package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.prop.Tables.Table
import org.scalactic.TolerantNumerics

class GridRangeGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Simple range horizontal") {
		val parser = new AlphnumericRangeParser("A1-A4", AlphanumericGrid(5, 6))
		parser.run().get.map(_.name) should equal (List("A1", "A2", "A3", "A4"))
	}
	property(s"Simple range vertical") {
		val parser = new AlphnumericRangeParser("A1-D1", AlphanumericGrid(5, 6))
		parser.run().get.map(_.name) should equal (List("A1", "B1", "C1", "D1"))
	}
	property(s"Block range") {
		val parser = new AlphnumericRangeParser("A1*C3", AlphanumericGrid(5, 6))
		parser.run().get.map(_.name) should equal (List("A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"))
	}
	property(s"Traversal range") {
		val parser = new AlphnumericRangeParser("A1...C2", AlphanumericGrid(5, 6))
		parser.run().get.map(_.name) should equal (List("A1", "A2", "A3", "A4", "A5", "B1", "B2", "B3", "B4", "B5", "C1", "C2"))
	}

}
