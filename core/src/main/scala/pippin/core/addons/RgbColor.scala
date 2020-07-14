package pippin.core.addons

/**
	* A representation of an RGB color convertible between a 6-digit hex representation and unsigned short values for red, green, and blue.
	*/
case class RgbColor(red: Short, green: Short, blue: Short) {
	def toHex: String = (Seq(red, green, blue) map (c => Integer.toHexString(c).reverse.padTo(2, '0').reverse)).mkString
}

object RgbColor {

	def fromHex(string: String): RgbColor = {
		val c = (Range.inclusive(0, 4, 2) map (i => {
			Integer.parseInt(string.toUpperCase.substring(i, i + 2), 16).toShort
		})).toList
		RgbColor(c(0), c(1), c(2))
	}

	def blend(colors: Seq[RgbColor]) = {
		if (colors.isEmpty) throw new IllegalArgumentException("Could not blend from zero colors")
		val sum = colors.foldLeft(Seq(0, 0, 0))((blended, next) =>
			Seq(blended(0) + next.red, blended(1) + next.green, blended(2) + next.blue) // casts to Int
		)
		val z = sum map (c => Math.round(c / colors.size.toFloat).toShort)
		RgbColor(z(0), z(1), z(2))
	}
}
