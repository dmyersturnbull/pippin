package kokellab.utils.grammars

import org.parboiled2._

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
		for (i <- a.index to b.index if i / nColumns > 0 && i % nColumns > 0) yield gen(i / nColumns, i % nColumns)
	}.toList
}

trait PointLike {
	def row: Int
	def column: Int
	def index: Int
	def name: String
	override def toString: String = s"($row, $column)[i=$index]"
}

object AlphanumericRangeGrammar {
	def eval(input: String, nRows: Int, nColumns: Int): Seq[PointLike] = {
		val parser = new AlphnumericRangeGrammar(input, AlphanumericGrid(nRows, nColumns))
		try {
			parser.run().get
		} catch {
			case e: ParseError =>
				throw new GrammarException(s"The expression $input could not be parsed",
					Some(parser.formatError(e, new ErrorFormatter(showExpected = true, showFrameStartOffset = true, showLine = true, showPosition = true, showTraces = true))), Some(e))
		}
	}
}

class AlphnumericRangeGrammar(val input: ParserInput, val grid: AlphanumericGrid) extends Parser {

	implicit val pointGen = (r: Int, c: Int) => grid.Point(r, c)

	def run() = rangeRule.run()

	def rangeRule: Rule1[List[grid.Point]] = rule {
		(simpleRangeRule | blockRangeRule | traversalRangeRule) ~ EOI
	}

	def simpleRangeRule: Rule1[List[grid.Point]] = rule {
		cell ~ ("-" | "–") ~ cell ~> ((a: grid.Point, b: grid.Point) => grid.simpleRange(a, b))
	}
	def blockRangeRule: Rule1[List[grid.Point]] = rule {
		cell ~ "*" ~ cell ~> ((a: grid.Point, b: grid.Point) => grid.blockRange(a, b))
	}
	def traversalRangeRule: Rule1[List[grid.Point]] = rule {
		cell ~ "..." ~ cell ~> ((a: grid.Point, b: grid.Point) => grid.traversalRange(a, b))
	}

	def cell: Rule1[grid.Point] = rule {
		capture(oneOrMore(CharPredicate.UpperAlpha) ~ oneOrMore(CharPredicate.Digit)) ~> ((s: String) => new grid.Point(s))
	}

}


case class AlphanumericGrid(nRows: Int, nColumns: Int) extends GridLike {

	require(nRows > 0)
	require(nColumns > 0)

	case class Point(row: Int, column: Int) extends PointLike {
		require(row > 0 && row <= nRows && column > 0 && column <= nColumns, s"($row, $column) is out of bounds of ($nRows, $nColumns) grid")
		def this(s: String) = this(stringToRow(s), stringToColumn(s))
		def this(i: Int) = this(i / nColumns, i % nColumns)
		val index = nColumns*row + column
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
