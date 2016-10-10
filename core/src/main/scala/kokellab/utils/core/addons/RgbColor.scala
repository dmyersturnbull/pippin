package kokellab.utils.core.addons

/**
	* A representation of an RGB color convertible between a 6-digit hex representation and unsigned short values for red, green, and blue.
	*/
case class RgbColor(red: Short, green: Short, blue: Short) {
	def toHex: String = Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue)
}

object RgbColor {
	def fromHex(string: String): RgbColor = {
		val c = (Range.inclusive(0, 4, 2) map (i => Integer.parseInt(string.toUpperCase.substring(i, i + 2), 16).toShort)).toList
		RgbColor(c(0), c(1), c(2))
	}
	def blend(colors: Seq[RgbColor]) = {
		val sum = colors.foldLeft((0, 0, 0))((blended, next) =>
			(blended._1 + next.red, blended._2 + next.green, blended._3 + next.blue) // casts to Int
		)
		RgbColor((sum._1 / colors.size).toShort, (sum._2 / colors.size).toShort, (sum._3 / colors.size).toShort)
	}
}
