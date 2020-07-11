package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class IfElseStringGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"If-elif-else") {
		IfElseStringGrammar.eval("if 0=5: \"cat\" elif 12%5=2: \"dog\" else: \"potato\"") should equal (Some("dog"))
	}

	property(s"Nested if-else") {
		IfElseStringGrammar.eval("""if 2>1: if 1<2: "5" else: "500"""") should equal (Some("5"))
		IfElseStringGrammar.eval("""if 2>1: if 1>2: "5" else: "500"""") should equal (Some("500"))
		IfElseStringGrammar.eval("""if 2>1: if 1>2: "5" else: "999" else: "500"""") should equal (Some("999"))
		IfElseStringGrammar.eval("""if 1>2: if 1>2: "5" else: "999" else: "500"""") should equal (Some("500"))
		IfElseStringGrammar.eval("""if 2>1: if 1>2: "5" elif 2>1: "234" else: "999" else: "500"""") should equal (Some("234"))
		IfElseStringGrammar.eval("""if 2>1: if 1>2: "5" elif 1>2: "234" else: "999" else: "500"""") should equal (Some("999"))
		IfElseStringGrammar.eval("""if 1>2: if 1>2: "5" elif 1>2: "234" else: "999" elif 2>1: "777" else: "500"""") should equal (Some("777"))
	}

	property(s"Trivial") {
		IfElseStringGrammar.eval("\"50+500\"") should equal (Some("50+500"))
	}

}
