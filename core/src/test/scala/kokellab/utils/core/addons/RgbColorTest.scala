package kokellab.utils.core.addons
import java.util.regex.Pattern

import org.scalacheck.Gen
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, PropertyChecks}
import org.scalatest.{Matchers, PropSpec}


class RgbColorTest extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {


	val gen = Gen.listOfN(6, Gen.alphaNumChar) map (_.mkString)

//	val pattern = Pattern.compile("[0-9A-Za-z]{6}")

	property(s"toHex(fromHex)) should be the identity") {
		forAll(gen) { (hexColor: String) => {
			// TODO I have NO idea why this sometimes isn't 6
			// https://github.com/rickynils/scalacheck/issues/304
			println(hexColor.length)
			(RgbColor.fromHex(hexColor).toHex) should equal (hexColor)
		}
	}}


}
