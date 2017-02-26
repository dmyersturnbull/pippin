package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class IfElseGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"If only") {
		IfElseGrammar.eval("if 50>5: 0") should equal (Some(0))
		IfElseGrammar.eval("if 50<5: 0") should equal (None)
		IfElseGrammar.eval("if 50<5 or 500=500: 0") should equal (Some(0))
	}

	property(s"If-else") {
		IfElseGrammar.eval("if 50>5: 0 else: 500") should equal (Some(0))
		IfElseGrammar.eval("if 50<5: 0 else: 500") should equal (Some(500))
	}

	property(s"If-elif-else") {
		IfElseGrammar.eval("if 0=5: 0 elif 10=10: 1 else: 2") should equal (Some(1))
		IfElseGrammar.eval("if 0=5: 0 elif 10=-5: 1 else: 2") should equal (Some(2))
		IfElseGrammar.eval("if 0=5: 0 elif 10=-5: 1 elif 40=50: 2 elif 50=60: 3 else: 4") should equal (Some(4))
		IfElseGrammar.eval("if 0=5: 0 elif 10=-5: 1 elif 40=50: 2 elif 50=50: 3 else: 4") should equal (Some(3))
		IfElseGrammar.eval("if 0=5: 0 elif 10=-5: 1 elif 40=50: 2 elif 50=50 and 40=50: 3 else: 4") should equal (Some(4))
	}

}
