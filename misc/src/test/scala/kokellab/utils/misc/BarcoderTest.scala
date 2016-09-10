package kokellab.utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.google.zxing.BarcodeFormat
import org.scalacheck.Gen
import org.scalacheck.Prop.BooleanOperators
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}

class BarcoderTest extends PropSpec with PropertyChecks with Matchers {

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
	def test(barcodeFormat: BarcodeFormat, dimensionsGen: Gen[(Int, Int)], stringGen: Gen[String]) = {
		property(s"Decoding an encoded string should yield the original string for ${barcodeFormat.name} codes") {
			forAll(imageFormatGen, stringGen, dimensionsGen) { (imageFormat: String, text: String, dimensions: (Int, Int)) =>
				(!text.trim.isEmpty) ==> (encodeDecode(text.toUpperCase, barcodeFormat, dimensions, imageFormat) == text.toUpperCase)
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

	test(BarcodeFormat.CODE_128, rectangularGen, genBoundedString(48, Gen.choose[Char](0,127)))

	test(BarcodeFormat.QR_CODE, squareGen, genBoundedString(4296, Gen.frequency((36, Gen.alphaNumChar), (8, Gen.oneOf('-', '.', '$', '/', '+', '%', ' ', ':')))))

}