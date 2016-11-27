package kokellab.utils

import java.io.File
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

import com.google.common.io.{BaseEncoding, ByteStreams}
import com.typesafe.config.{Config, ConfigException, ConfigFactory, ConfigParseOptions}
import com.typesafe.scalalogging.LazyLogging

package object core extends LazyLogging {

	def parseConfig(path: String): Config = parseConfig(new File(path))
	def parseConfig(path: File): Config =
		ConfigFactory.parseFile(path, ConfigParseOptions.defaults().setAllowMissing(false))

	def configOptionString(key: String)(implicit config: Config): Option[String] = configOption(key, config.getString)
	def configOptionInt(key: String)(implicit config: Config): Option[Int] = configOption(key, config.getInt)
	def configOptionBoolean(key: String)(implicit config: Config): Option[Boolean] = configOption(key, config.getBoolean)
	private def configOption[V](key: String, extractor: String => V): Option[V] = try {
		Some(extractor(key))
	} catch {
		case e: ConfigException.Missing => None
	}

	/** Logs an error message for any exception, then rethrows. */
	def withLoggedError[T](errorMessage: String, fn: () => T): T = withLoggedError(fn, errorMessage) // helpful for long functions

	/** Logs an error message for any exception, then rethrows. */
	def withLoggedError[T](fn: () => T, errorMessage: String): T = try {
			fn()
		} catch {
			case e: Exception =>
				logger.error(errorMessage)
				throw e
		}

	val sha1 = MessageDigest.getInstance("SHA-1")
	def bytesToHash(bytes: Traversable[Byte]): Array[Byte] = sha1.digest(bytes.toArray)
	def blobToBytes(blob: Blob): Array[Byte] = ByteStreams.toByteArray(blob.getBinaryStream)
	def bytesToHex(bytes: Traversable[Byte]) = BaseEncoding.base16().lowerCase.encode(bytes.toArray)
	def blobToHex(blob: Blob) = BaseEncoding.base16().lowerCase().encode(blobToBytes(blob))
	def bytesToBlob(bytes: Traversable[Byte]): Blob = new SerialBlob(bytes.toArray)
	def bytesToHashBlob(bytes: Traversable[Byte]): Blob = bytesToBlob(bytesToHash(bytes))
	def bytesToHashHex(bytes: Traversable[Byte]) = bytesToHex(bytesToHash(bytes))

	def floatsToBytes(values: Traversable[Float]): Traversable[Byte] =
		values flatMap (value => ByteBuffer.allocate(4).putFloat(value).array())

	def doublesToBytes(values: Traversable[Double]): Traversable[Byte] =
		values flatMap (value => ByteBuffer.allocate(8).putDouble(value).array())

	def intsToBytes(values: Traversable[Int]): Traversable[Byte] =
		values flatMap (value => ByteBuffer.allocate(4).putInt(value).array())

	def shortsToBytes(values: Traversable[Short]): Traversable[Byte] =
		values flatMap (value => ByteBuffer.allocate(2).putShort(value).array())

	def longsToBytes(values: Traversable[Long]): Traversable[Byte] =
		values flatMap (value => ByteBuffer.allocate(8).putLong(value).array())

	/**
		* Looks up all of the given keys in the map.
		* Throws an InvalidDataFormatException if conflicting values are found.
		* If the value is not found, returns None. If it is found (and is unique), returns the value.
		*/
	def uniqueLookupOption[A, B](keys: Set[A], map: Map[A, B]): Option[B] = {
		val newSet: Set[B] = keys.foldLeft(Set.empty[B])((current, next) => {
			if (map contains next) current + map(next) else current
		})
		if (newSet.size < 2) newSet.headOption
		else throw new IllegalArgumentException(s"There were ${newSet.size} conflicting values for keys {${keys.mkString(",")}}")
	}
	def uniqueLookup[A, B](keys: Set[A], map: Map[A, B]): B =
		uniqueLookupOption(keys, map) getOrElse (throw new IllegalArgumentException("No value for keys {${keys.mkString(\",\")}}"))

}
