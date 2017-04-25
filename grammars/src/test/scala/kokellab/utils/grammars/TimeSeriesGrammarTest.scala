package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class TimeSeriesGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	import scala.reflect.runtime.universe._
	import scala.reflect._
	val randBasis = Some(GrammarUtils.randBasis(1))

	property(s"Constant") {
		TimeSeriesGrammar.build[Double]("5", 0, 5, d=>d, randBasis).toSeq should equal (Seq(5, 5, 5, 5, 5))
	}

	property(s"Just t") {
		TimeSeriesGrammar.build[Double]("$t", 0, 5, d=>d, randBasis).toSeq should equal (Seq(0.0, 1.0, 2.0, 3.0, 4.0))
	}

	property(s"Simple") {
		TimeSeriesGrammar.build[Double]("3+$t/2", 0, 5, d=>d, randBasis).toSeq should equal (Seq(3.0, 3.5, 4.0, 4.5, 5.0))
	}

	property(s"If-else") {
		TimeSeriesGrammar.build[Double]("if $t<3: $t else: 100", 0, 5, d=>d, randBasis).toSeq should equal (Seq(0.0, 1.0, 2.0, 100.0, 100.0))
	}

	property(s"Array access") {
		TimeSeriesGrammar.build[Double]("if $t=0: 1 else: $t[0]+1", 0, 5, d=>d, randBasis).toSeq should equal (Seq(1.0, 2.0, 2.0, 2.0, 2.0))
	}

	property(s"Weird array access") {
		TimeSeriesGrammar.build[Double]("$t + $t[5]", 0, 10, d=>d, randBasis).toSeq should equal (Seq(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 11.0, 12.0, 13.0, 14.0))
	}

	property(s"Expression in array access") {
		TimeSeriesGrammar.build[Double]("if $t>0: $t + $t[$t-1] else: 0", 0, 10, d=>d, randBasis).toSeq should equal (Seq(0.0, 1.0, 3.0, 6.0, 10.0, 15.0, 21.0, 28.0, 36.0, 45.0))
	}

	property(s"Expression in array access with evaluation interval") {
		TimeSeriesGrammar.build[Double]("if $t>5: $t + $t[$t-6] else: 0  @ 5", 0, 40, d=>d, randBasis).toSeq should equal (Seq(0.0, 0.0, 10.0, 15.0, 30.0, 40.0, 60.0, 75.0))
	}

	/**
	  * Not manually verified, but the result should always be the same for seed=1.
	  */
	property(s"Stabilized Brownian motion") {
		val z = TimeSeriesGrammar.build[Double]("$t[$t-1] + normR(0, 20) / pow($t+1, 1.5)", 0, 50, d=>d, randBasis, outOfBoundsValue = 0).toSeq
		z.last should equal (2.9539912532197454)
	}

	property(s"Bounded submartingale") {
		val z = TimeSeriesGrammar.build[Double]("if $t=0: 100 else: max(0, min(200, $t[$t-1] + normR(5, 200) / ($t+1)))", 0, 100, d=>d, randBasis)// foreach println
	}

	property(s"Out-of-bounds") {
		assert(TimeSeriesGrammar.build[Double]("$t[$t-2]", 0, 5, d=>d, randBasis).toSeq forall (_.isNaN))
	}

//	property(s"Stress test 1") {
//		TimeSeriesGrammar.build("if $t=0: 100 else: max(0, min(200, $t[$t-1] + normR(5, 200) / ($t+1)))", 0, 1209600000, randomBasis = randBasis) foreach println
//	}

//	property(s"Stress test 2") {
//		TimeSeriesGrammar.build[Byte]("$t  @ 40", 0, 1000*60*60*20, _.toByte, randBasis)
//	}

}
