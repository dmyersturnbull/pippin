package kokellab.utils.grammars

import java.util.regex.Pattern
//import com.google.common.base.Objects
import java.util.Objects


class GridException(message: String) extends GrammarException(message)

class RangeIsNotSimpleException(message: String) extends GridException(message)

class FlippedCoordinatesException(message: String) extends GridException(message)


trait GridLike {

	def nRows: Int
	def nColumns: Int

	def at[P <: PointLike](row: Int, column: Int)(implicit gen: (Int, Int) => P): P = gen(row, column)
	def at[P <: PointLike](i: Int)(implicit gen: (Int, Int) => P): P = gen((i-1) / nColumns + 1, (i-1) % nColumns + 1)

	def cells[P <: PointLike]()(implicit gen: (Int, Int) => P): Seq[PointLike] = {
		for (i <- 0 to nRows * nColumns) yield {
			gen((i-1) / nColumns + 1, (i-1) % nColumns + 1)
		}}

	def simpleRange[P <: PointLike](a: P, b: P)(implicit gen: (Int, Int) => P) = {
		if (a.row == b.row) {
			indCheck(a.column <= b.column, s"Column for $a > column for $b")
			for (c <- a.column to b.column) yield gen(a.row, c)
		} else if (a.column == b.column) {
			indCheck(a.row <= b.row, s"Row for $a > column for $b")
			for (r <- a.row to b.row) yield gen(r, a.column)
		} else {
			throw new RangeIsNotSimpleException(s"$a and $b do not form a simple range. When specifying more than a single row or single column, use * to denote a block and ... to denote a traversal.")
		}
	}.toList

	def blockRange[P <: PointLike](a: P, b: P)(implicit gen: (Int, Int) => P) = {
		indCheck(a.row <= b.row && a.column <= b.column, s"Row or column for $a comes after row or column for $b in block range")
		for (r <- a.row to b.row; c <- a.column to b.column) yield gen(r, c)
	}.toList

	def traversalRange[P <: PointLike](a: P, b: P)(implicit gen: (Int, Int) => P) = {
		indCheck(a.index <= b.index, s"Point $a > $b in traversal range")
		for (i <- a.index to b.index) yield {
			gen((i-1) / nColumns + 1, (i-1) % nColumns + 1)
		}
	}.toList

	protected def indCheck(check: Boolean, message: String): Unit = {
		if (!check) throw new FlippedCoordinatesException(message)
	}
	protected def grammarCheck(check: Boolean, message: String): Unit = {
		if (!check) throw new GridException(message)
	}
}

/**
  * Rows, columns, and row–columns indices are all 1-indexed.
  */
case class AlphanumericGrid(nRows: Int, nColumns: Int) extends GridLike {

	grammarCheck(nRows > 0, s"There are 0 rows in ($nRows, $nColumns)")
	grammarCheck(nColumns > 0, s"There are 0 columns in ($nRows, $nColumns)")

	case class Point(row: Int, column: Int) extends PointLike {
		indCheck(row > 0 && row <= nRows && column > 0 && column <= nColumns, s"($row, $column) is out of bounds of ($nRows, $nColumns) grid")
		def this(s: String) = this(stringToRow(s), stringToColumn(s))
		def this(i: Int) = this((i-1) / nColumns + 1, (i-1) % nColumns + 1)
		val index = nColumns*(row-1) + column
		override val name: String = (row + 'A' - 1).toChar + column.toString
	}

	private val pattern = Pattern.compile("([A-Z]+)([0-9]+)")
	private def stringToRow(s: String): Int = {
		val m = pattern.matcher(s)
		grammarCheck(m.matches(), s"String $s does not define a row")
		(m.group(1).zipWithIndex map { case (c: Char, i: Int) =>
			26*i + (c - 'A' + 1)
		}).sum
	}
	private def stringToColumn(s: String): Int = {
		val m = pattern.matcher(s)
		grammarCheck(m.matches(), s"String $s does not define a column")
		m.group(2).toInt
	}

}

trait PointLike {
	def row: Int
	def column: Int
	def index: Int
	def name: String
	override def toString: String = s"($row, $column)[i=$index]"
	override def hashCode: Int = Objects.hashCode(row, column, index, name)
	def canEqual(o: Any): Boolean = o.isInstanceOf[PointLike]
	override def equals(o: Any): Boolean = o match {
		case v: PointLike => (v canEqual this) && v.row == row && v.column == column && v.index == index && v.name == name
		case _ => false
	}
}
