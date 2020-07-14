package pippin.misc

import scala.language.postfixOps
import java.nio.file.{Path, Paths}

import sys.process._
import com.typesafe.scalalogging.LazyLogging
import pippin.core.exceptions.ServiceFailedException

import scala.util.{Failure, Success, Try}


object SevenZip extends LazyLogging {
	def un7zip(path: Path, writePath: Path, command: String = "7za"): Try[String] = {
		val os  = new java.io.ByteArrayOutputStream
		val code = (s"$command x $path -o$writePath" #> os) !
		val string = os.toString("UTF-8")
		code match {
			case 0 => Success(string)
			case i => Failure(throw new ServiceFailedException(s"Extracting archive $path to $writePath failed with exit code $code", serviceOutput = Some(string)))
		}
	}
}


