package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class IfElseStringGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"If-elif-else") {
		IfElseStringGrammar.eval("if 0=5: \"cat\" elif 12%5=2: \"dog\" else: \"potato\"") should equal (Some("dog"))
	}

}
