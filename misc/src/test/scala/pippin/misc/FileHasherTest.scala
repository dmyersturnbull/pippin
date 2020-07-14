package pippin.misc

import java.io.ByteArrayInputStream
import java.nio.file.Paths

import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class FileHasherTest extends AnyPropSpec with ScalaCheckDrivenPropertyChecks with Matchers {

	val hasher = new FileHasher("SHA-256")
	println(hasher.hash("aef46c1e5d854fcfd20ab427b599c86102b2fc6252fcdfe9be84dafdb58d6ffb".getBytes()))

	val anyByteArray: Gen[List[Byte]] = Gen.listOf(Arbitrary.arbByte.arbitrary)
	property("Correct hashes should validate") {
		forAll(anyByteArray) { (array: List[Byte]) =>
			val hex = hasher.hash(array)
			hasher.validate(array, hex)
		}
	}

	property("Incorrect hashes should not validate") {
		// this is a bit weird, but if we generated a byte array and a string hash hex, we couldn't guarantee that the hashes don't match
		forAll(anyByteArray, anyByteArray) { (arrayA: List[Byte], arrayB: List[Byte]) =>
			if (arrayA != arrayB) {
				a [ValidationFailedException] should be thrownBy {
					val hexB = hasher.hash(arrayB)
					hasher.validate(arrayA, hexB)
				}
			}
		}
	}

	property("Can hash from stream") {
		forAll(anyByteArray) { (array: List[Byte]) =>
			val hex = hasher.hash(new ByteArrayInputStream(array.toArray))
			hasher.validate(new ByteArrayInputStream(array.toArray), hex)
		}
	}

	property("Can hash from file") {
		val archive = Paths.get(this.getClass.getResource("test.7z").toURI)
		val file = Paths.get(this.getClass.getResource("expected_file_to_hash.txt").toURI)
		hasher.hash(archive) should equal ("afa411f16a1e7943cb07a57516c593384c097e8521f840b2112d2680877a2b04")
		hasher.hash(file) should equal ("aef46c1e5d854fcfd20ab427b599c86102b2fc6252fcdfe9be84dafdb58d6ffb")
	}

}
