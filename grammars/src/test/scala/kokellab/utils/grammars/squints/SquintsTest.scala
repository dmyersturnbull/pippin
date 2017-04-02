package kokellab.utils.grammars.squints

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, PropSpec}
import squants.MetricSystem
import squants.mass.{ChemicalAmount, Molars, Moles, SubstanceConcentration}
import squants.space.Inches
import squants.time.{Seconds, Time}

class SquintsTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	property(s"Molarity") {
		val squinter = new Squinter(SubstanceConcentration.apply(_))
		squinter("5 Gmol/L") should equal (Molars(5) * MetricSystem.Giga)
	}

	property(s"Seconds") {
		val squinter = new Squinter(Time.apply(_))
		squinter("5 Gs") should equal (Seconds(5) * MetricSystem.Giga)
	}
}
