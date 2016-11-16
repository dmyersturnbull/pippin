package kokellab.utils.misc

import java.io.File
import javax.sound.sampled.AudioSystem
import org.tritonus.share.sampled.file.TAudioFileFormat
import kokellab.utils.core.bytesToHash

case class AudioFile(bytes: Array[Byte], sha1: Array[Byte], nSeconds: Double)

object AudioFile {

	def read(file: File): AudioFile = {
		val bytes = audioFileBytes(file)
		AudioFile(bytes, bytesToHash(bytes), audioFileNSeconds(file))
	}

	private def audioFileBytes(file: File): Array[Byte] = {
		val is = AudioSystem.getAudioInputStream(file)
		try {
			(Stream.continually(is.read) takeWhile (_ != -1) map (_.toByte)).toArray
		} finally is.close()
	}

	private def audioFileNSeconds(file: File): Double = {
		val properties = AudioSystem.getAudioFileFormat(file).asInstanceOf[TAudioFileFormat].properties
		val microseconds = properties.get("duration").asInstanceOf[Long]
		microseconds.toDouble * 1e-6
	}

}