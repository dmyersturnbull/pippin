package kokellab.utils

import java.io.{File, FileInputStream, InputStreamReader}
import java.sql.Timestamp
import java.util.Calendar
import java.util.zip.GZIPInputStream

import com.github.tototoshi.csv.{CSVReader, CSVWriter}
import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions}
import com.typesafe.scalalogging.LazyLogging
import resource._

import scala.io.Source

package object core extends LazyLogging {

	def parseConfig(path: String): Config = parseConfig(new File(path))
	def parseConfig(path: File): Config =
		ConfigFactory.parseFile(path, ConfigParseOptions.defaults().setAllowMissing(false))

	def timestamp() = new Timestamp(Calendar.getInstance().getTime.getTime)

	def withLoggedError[T](errorMessage: String, fn: () => T): T = withLoggedError(fn, errorMessage) // helpful for long functions
	def withLoggedError[T](fn: () => T, errorMessage: String): T = try {
			fn()
		} catch {
			case e: Exception =>
				logger.error(errorMessage)
				throw e
		}


	def streamLines(path: String, encoding: String = "UTF8"): TraversableOnce[String] = {
		val is = if (path endsWith ".gz") new GZIPInputStream(new FileInputStream(path)) else new FileInputStream(path)
		Source.fromInputStream(is, encoding) withClose (() => is.close()) getLines()
	}

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
