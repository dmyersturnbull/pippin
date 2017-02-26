package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.prop.Tables.Table
import org.scalactic.TolerantNumerics

class CalculatorTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	val doubleEq = TolerantNumerics.tolerantDoubleEquality(1e-4f)

	property(s"Should work, damn it") {
		(Calculator.eval("sin(min(ln(sqrt(10+100)),10)+20)*_+$row", Map("_" -> 1000, "$row" -> -10*4))) should equal (-391.42392664291674)
	}

	property(s"Crazy logical operators") {
		(Calculator.eval("bool(15)") should equal (1))
		(Calculator.eval("¬(bool(5))") should equal (0))
		(Calculator.eval("5 ∧ 5") should equal (1))
		(Calculator.eval("5 ∧ 0") should equal (0))
		(Calculator.eval("0 ∧ 0") should equal (0))
	}

	property(s"Approximately equal") {
		(Calculator.eval("10.2342342351251523525 ≈ 10.2342342351251523524×2.0/2.0") should equal (1))
		(Calculator.eval("10.234235 ≈ 10.234234×2.0/2.0") should equal (0))
	}

	property(s"Modulo") {
		(Calculator.eval("12 % 5") should equal (2))
	}

	property(s"Sign") { // TODO
		(Calculator.eval("sgn(10") should equal (1))
		(Calculator.eval("sgn(-1)") should equal (-1))
		(Calculator.eval("sgn(0") should equal (0))
	}


}
