package kokellab.utils.grammars.squints

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}
import squants.MetricSystem
import squants.mass.{ChemicalAmount, Molars, Moles, SubstanceConcentration}
import squants.space.Inches
import squants.time.{Hours, Seconds, Time}

class SquintsTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

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

}
