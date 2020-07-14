package pippin.grammars.params

import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec


class TextToParameterizationTest extends AnyPropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Parse") {
		val results = new TextToParameterization().parse(
			"""
	$a = 55
	$b = "one hundred"
 	$...c = [this, is, an, array]
  $zzz = "this isn't defined"
	""", Set(DollarSignParam("$a", false), DollarSignParam("$b", false), DollarSignParam("$...c", false)), Map("$...c" -> 4))
		(results map (r => (r._1.name, r._2.values))) should equal (Map(
			"$a" -> List("55"),
			"$b" -> List("\"one hundred\""),
			"$...c" -> List("this", "is", "an", "array")
		))
	}

	property(s"Parse empty") {
		val results = new TextToParameterization().parse("", Set(DollarSignParam("$a", false), DollarSignParam("$b", false), DollarSignParam("$...c", false)), Map("$...c" -> 4))
		(results map (r => (r._1.name, r._2.values))) should equal (Map.empty)
	}

	property(s"Parse with multiline array") {
		val results = new TextToParameterization().parse(
			"""
 	$...c = [
 	this
 	is
 	an
 	"array"
 	]
$...d = [
	this,
 is,
     another,
	 		array,
	ok?
 ]
	""", Set(DollarSignParam("$...d", false), DollarSignParam("$...c", false)), Map("$...c" -> 4, "$...d" -> 5))
		(results map (r => (r._1.name, r._2.values))) should equal (Map(
			"$...c" -> List("this", "is", "an", "\"array\""),
			"$...d" -> List("this", "is", "another", "array", "ok?")
		))
	}

}
