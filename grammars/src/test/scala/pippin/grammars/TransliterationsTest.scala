package pippin.grammars

import org.scalacheck.Gen
import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import scala.collection.immutable.ListMap

class TransliterationsTest extends AnyPropSpec with ScalaCheckDrivenPropertyChecks with TableDrivenPropertyChecks with Matchers {

	property(s"Greek") {
		test(Transliterations.greek)
		Transliterations.greek.keys.last should equal ("eta")
	}
	property(s"Math") {
		test(Transliterations.math)
		Transliterations.math.idempotent should equal (true)
	}
	property(s"infinity") {
		Transliterations.math("inf") should equal ("∞")
	}
	property(s"Greek math") {
		val gm = Transliterations.math ++ Transliterations.greek
		(gm ## gm) should equal (true)
		gm("zeta !=  eta") should equal ("ζ≠η")
	}
	property(s"Noncommutative") {
		val t1 = new Transliteration { override val replacements: ListMap[String, String] = ListMap("alpha" -> "beta") }
		val t2 = new Transliteration { override val replacements: ListMap[String, String] = ListMap("beta" -> "gamma") }
		(t1 ## t2) should equal (false)
		(t2 ## t1) should equal (false)
		(t1 ## t1) should equal (true)
		(t2 ## t2) should equal (true)
		(t1 ~> t2)("alpha") should equal ("gamma")
		(t2 ~> t1)("alpha") should equal ("beta")
	}

//	property(s"Dashes") {
//		forAll(Gen.oneOf(Seq("–", "—", "‒", "−"))) { char =>
//			Transliterations.dashes(char) should equal ("-")
//		}
//	}

	private def test(trans: Transliteration): Unit = {
		trans.hazardousSubstrings.toSeq should equal (Seq.empty)
		(trans ## trans) should equal (true)
		val gen = Gen.oneOf(trans.keys.toSeq)
		// TODO: This is broken
		/*
		forAll(gen) { char =>
			trans(char) should equal (trans.replacements(char))
		}
		*/
	}

}
