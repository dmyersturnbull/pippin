package kokellab.utils.grammars.squints

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class SiPrefixTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property("between log levels") {
		SiPrefix.between(-2, 2) should equal (List(SiPrefix.centi, SiPrefix.deci, SiPrefix.deca, SiPrefix.hecto))
	}

	property("between") {
		SiPrefix.between("micro", "hecto") should equal (List(SiPrefix.micro, SiPrefix.milli, SiPrefix.centi, SiPrefix.deci, SiPrefix.deca, SiPrefix.hecto))
	}

}
