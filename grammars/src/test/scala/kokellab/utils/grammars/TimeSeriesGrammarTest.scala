package kokellab.utils.grammars

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks

class TimeSeriesGrammarTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	import scala.reflect.runtime.universe._
	import scala.reflect._

	def randBasis = {
		Some(GrammarUtils.randBasis(1))
	}

	property(s"Empty") {
		a [GrammarException] should be thrownBy {
			TimeSeriesGrammar.build[Double]("", 0, 5, d => d, randBasis).toSeq should equal(Seq(5, 5, 5, 5, 5))
		}
	}

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

	property(s"Bug #41") {
		// the evaluation interval is exactly equal to the end, which is okay
		TimeSeriesGrammar.build[Double]("if $t=0: 127 else: $t[$t-1] @ 9", 0, 9, d => d, randBasis).toSeq
		// the interval is greater than the stop
		a [EvaluationIntervalException] should be thrownBy {
			TimeSeriesGrammar.build[Double]("if $t=0: 127 else: $t[$t-1] @ 10", 0, 9, d => d, randBasis).toSeq
		}
		// now let's start a bit later
		a [EvaluationIntervalException] should be thrownBy {
			TimeSeriesGrammar.build[Double]("if $t=0: 127 else: $t[$t-1] @ 9", 5, 9, d => d, randBasis).toSeq
		}
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

	property(s"Using integers") {
		TimeSeriesGrammar.build[Int]("if $t>0: $t[$t-1]+2", 0, 6, d=>d.toInt, randBasis).toSeq should equal (Seq(0, 2, 4, 6, 8, 10))
	}

	property(s"Out-of-bounds") {
		assert(TimeSeriesGrammar.build[Double]("$t[$t-2]", 0, 5, d=>d, randBasis).toSeq forall (_.isNaN))
	}

	property(s"Out-of-bounds with integers") {
		// this is weird behavior, but let's at least be consistent about it
		// Scala converts NaN to 0 with .toInt
		TimeSeriesGrammar.build[Int]("$t[$t-2]", 0, 5, d=>d.toInt, randBasis).toSeq should equal (Seq(0, 0, 0, 0, 0))
	}

	property(s"Negative and fractional evaluation intervals") {
		a [EvaluationIntervalException] should be thrownBy {
			val results = TimeSeriesGrammar.build[Double]("5 @ -1", 0, 5, d=>d, randBasis).toSeq
			System.err.println(results)
		}
		a [EvaluationIntervalException] should be thrownBy {
			val results = TimeSeriesGrammar.build[Double]("5 @ 1.0", 0, 5, d=>d, randBasis).toSeq
			System.err.println(results)
		}
	}

	property(s"Evaluation interval with modulo") {
		TimeSeriesGrammar.build[Double]("$t%2 @ 1", 0, 5, d=>d, randBasis).toSeq should equal (Seq(0.0, 1.0, 0.0, 1.0, 0.0))
		TimeSeriesGrammar.build[Double]("$t%2 @ 2", 0, 5, d=>d, randBasis).toSeq should equal (Seq(0.0, 0.0, 0.0, 0.0, 0.0))
		TimeSeriesGrammar.build[Double]("$t%6 @ 3", 0, 5, d=>d, randBasis).toSeq should equal (Seq(0.0, 0.0, 0.0, 3.0, 3.0))
		a [EvaluationIntervalException] should be thrownBy {
			val results = TimeSeriesGrammar.build[Double]("$t%2 @ 3", 0, 5, d=>d, randBasis).toSeq
			System.err.println(results)
		}
		a [EvaluationIntervalException] should be thrownBy {
			val results = TimeSeriesGrammar.build[Double]("$t%3 @ 6", 0, 5, d=>d, randBasis).toSeq
			System.err.println(results)
		}
	}

//	property(s"Stress test 1") {
//		TimeSeriesGrammar.build("if $t=0: 100 else: max(0, min(200, $t[$t-1] + normR(5, 200) / ($t+1)))", 0, 1209600000, randomBasis = randBasis) foreach println
//	}

//	property(s"Stress test 2") {
//		TimeSeriesGrammar.build[Byte]("$t  @ 40", 0, 1000*60*60*20, _.toByte, randBasis)
//	}

}
