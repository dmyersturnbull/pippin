package pippin.misc

import java.io.InputStream
import java.nio.file.{Files, Path}
import java.security.MessageDigest
import pippin.core._

import pippin.core.{bytesToHex, hexToBytes}

class FileHasher(algorithm: String = "SHA-256") {

	val emptyHash: String = hash(Array.emptyByteArray)

	def validate(file: Path, hashHex: String): Unit = {
		val actual = hash(file)
		if (actual != hashHex) throw new ValidationFailedException(s"Validation failed: Required $hashHex but got $actual")
	}
	def validate(stream: InputStream, hashHex: String): Unit = {
		val actual = hash(stream)
		if (actual != hashHex) throw new ValidationFailedException(s"Validation failed: Required $hashHex but got $actual")
	}
	def validate(bytes: IterableOnce[Byte], hashHex: String): Unit = {
		val actual = hash(bytes)
		if (actual != hashHex) throw new ValidationFailedException(s"Validation failed: Required $hashHex but got $actual")
	}

	def hash(file: Path): String = {
		val md = MessageDigest.getInstance(algorithm)
		readFileInChunks(file, md.update)
		bytesToHex(md.digest())
	}

	def hash(stream: InputStream): String = {
		val md = MessageDigest.getInstance(algorithm)
		readStreamInChunks(stream, md.update)
		bytesToHex(md.digest())
	}

	def hash(bytes: IterableOnce[Byte]): String = {
		val md = MessageDigest.getInstance(algorithm)
		md.update(bytes.iterator.toArray)
		bytesToHex(md.digest())
	}

}

class ValidationFailedException(message: String = "The validation failed", cause: Throwable = null) extends Exception(message, cause)
