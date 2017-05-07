package kokellab.utils.misc

import java.io.{FileOutputStream, OutputStream, FileInputStream}
import java.nio.file.Path
import javax.imageio.ImageIO

import com.google.zxing.client.j2se.{MatrixToImageWriter, BufferedImageLuminanceSource}
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.BinaryBitmap
import com.google.zxing._

class Barcoder(val barcodeFormat: BarcodeFormat, val imageFormat: String, val width: Int, val height: Int) {

	def decode(path: Path): String = {
		val stream = new FileInputStream(path.toFile)
		try {
			decode(stream)
		} finally stream.close()
	}

	def decode(stream: java.io.InputStream): String = {
		val bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(stream))))
		new MultiFormatReader().decode(bitmap).getText
	}

	def encode(text: String, path: Path) {
		encode(text, new FileOutputStream(path.toFile))
	}

	def encode(text: String, stream: OutputStream) = {
		val matrix = new MultiFormatWriter().encode(text, barcodeFormat, width, height, null)
		MatrixToImageWriter.writeToStream(matrix, imageFormat, stream)
	}

}
