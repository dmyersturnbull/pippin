package pippin.grammars

import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

class IfElseIntegerTest extends AnyPropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"If-elif-else") {
		IfElseIntegerGrammar.eval("if 0=5: 1 elif 12%5=2: 2 else: 3") should equal (Some(2))
	}

	property("Nested if-else") {
		IfElseIntegerGrammar.eval("if 2<1: 50 else: if 5<10: 100 else: 150") should equal (Some(100))
		IfElseIntegerGrammar.eval("if 2<1: 50 else: if 5>10: 100 else: 150") should equal (Some(150))
		IfElseIntegerGrammar.eval("if 2>1: if 1<2: 5 else: 500") should equal (Some(5))
		IfElseIntegerGrammar.eval("if 2>1: if 1>2: 5 else: 500") should equal (Some(500))
		IfElseIntegerGrammar.eval("if 2>1: if 1>2: 5 else: 999 else: 500") should equal (Some(999))
		IfElseIntegerGrammar.eval("if 1>2: if 1>2: 5 else: 999 else: 500") should equal (Some(500))
		IfElseIntegerGrammar.eval("if 2>1: if 1>2: 5 elif 2>1: 234 else: 999 else: 500") should equal (Some(234))
		IfElseIntegerGrammar.eval("if 2>1: if 1>2: 5 elif 1>2: 234 else: 999 else: 500") should equal (Some(999))
		IfElseIntegerGrammar.eval("if 1>2: if 1>2: 5 elif 1>2: 234 else: 999 elif 2>1: 777 else: 500") should equal (Some(777))
	}

	property(s"Should fail on fraction") {
		a [GrammarException] should be thrownBy {
			IfElseIntegerGrammar.eval("if 0=5: 1.5 elif 12%5=2: 2 else: 3")
		}
		a [GrammarException] should be thrownBy {
			IfElseIntegerGrammar.eval("sqrt(5)")
		}
	}

}
