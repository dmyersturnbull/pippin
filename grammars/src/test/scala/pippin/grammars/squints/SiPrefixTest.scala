package pippin.grammars.squints

import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

class SiPrefixTest extends AnyPropSpec with TableDrivenPropertyChecks with Matchers {

	property("between log levels") {
		SiPrefix.between(-2, 2) should equal (List(SiPrefix.centi, SiPrefix.deci, SiPrefix.deca, SiPrefix.hecto))
	}

	property("between") {
		SiPrefix.between("micro", "hecto") should equal (List(SiPrefix.micro, SiPrefix.milli, SiPrefix.centi, SiPrefix.deci, SiPrefix.deca, SiPrefix.hecto))
	}

}
