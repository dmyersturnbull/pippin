package kokellab.utils.grammars

import java.util.regex.Pattern

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class ParametersTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Find") {
		DollarSignParams.find("ab+$22*ac$zz$aa*ca$yy0$zzz", Set()) should equal (Set("$22", "$zz", "$aa", "$yy0", "$zzz") map (s => DollarSignParam(s, false)))
		DollarSignParams.find("$abcd$efg", Set("$abcd")) should equal (Set(DollarSignParam("$abcd", true), DollarSignParam("$efg", false)))
	}

	property(s"Parse") {
		val results = Parameterizations.parse(
			"""
	$a = 55
	$b = "one hundred"
 	$...c = ["this", "is", "an", "array"]
	""", Set(DollarSignParam("$a", false), DollarSignParam("$b", false), DollarSignParam("$...c", false)), Map("$...c" -> 4))
//	results foreach println
	}

	property(s"mapIndexToValue") {
		Parameterizations.mapIndexToValue(Seq(0, 1, 2, 3, 4, 5), "$t+$abc", "$abc = 500") should equal (Left("$t+500"))
		Parameterizations.mapIndexToValue(Seq(0, 1, 2, 3, 4, 5), "$...abc", "$...abc = [10, 11, 12, 13, 14, 15]") should equal (Right(Seq("10", "11", "12", "13", "14", "15")))
	}

	property("map values onto grid") {
		Parameterizations.mapValuesOntoGrid("A1-A5", "$c+$abc", "$abc = 50", 4, 8) map (z => z._1.index -> z._2.toDouble) should equal (Map(1 -> 51, 2 -> 52, 3 -> 53, 4 -> 54, 5 -> 55))
		Parameterizations.mapValuesOntoGrid("A1*C2", "$abc + 10*$r + $c", "$abc = 10", 4, 8) map (z => (z._1.row, z._1.column) -> z._2.toDouble) should equal (
			Map((1,1) -> 21, (1,2)-> 22, (2,1) -> 31, (2,2)-> 32, (3,1) -> 41, (3,2)-> 42)
		)
	}

}
