package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.prop.Tables.Table
import org.scalactic.TolerantNumerics

class RealNumberGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	val doubleEq = TolerantNumerics.tolerantDoubleEquality(1e-4f)

	property(s"Should work, damn it") {
		RealNumberGrammar.eval("5*sin(50+sqrt(10)) - min(5, 10, 15, 20) + mean(10, 20)") should equal (11.211799096658236)
	}

	property("Exp notation") {
		RealNumberGrammar.eval("-5.0E10 + (2E10) + (1E-2) + (3E+5)") should equal (-2.999969999999E10)
	}

	property("NaN") {
		assert(RealNumberGrammar.eval("-NaN*5").isNaN)
	}
	property("Inf") {
		assert(RealNumberGrammar.eval("-∞").isNegInfinity)
	}
}
