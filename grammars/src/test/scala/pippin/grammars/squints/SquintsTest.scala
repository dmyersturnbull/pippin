package pippin.grammars.squints

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.time.Millisecond
import squants.MetricSystem
import squants.mass.{ChemicalAmount, Molars, Moles, SubstanceConcentration}
import squants.space.Inches
import squants.time.{Hours, Milliseconds, Seconds, Time}
import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

class SquintsTest extends AnyPropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Molarity") {
		val squinter = new Squinter(SubstanceConcentration.apply(_), Set("M", "mol/L"))
		squinter("5 Gmol/L") should equal (Molars(5) * MetricSystem.Giga)
	}

	property(s"Time") {
		val squinter = new Squinter(Time.apply(_), Set("s", "m", "h", "d")) // this has a weird consequence of allowing picohours and femtodays
		squinter("5 Gs") should equal (Seconds(5) * MetricSystem.Giga)
		squinter("5E6 ms") should equal (Seconds(5000))
		squinter("5.23E-02 µh") should equal (Hours(5.23E-8))
	}

	property("Time without a prefix or units") {
		val squinter: Squinter[Time] = new Squinter(Time.apply(_), Set("s", "m", "h"), numberParser = _.toInt, defaultUnit = Some("ms"), numberPattern = Squinter.nonnegativeDoublePattern,
			siPrefixes = List(SiPrefix.milli, SiPrefix.kilo))
		squinter("7s") should equal (Seconds(7))
		squinter("7 s") should equal (Seconds(7))
		squinter("7") should equal (Milliseconds(7))
	}

}
