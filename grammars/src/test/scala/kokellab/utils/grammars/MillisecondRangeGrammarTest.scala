package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class MillisecondRangeGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Split range") {
		MillisecondRangeGrammar.eval("3ms - 5m") should equal ((3,300000))
		MillisecondRangeGrammar.eval("3 - 5") should equal ((3,5))
		MillisecondRangeGrammar.eval("3-5") should equal ((3,5))
		MillisecondRangeGrammar.eval("0-70000") should equal ((0,70000))
		MillisecondRangeGrammar.eval("0-70s") should equal ((0,70000))
		MillisecondRangeGrammar.eval("3 - 5") should equal ((3,5))
	}

}
