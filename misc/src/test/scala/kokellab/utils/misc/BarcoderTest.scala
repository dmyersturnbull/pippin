package kokellab.utils.misc

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.google.zxing.BarcodeFormat

import org.scalacheck.Gen
import org.scalatest._
import prop._


class BarcoderTest extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {

	val imageFormatGen = Gen.oneOf("png", "jpg", "gif")

	val rectangularGen: Gen[(Int, Int)] = for {
		width <- Gen.choose(20, 100)
		height <- Gen.choose(20, 100)
	} yield (width, height)

	val squareGen: Gen[(Int, Int)] = for {
		size <- Gen.choose(20, 100)
	} yield (size, size)

	val code39And93Gen: Gen[String] = Utils.genBoundedString(48, Gen.frequency((36, Gen.alphaNumChar), (7, Gen.oneOf('-', '.', '$', '/', '+', '%', ' '))))

//		def test(barcodeFormat: BarcodeFormat, dimensionsGen: Gen[(Int, Int)], stringGen: Gen[String]) = {
//			property(s"Decoding an encoded string should yield the original string for ${barcodeFormat.name} codes") {
//				forAll(imageFormatGen, stringGen, dimensionsGen) { (imageFormat: String, text: String, dimensions: (Int, Int)) =>
//					println("SDFDSF")
//					whenever (!text.trim.isEmpty) {
//						encodeDecode(text.toUpperCase, barcodeFormat, dimensions, imageFormat) should equal(text.toUpperCase)
//					}
//				}
//			}
//		}

		property(s"Decoding an encoded string should yield the original string for ${BarcodeFormat.CODE_39.name} codes") {
			forAll(imageFormatGen, Utils.genBoundedString(48, Gen.choose[Char](0, 127)), rectangularGen) { (imageFormat: String, text: String, dimensions: (Int, Int)) =>
				println("SDFDSF")
				whenever (!text.trim.isEmpty) {
					Utils.encodeDecode(text.toUpperCase, BarcodeFormat.CODE_39, dimensions, imageFormat) should equal(text.toUpperCase)
				}
			}
	}
//		test(BarcodeFormat.CODE_39, rectangularGen, code39And93Gen)
//		test(BarcodeFormat.CODE_93, rectangularGen, code39And93Gen)
//
//		test(BarcodeFormat.CODE_128, rectangularGen, genBoundedString(48, Gen.choose[Char](0, 127)))
//
//		test(BarcodeFormat.QR_CODE, squareGen, genBoundedString(4296, Gen.frequency((36, Gen.alphaNumChar), (8, Gen.oneOf('-', '.', '$', '/', '+', '%', ' ', ':')))))

//	val fullGen: Gen[(BarcodeFormat, Gen[(Int, Int)], Gen[String])] = Gen.oneOf(Seq(
//		(BarcodeFormat.CODE_39, rectangularGen, code39And93Gen)
//	))

}

object Utils {

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

}