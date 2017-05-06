package kokellab.utils.misc

import java.io.FileOutputStream
import java.nio.file.{Path, Paths}

import sys.process._
import com.typesafe.scalalogging.LazyLogging
import kokellab.utils.core.exceptions.ServiceFailedException
import org.apache.commons.compress.archivers.sevenz.{SevenZArchiveEntry, SevenZFile}

import scala.util.{Failure, Success, Try}


object SevenZipCommandLine extends LazyLogging {
	def un7zip(path: Path, writePath: Path): Try[String] = {
		val os  = new java.io.ByteArrayOutputStream
		val code = (s"7z e $path -aos -o$writePath" #> os) !
		val string = os.toString("UTF-8")
		code match {
			case 0 => Failure(throw new ServiceFailedException(s"Extracting archive $path to $writePath failed with exit code $code", serviceOutput = string))
			case _ => Success(string)
		}
	}
}


@deprecated("This code is based on apache commons-compress, which has a lot of problems that make this unreliable. See [[SevenZip.un7zip]] instead, which requires 7z available externally..", "0.1.4")
object SevenZip extends LazyLogging {

	def un7zip(path: Path, writePath: Path): Unit = {

		writePath.toFile.mkdir()
		val seven = new SevenZFile(path.toFile)

		try {
			var entry: SevenZArchiveEntry = null
			do {
				entry = seven.getNextEntry
				if (entry != null) writeEntry(seven, entry, writePath)
			} while (entry != null)
		} finally seven.close()
	}

	private def writeEntry(seven: SevenZFile, entry: SevenZArchiveEntry, writePath: Path) = {

		val outFile = Paths.get(writePath.toFile.getPath, entry.getName)
		require(!outFile.toFile.exists, s"File $outFile already exists; refusing to overwrite")

		val os = new FileOutputStream(outFile.toFile, true)

		var status: Int = 1
		try {
			while (status != -1) {
				val buffer = new Array[Byte](entry.getSize.toInt)
				status = seven.read(buffer)
				if (status != -1) os.write(buffer)
			}
		} finally os.close()

		logger.debug(s"Extracted ${entry.getName} to $outFile")
	}

}
