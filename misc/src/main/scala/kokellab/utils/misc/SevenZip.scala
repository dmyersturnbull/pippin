package kokellab.utils.misc

import java.io.FileOutputStream
import java.nio.file.{Path, Paths}

import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.compress.archivers.sevenz.{SevenZArchiveEntry, SevenZFile}


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
