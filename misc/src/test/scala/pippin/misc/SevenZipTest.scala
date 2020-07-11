package kokellab.utils.misc

import java.io.File
import java.nio.file.{Files, Paths}

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

import scala.io.Source

class SevenZipTest extends PropSpec with Matchers {

	val archive = Paths.get(this.getClass.getResource("test.7z").toURI)
	val correctFile = Paths.get(this.getClass.getResource("expected_file_to_hash.txt").toURI)
	val newFile = Paths.get(archive.getParent.toFile.getPath, "file_to_hash.txt")
	newFile.toFile.deleteOnExit()
	property("Should be able to unzip correctly") {
		SevenZip.un7zip(archive, archive.getParent)
		val goodStuff = Source.fromFile(correctFile.toFile).mkString
		val newStuff = Source.fromFile(newFile.toFile).mkString
		goodStuff should equal (newStuff)
	}

}
