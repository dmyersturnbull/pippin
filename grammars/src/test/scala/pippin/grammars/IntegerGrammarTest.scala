package pippin.grammars

import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

class IntegerGrammarTest extends AnyPropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Should work, damn it") {
		IntegerGrammar.eval("5*pow(2,5) - min(5, 10, 15, 20)") should equal (155)
	}

}
