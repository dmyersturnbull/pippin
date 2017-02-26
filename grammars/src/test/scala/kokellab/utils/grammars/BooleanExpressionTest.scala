package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.prop.Tables.Table
import org.scalactic.TolerantNumerics

class BooleanExpressionTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	val doubleEq = TolerantNumerics.tolerantDoubleEquality(1e-4f)

	property(s"Should work, damn it") {
		BooleanGrammar.eval("5*sin(50+sqrt(10)) - min(5, 10, 15, 20) = -3", 0.01) should equal (false)
		BooleanGrammar.eval("5*sin(50+sqrt(10)) - min(5, 10, 15, 20) ≈ -3.788", 0.01) should equal (true)
		BooleanGrammar.eval("5*sin(50+sqrt(10)) - min(5, 10, 15, 20) ≉ -3.788", 0.01) should equal (false)
		BooleanGrammar.eval("(15+20) < 50", 0.01) should equal (true)
		BooleanGrammar.eval("(15-20) > -50", 0.01) should equal (true)
		BooleanGrammar.eval("(15-20) > -50 and (50*2)=100", 0.01) should equal (true)
		BooleanGrammar.eval("5 = 5 = 5", 0.01) should equal (true)
		BooleanGrammar.eval("5 = 5 = 6", 0.01) should equal (false)
		BooleanGrammar.eval("5 < 6 < 7", 0.01) should equal (true)
		BooleanGrammar.eval("5 > 20 < 15", 0.01) should equal (false)
		BooleanGrammar.eval("5 < 10 = 10", 0.01) should equal (true)
		BooleanGrammar.eval("2=0 or 50=50 and ((50*2)=100)", 0.01) should equal (true)
		BooleanGrammar.eval("(2=0 or 50=50) and ((50*2)=100)", 0.01) should equal (true)
		BooleanGrammar.eval("0=1 or 2<5 and 5=5", 0.01) should equal (true) // tests precedence
		BooleanGrammar.eval("sqrt(10) < mean(10, 50, 60) nand 50=50", 0.01) should equal (false)
		BooleanGrammar.eval("sqrt(10) < mean(10, 50, 60) and 50=50", 0.01) should equal (true)
		BooleanGrammar.eval("sqrt(10) >= mean(10, 50, 60) or 50=50", 0.01) should equal (true)
		BooleanGrammar.eval("sqrt(10) >= mean(10, 50, 60) nor 50=50", 0.01) should equal (false)
	}

}
