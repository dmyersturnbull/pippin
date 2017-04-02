package squants.mass

import squants._

/**
  * Molarity.
  * @author  Douglas Myers-Turnbull
  * @since   1.3
  *
  */
final class SubstanceConcentration private (val value: Double, val unit: SubstanceConcentrationUnit)
		extends Quantity[SubstanceConcentration] {

	def dimension = SubstanceConcentration

	def *(that: Volume): ChemicalAmount = Moles(this.toMolars * that.toLitres)

	def toMolars = to(Molars)
}

object SubstanceConcentration extends Dimension[SubstanceConcentration] with BaseDimension {
	// TODO package protection?
	def apply[A](n: A, unit: SubstanceConcentrationUnit)(implicit num: Numeric[A]) = new SubstanceConcentration(num.toDouble(n), unit)
	def apply = parse _
	val name = "SubstanceConcentration"
	def primaryUnit = Molars
	def siUnit = Molars
	def units = Set(
		Molars, Decimolars, Millimolars, Micromolars, Nanomolars, Picomolars, Femtomolars,
		MolesPerLitre, MillimolesPerLitre, MicromolesPerLitre, NanomolesPerLitre, PicomolesPerLitre, FemtomolesPerLitre
	)
	def dimensionSymbol = "M"
}

trait SubstanceConcentrationUnit extends UnitOfMeasure[SubstanceConcentration] with UnitConverter {
	def apply[A](n: A)(implicit num: Numeric[A]) = SubstanceConcentration(n, this)
}

// TODO reduce boilerplate
// the unit set is a bit excessive, but I've seen some of these units (like mol/dL)

object Molars extends SubstanceConcentrationUnit with PrimaryUnit with SiBaseUnit {
	val symbol = "M"
}

object Decimolars extends SubstanceConcentrationUnit {
	val symbol = "dM"
	val conversionFactor = MetricSystem.Deci
}

object Millimolars extends SubstanceConcentrationUnit {
	val symbol = "mM"
	val conversionFactor = MetricSystem.Milli
}

object Micromolars extends SubstanceConcentrationUnit {
	val symbol = "μM"
	val conversionFactor = MetricSystem.Micro
}

object Nanomolars extends SubstanceConcentrationUnit {
	val symbol = "nM"
	val conversionFactor = MetricSystem.Nano
}

object Picomolars extends SubstanceConcentrationUnit {
	val symbol = "pM"
	val conversionFactor = MetricSystem.Pico
}

object Femtomolars extends SubstanceConcentrationUnit {
	val symbol = "fM"
	val conversionFactor = MetricSystem.Femto
}

object MolesPerLitre extends SubstanceConcentrationUnit {
	val symbol = "mol/L"
	val conversionFactor = MetricSystem.Atto / MetricSystem.Atto
}

object MillimolesPerLitre extends SubstanceConcentrationUnit {
	val symbol = "mmol/L"
	val conversionFactor = MetricSystem.Milli
}

object MicromolesPerLitre extends SubstanceConcentrationUnit {
	val symbol = "μmol/L"
	val conversionFactor = MetricSystem.Micro
}

object NanomolesPerLitre extends SubstanceConcentrationUnit {
	val symbol = "nmol/L"
	val conversionFactor = MetricSystem.Micro
}

object PicomolesPerLitre extends SubstanceConcentrationUnit {
	val symbol = "pmol/L"
	val conversionFactor = MetricSystem.Micro
}

object FemtomolesPerLitre extends SubstanceConcentrationUnit {
	val symbol = "fmol/L"
	val conversionFactor = MetricSystem.Femto
}

object SubstanceConcentrationConversions {
	lazy val molars = Molars(1)
	lazy val millimolars = Millimolars(1)
	val micromolars = Micromolars(1)
	val nanomolars = Nanomolars(1)
	val picomolars = Picomolars(1)
	val femtomolars = Femtomolars(1)

	implicit class SubstanceConcentrationConversions[A](n: A)(implicit num: Numeric[A]) {
		def molars = Molars(n)
		def millimolars = Millimolars(n)
		def micromolars = Micromolars(n)
		def nanomolars = Nanomolars(n)
		def picomolars = Picomolars(n)
		def femtomolars = Femtomolars(n)
	}

	implicit object SubstanceConcentrationNumeric extends AbstractQuantityNumeric[SubstanceConcentration](SubstanceConcentration.primaryUnit)
}