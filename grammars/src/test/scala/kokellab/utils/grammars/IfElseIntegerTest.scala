package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class IfElseIntegerTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"If-elif-else") {
		IfElseIntegerGrammar.eval("if 0=5: 1 elif 12%5=2: 2 else: 3") should equal (Some(2))
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
