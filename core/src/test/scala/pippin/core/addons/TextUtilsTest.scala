package pippin.core.addons

import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import scala.collection.immutable.ListMap


class TextUtilsTest extends AnyPropSpec with ScalaCheckDrivenPropertyChecks with Matchers {

	property("signByte can be out of range") {
		assertThrows[NumberFormatException] {
			TextUtils.signByte("270")
		}
	}

	property("pint can be out of range") {
		assertThrows[NumberFormatException] {
			TextUtils.signByte("270")
		}
		assertThrows[NumberFormatException] {
			TextUtils.signByte("-1")
		}
	}

	property("pint strips .0") {
		TextUtils.pint("1000.0") should equal (1000)
	}

	property("signByte strips .0") {
		TextUtils.signByte("200.0") should equal (200 - 128)
		TextUtils.signByte("0.0") should equal (0 - 128)
	}

}
