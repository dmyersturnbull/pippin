package kokellab.utils.grammers

import java.util.regex.Pattern

trait GridLike {
	def nRows: Int
	def nColumns: Int
}

object AlphanumericGrid {
}

case class AlphanumericGrid(nRows: Int, nColumns: Int) extends GridLike {

	require(nRows > 0)
	require(nColumns > 0)

	case class Point(row: Int, column: Int) extends PointLike {
		require(row > 0 && row <= nRows && column > 0 && column <= nColumns)
		def this(s: String) = this(stringToRow(s), stringToRow(s))
		def this(i: Int) = this(i / nColumns, i % nColumns)
		val index = nColumns*row + column
		override val toString: String = (row + 'A' - 1).toChar + column.toString
	}

	private val pattern = Pattern.compile("([A-Z])(\\d+)")
	private def stringToRow(s: String): Int = {
		val m = pattern.matcher(s)
		require(m.matches())
		m.group(1).charAt(0) - 'A' + 1
	}
	private def stringToColumn(s: String): Int = {
		val m = pattern.matcher(s)
		require(m.matches())
		m.group(2).toInt
	}

}

case class NinetySixWellPlate() extends AlphanumericGrid(8, 12)

trait PointLike {
	def row: Int
	def column: Int
	def index: Int
}
