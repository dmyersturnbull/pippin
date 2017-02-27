package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.prop.Tables.Table
import org.scalactic.TolerantNumerics

class SimpleCalculatorTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	val doubleEq = TolerantNumerics.tolerantDoubleEquality(1e-4f)

	property(s"Should work, damn it") {
		RealNumberGrammar.eval("5*sin(50+sqrt(10)) - min(5, 10, 15, 20) + mean(10, 20)") should equal (11.211799096658236)
	}

}
