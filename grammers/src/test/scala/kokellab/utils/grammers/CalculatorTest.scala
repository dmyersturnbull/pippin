package kokellab.utils.grammers

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.prop.Tables.Table
import org.scalactic.TolerantNumerics

class CalculatorTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	val doubleEq = TolerantNumerics.tolerantDoubleEquality(1e-4f)

	property(s"Should work, damn it") {
		(Calculator.eval("sin(min(ln(sqrt(10+100)),10)+20)*_+$row", Map("_" -> 1000, "$row" -> -10*4))) should equal (-391.42392664291674)
	}


}
