package pippin.misc

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.google.zxing.BarcodeFormat
import org.scalacheck.Gen
import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import scala.collection.immutable.ListMap

class BarcoderTest extends AnyPropSpec with ScalaCheckDrivenPropertyChecks with Matchers {

		def fakeEncodeDecode(text: String, barcodeFormat: BarcodeFormat, dimensions: (Int, Int), imageFormat: String): String =
			if (text.isEmpty) text else encodeDecode(text.toUpperCase, barcodeFormat, dimensions, imageFormat)

		def genBoundedList[T](maxSize: Int, gen: Gen[T]): Gen[List[T]] =
			Gen.choose(0, maxSize) flatMap (size => Gen.listOfN(size, gen))

		def genBoundedString(maxSize: Int, gen: Gen[Char]): Gen[String] =
			Gen.choose(0, maxSize) flatMap (size => Gen.listOfN(size, gen) map (_.mkString))

		def encodeDecode(text: String, codeFormat: BarcodeFormat, dimensions: (Int, Int), imageFormat: String): String = {
			val barcoder = new Barcoder(codeFormat, imageFormat, dimensions._1, dimensions._2)
			val os = new ByteArrayOutputStream()
			barcoder.encode(text, os)
			val is = new ByteArrayInputStream(os.toByteArray)
			barcoder.decode(is)
		}

		val imageFormatGen = Gen.oneOf("png", "jpg", "gif")
		def test(barcodeFormat: BarcodeFormat, dimensionsGen: Gen[(Int, Int)], stringGen: Gen[String]): Unit = {
			property(s"Decoding an encoded string should yield the original string for ${barcodeFormat.name} codes") {
				forAll(imageFormatGen, stringGen, dimensionsGen) { (imageFormat: String, text: String, dimensions: (Int, Int)) =>
					fakeEncodeDecode(text, barcodeFormat, dimensions, imageFormat) should equal (text.toUpperCase)
				}
			}
		}

		val rectangularGen: Gen[(Int, Int)] = for {
			width <- Gen.choose(20, 100)
			height <- Gen.choose(20, 100)
		} yield (width, height)

		val squareGen: Gen[(Int, Int)] = for {
			size <- Gen.choose(20, 100)
		} yield (size, size)

		val code39And93Gen: Gen[String] = genBoundedString(48, Gen.frequency((36, Gen.alphaNumChar), (7, Gen.oneOf('-', '.', '$', '/', '+', '%', ' '))))
		test(BarcodeFormat.CODE_39, rectangularGen, code39And93Gen)
		test(BarcodeFormat.CODE_93, rectangularGen, code39And93Gen)

		// TODO this fails due to https://github.com/zxing/zxing/issues/716
		// there's nothing I can do now
//		test(BarcodeFormat.CODE_128, rectangularGen, genBoundedString(48, Gen.choose[Char](0x20, 127)))

		// TODO QR codes break; also not my fault
//		test(BarcodeFormat.QR_CODE, squareGen, genBoundedString(4296, Gen.frequency((36, Gen.alphaNumChar), (8, Gen.oneOf('-', '.', '$', '/', '+', '%', ' ', ':')))))

}