package pippin.misc

import java.nio.file.Paths
import java.nio.file.Files

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.propspec.AnyPropSpec

import scala.io.Source

/*
class SevenZipTest extends AnyPropSpec with Matchers {

	private val archive = Paths.get(this.getClass.getResource("test.7z").toURI)
	private val correctFile = Paths.get(this.getClass.getResource("expected_file_to_hash.txt").toURI)
	private val newFile = Paths.get(archive.getParent.toFile.getPath, "file_to_hash.txt")
	newFile.toFile.deleteOnExit()
	property("Should be able to unzip correctly") {
		SevenZip.un7zip(archive, archive.getParent)
		val goodStuff = Files.readString(correctFile)
		val newStuff = Files.readString(newFile)
		goodStuff should equal (newStuff)
	}

}
*/
