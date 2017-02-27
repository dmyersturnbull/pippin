package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class TimeSeriesGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	val randBasis = Some(GrammarUtils.randBasis(1))

	property(s"Simple") {
		TimeSeriesGrammar.build("3+$t/2", 0, 5, randBasis) should equal (Seq(3.0, 3.5, 4.0, 4.5, 5.0, 5.5))
	}

	property(s"If-else") {
		TimeSeriesGrammar.build("if $t<3: $t else: 100", 0, 5, randBasis) should equal (Seq(0.0, 1.0, 2.0, 100.0, 100.0, 100.0))
	}

	property(s"Array access") {
		TimeSeriesGrammar.build("if $t=0: 1 else: $t[0]+1", 0, 5, randBasis) should equal (Seq(1.0, 2.0, 2.0, 2.0, 2.0, 2.0))
	}

	property(s"Weird array access") {
		TimeSeriesGrammar.build("$t + $t[5]", 0, 10, randBasis) should equal (Seq(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 11.0, 12.0, 13.0, 14.0, 15.0))
	}

	property(s"Expression in array access") {
		TimeSeriesGrammar.build("$t + $t[pow($t-1, 2)]", 0, 10, randBasis) should equal (Seq(0.0, 1.0, 3.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0))
	}

	/**
	  * Not manually verified, but the result should always be the same for seed=1.
	  */
	property(s"Stabilized Brownian motion") {
		val z = TimeSeriesGrammar.build("$t[$t-1] + normR(0, 20) / pow($t+1, 1.5)", 0, 50, randBasis)
		z.last should equal (3.001968235196985)
	}

	property(s"Bounded submartingale") {
		val z = TimeSeriesGrammar.build("if $t=0: 100 else: max(0, min(200, $t[$t-1] + normR(5, 200) / ($t+1)))", 0, 100, randBasis) foreach println
	}

}
