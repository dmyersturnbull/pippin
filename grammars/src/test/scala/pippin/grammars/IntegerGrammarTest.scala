package kokellab.utils.grammars

import org.scalactic.TolerantNumerics
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class IntegerGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Should work, damn it") {
		IntegerGrammar.eval("5*pow(2,5) - min(5, 10, 15, 20)") should equal (155)
	}

}
