package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class TimeRangeGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Split range") {
		TimeRangeGrammar.evalMillis("3ms - 5m") should equal ((3,300000))
		TimeRangeGrammar.evalMillis("1 - 1") should equal ((1, 1))
		TimeRangeGrammar.evalMillis("1mh - 1mh") should equal ((3600, 3600))
		TimeRangeGrammar.evalMillis("3 - 5") should equal ((3,5))
		TimeRangeGrammar.evalMillis("3-5") should equal ((3,5))
		TimeRangeGrammar.evalMillis("0-70000") should equal ((0,70000))
		TimeRangeGrammar.evalMillis("0-70s") should equal ((0,70000))
		TimeRangeGrammar.evalMillis("3 - 5") should equal ((3,5))
		TimeRangeGrammar.evalMillis("3 – 5") should equal ((3,5)) // en dash allowed
	}

}
