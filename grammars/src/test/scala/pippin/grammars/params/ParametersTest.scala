package kokellab.utils.grammars.params

import kokellab.utils.grammars.IfElseIntegerGrammar
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}

class ParametersTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	val p1 = DollarSignParam("$abc", false)
	val x1 = Map(p1 -> DollarSignSub(p1, List("50")))

	val p2 = DollarSignParam("$...abc", false)
	val x2 = Map(p2 -> DollarSignSub(p2, List(10, 11, 12, 13, 14, 15) map (_.toString)))

	val p3 = DollarSignParam("$abc", false)
	val x3 = Map(p3 -> DollarSignSub(p1, List("10")))

	property(s"Find") {
		DollarSignParams.find("ab+$22*ac$zz$aa*ca$yy0$zzz", Set()) should equal (Set("$22", "$zz", "$aa", "$yy0", "$zzz") map (s => DollarSignParam(s, false)))
		DollarSignParams.find("$abcd$efg", Set("$abcd")) should equal (Set(DollarSignParam("$abcd", true), DollarSignParam("$efg", false)))
	}

}
