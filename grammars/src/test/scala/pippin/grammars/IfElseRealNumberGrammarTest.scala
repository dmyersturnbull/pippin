package pippin.grammars

import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

class IfElseRealNumberGrammarTest extends AnyPropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"If only") {
		IfElseRealNumberGrammar.eval("if 50>5: 0") should equal (Some(0))
		IfElseRealNumberGrammar.eval("if 50<5: 0") should equal (None)
		IfElseRealNumberGrammar.eval("if 50<5 or 500=500: 0") should equal (Some(0))
	}

	property(s"If-else") {
		IfElseRealNumberGrammar.eval("if 50>5: 0 else: 500") should equal (Some(0))
		IfElseRealNumberGrammar.eval("if 50<5: 0 else: 500") should equal (Some(500))
	}

	property(s"Nested if-else") {
		IfElseRealNumberGrammar.eval("if 2<1: 50 else: if 5<10: 100 else: 150") should equal (Some(100))
		IfElseRealNumberGrammar.eval("if 2<1: 50 else: if 5>10: 100 else: 150") should equal (Some(150))
		IfElseRealNumberGrammar.eval("if 2>1: if 1<2: 5 else: 500") should equal (Some(5))
		IfElseRealNumberGrammar.eval("if 2>1: if 1>2: 5 else: 500") should equal (Some(500))
		IfElseRealNumberGrammar.eval("if 2>1: if 1>2: 5 else: 999 else: 500") should equal (Some(999))
		IfElseRealNumberGrammar.eval("if 1>2: if 1>2: 5 else: 999 else: 500") should equal (Some(500))
		IfElseRealNumberGrammar.eval("if 2>1: if 1>2: 5 elif 2>1: 234 else: 999 else: 500") should equal (Some(234))
		IfElseRealNumberGrammar.eval("if 2>1: if 1>2: 5 elif 1>2: 234 else: 999 else: 500") should equal (Some(999))
		IfElseRealNumberGrammar.eval("if 1>2: if 1>2: 5 elif 1>2: 234 else: 999 elif 2>1: 777 else: 500") should equal (Some(777))
	}

	property(s"If-elif-else") {
		IfElseRealNumberGrammar.eval("if 0=5: 0 elif 10=10: 1 else: 2") should equal (Some(1))
		IfElseRealNumberGrammar.eval("if 0=5: 0 elif 10=-5: 1 else: 2") should equal (Some(2))
		IfElseRealNumberGrammar.eval("if 0=5: 0 elif 10=-5: 1 elif 40=50: 2 elif 50=60: 3 else: 4") should equal (Some(4))
		IfElseRealNumberGrammar.eval("if 0=5: 0 elif 10=-5: 1 elif 40=50: 2 elif 50=50: 3 else: 4") should equal (Some(3))
		IfElseRealNumberGrammar.eval("if 0=5: 0 elif 10=-5: 1 elif 40=50: 2 elif 50=50 and 40=50: 3 else: 4") should equal (Some(4))
	}

}
