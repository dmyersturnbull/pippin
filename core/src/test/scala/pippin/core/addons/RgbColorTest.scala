package kokellab.utils.core.addons

import org.scalacheck.Gen
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, TableDrivenPropertyChecks}
import org.scalatest.{Matchers, PropSpec}


class RgbColorTest extends PropSpec with GeneratorDrivenPropertyChecks with TableDrivenPropertyChecks with Matchers {

	val gen = Gen.listOfN(6, Gen.frequency((10, Gen.numChar), (6, Gen.oneOf('A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f')))) map (_.mkString)
	val things = Table("0000ff", "0000ff")

	property(s"toHex(fromHex)) should be the identity") {
		forAll(gen) { (hexColor: String) =>
			RgbColor.fromHex(hexColor).toHex should equal (hexColor.toLowerCase)
		}}

	val blendGen: Gen[(List[String], String)] = Gen.oneOf(
		(List("CC3366", "99FF00"), "B39933"),
		(List("ff0000", "0000ff", "ff00ff"), "aa00aa")
	)

	property(s"Colors should blend correctly") {
		forAll(blendGen) { case (colors: Seq[String], correct: String) =>
			if (colors.nonEmpty) {
				RgbColor.blend(colors map RgbColor.fromHex).toHex should equal (correct.toLowerCase)
			}
		}}


}
