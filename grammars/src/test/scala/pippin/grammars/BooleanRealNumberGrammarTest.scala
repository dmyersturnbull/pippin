package pippin.grammars

import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec
import org.scalactic.TolerantNumerics

class BooleanRealNumberGrammarTest extends AnyPropSpec with TableDrivenPropertyChecks with Matchers {

	val doubleEq = TolerantNumerics.tolerantDoubleEquality(1e-4f)

	property(s"Should work, damn it") {
		BooleanRealNumberGrammar.eval("5*sin(50+sqrt(10)) - min(5, 10, 15, 20) = -3", 0.01) should equal (false)
		BooleanRealNumberGrammar.eval("5*sin(50+sqrt(10)) - min(5, 10, 15, 20) ≈ -3.788", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("5*sin(50+sqrt(10)) - min(5, 10, 15, 20) ≉ -3.788", 0.01) should equal (false)
		BooleanRealNumberGrammar.eval("(15+20) < 50", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("(15-20) > -50", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("(15-20) > -50 and (50*2)=100", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("5 = 5 = 5", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("5 = 5 = 6", 0.01) should equal (false)
		BooleanRealNumberGrammar.eval("5 < 6 < 7", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("5 > 20 < 15", 0.01) should equal (false)
		BooleanRealNumberGrammar.eval("5 < 10 = 10", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("2=0 or 50=50 and ((50*2)=100)", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("(2=0 or 50=50) and ((50*2)=100)", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("0=1 or 2<5 and 5=5", 0.01) should equal (true) // tests precedence
		BooleanRealNumberGrammar.eval("sqrt(10) < mean(10, 50, 60) nand 50=50", 0.01) should equal (false)
		BooleanRealNumberGrammar.eval("sqrt(10) < mean(10, 50, 60) and 50=50", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("sqrt(10) >= mean(10, 50, 60) or 50=50", 0.01) should equal (true)
		BooleanRealNumberGrammar.eval("sqrt(10) >= mean(10, 50, 60) nor 50=50", 0.01) should equal (false)
	}

}
