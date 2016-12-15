package kokellab.utils.core.addons

import java.io.{FileInputStream, InputStreamReader}
import java.util.zip.GZIPInputStream

import com.github.tototoshi.csv.{CSVReader, CSVWriter}
import resource._

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

	/**
		* Streams a text file, gunzipping if the filename ends with ".gz".
		*/
	def streamLines(path: String, encoding: String = "UTF8"): TraversableOnce[String] = {
		val is = if (path endsWith ".gz") new GZIPInputStream(new FileInputStream(path)) else new FileInputStream(path)
		Source.fromInputStream(is, encoding) withClose (() => is.close()) getLines()
	}
	/**
		* Opens a CSV reader, gunzipping if the filename ends with ".gz".
		*/
	def openCsvReader(path: String, encoding: String = "UTF8"): CSVReader = {
		val is = if (path endsWith ".gz") new GZIPInputStream(new FileInputStream(path)) else new FileInputStream(path)
		CSVReader.open(new InputStreamReader(is, encoding))
	}

	/** Transposes a CSV file. */
	def transposeCsv(inputFile: String, outputFile: String) {
		for (
			reader <- managed(openCsvReader(inputFile));
			writer <- managed(CSVWriter.open(outputFile))
		) writer.writeAll(reader.toStream.transpose.reverse)
	}

}
