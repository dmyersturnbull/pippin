package kokellab.utils.core.addons

import java.io.InputStreamReader
import java.nio.file.{Files, Path, Paths}
import java.util.zip.GZIPInputStream
import scala.util.Using

import com.github.tototoshi.csv.{CSVReader, CSVWriter}

import scala.io.Source

object TextUtils {

	/**
	 * Converts a string representation of an integer to a signed byte <em>minus 128</em>.
	 */
	def signByte(string: String): Byte = (pint(string) - 128).toString.toByte

	/**
	 * Parses a string to an int but stripping out any decimal points that are 0
	 */
	def pint(s: String): Int =
		new java.math.BigDecimal(s).stripTrailingZeros.toPlainString.toInt

	// unfortunately default arguments aren't allowed for overloaded methods

	/**
	 * Streams a text file, gunzipping if the filename ends with ".gz".
	 */
	def streamLines(path: String): TraversableOnce[String] = streamLines(Paths.get(path), "UTF8")
	def streamLines(path: String, encoding: String): TraversableOnce[String] = streamLines(Paths.get(path), encoding)
	def streamLines(path: Path): TraversableOnce[String] = streamLines(path, "UTF8")
	def streamLines(path: Path, encoding: String): TraversableOnce[String] = {
		val is = if (path endsWith ".gz") new GZIPInputStream(Files.newInputStream(path)) else Files.newInputStream(path)
		Source.fromInputStream(is, encoding).getLines
		//Source.fromInputStream(is, encoding) withClose (() => is.close()) getLines()
	}

	/**
	 * Opens a CSV reader, gunzipping if the filename ends with ".gz".
	 */
	def openCsvReader(path: String): CSVReader = openCsvReader(path, "UTF8")
	def openCsvReader(path: String, encoding: String): CSVReader = openCsvReader(Paths.get(path))
	def openCsvReader(path: Path): CSVReader = openCsvReader(path, "UTF8")
	def openCsvReader(path: Path, encoding: String): CSVReader = {
		val is = if (path.toString endsWith ".gz") new GZIPInputStream(Files.newInputStream(path)) else Files.newInputStream(path)
		CSVReader.open(new InputStreamReader(is, encoding))
	}

	/** Transposes a CSV file. */
	def transposeCsv(inputFile: String, outputFile: String): Unit = {
		transposeCsv(Paths.get(inputFile), Paths.get(outputFile))
	}
	def transposeCsv(inputFile: Path, outputFile: Path): Unit = {
		Using(openCsvReader(inputFile)) { reader =>
			Using(CSVWriter.open(outputFile.toFile)) { writer =>
				writer.writeAll(reader.toStream.transpose.reverse)
			}
		}
	}

}
