package kokellab.utils.grammars

import java.util.regex.Pattern

trait GridLike {

	def nRows: Int
	def nColumns: Int

	def simpleRange[P <: PointLike](a: P, b: P)(implicit gen: (Int, Int) => P) = {
		if (a.row == b.row) {
			require(a.column <= b.column, "Column for $a > column for $b")
			for (c <- a.column to b.column) yield gen(a.row, c)
		} else if (a.column == b.column) {
			require(a.row <= b.row, "Row for $a > column for $b")
			for (r <- a.row to b.row) yield gen(r, a.column)
		} else {
			throw new IllegalArgumentException(s"$a and $b do not form a simple range")
		}
	}.toList

	def blockRange[P <: PointLike](a: P, b: P)(implicit gen: (Int, Int) => P) = {
		require(a.row <= b.row && a.column <= b.column, "Row or column for $a comes after row or column for $b in block range")
		for (r <- a.row to b.row; c <- a.column to b.column) yield gen(r, c)
	}.toList

	def traversalRange[P <: PointLike](a: P, b: P)(implicit gen: (Int, Int) => P) = {
		require(a.index <= b.index, "Point $a > $b in traversal range")
		for (i <- a.index to nColumns + b.index if i / nColumns > 0 && i % nColumns > 0) yield gen(i / nColumns, i % nColumns)
	}.toList
}

case class AlphanumericGrid(nRows: Int, nColumns: Int) extends GridLike {

	require(nRows > 0)
	require(nColumns > 0)

	case class Point(row: Int, column: Int) extends PointLike {
		require(row > 0 && row <= nRows && column > 0 && column <= nColumns, s"($row, $column) is out of bounds of ($nRows, $nColumns) grid")
		def this(s: String) = this(stringToRow(s), stringToColumn(s))
		def this(i: Int) = this(i / nColumns, i % nColumns)
		val index = nColumns*(row-1) + column
		override val name: String = (row + 'A' - 1).toChar + column.toString
	}

	private val pattern = Pattern.compile("([A-Z]+)([0-9]+)")
	private def stringToRow(s: String): Int = {
		val m = pattern.matcher(s)
		require(m.matches())
		(m.group(1).zipWithIndex map { case (c: Char, i: Int) =>
			26*i + (c - 'A' + 1)
		}).sum
	}
	private def stringToColumn(s: String): Int = {
		val m = pattern.matcher(s)
		require(m.matches())
		m.group(2).toInt
	}

}

trait PointLike {
	def row: Int
	def column: Int
	def index: Int
	def name: String
	override def toString: String = s"($row, $column)[i=$index]"
}
