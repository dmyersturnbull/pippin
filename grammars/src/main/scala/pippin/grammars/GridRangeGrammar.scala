package kokellab.utils.grammars

import org.parboiled2._


object GridRangeGrammar {
	def eval(input: String, nRows: Int, nColumns: Int): Seq[PointLike] = {
		val parser = new GridRangeGrammar(input.toUpperCase.replaceAllLiterally(" ", ""), AlphanumericGrid(nRows, nColumns))
		try {
			parser.run().get
		} catch {
			case e: IllegalArgumentException =>
				throw new GrammarException(s"The expression $input could not be parsed. Make sure to use the correct syntax (– vs. * vs. ...)")
			case e: ParseError =>
				throw new GrammarException(s"The expression $input could not be parsed",
					Some(parser.formatError(e, new ErrorFormatter(showExpected = true, showFrameStartOffset = true, showLine = true, showPosition = true, showTraces = true))), Some(e))
		}
	}
}

class GridRangeGrammar(val input: ParserInput, val grid: AlphanumericGrid) extends Parser {

	implicit val pointGen = (r: Int, c: Int) => grid.Point(r, c)

	def run() = multiRangeRule.run()

	def multiRangeRule: Rule1[List[grid.Point]] = rule {
		(rangeRule ~ zeroOrMore("," ~ rangeRule)) ~ EOI ~> ((first: List[grid.Point], others: Seq[List[grid.Point]]) => first ++ others.flatten)
	}

	def rangeRule: Rule1[List[grid.Point]] = rule {
		simpleRangeRule | blockRangeRule | traversalRangeRule | singleRule
	}

	def simpleRangeRule: Rule1[List[grid.Point]] = rule {
		cell ~ ("-" | "–") ~ cell ~> ((a: grid.Point, b: grid.Point) => grid.simpleRange(a, b))
	}
	def blockRangeRule: Rule1[List[grid.Point]] = rule {
		cell ~ "*" ~ cell ~> ((a: grid.Point, b: grid.Point) => grid.blockRange(a, b))
	}
	def traversalRangeRule: Rule1[List[grid.Point]] = rule {
		cell ~ ("..." | "…") ~ cell ~> ((a: grid.Point, b: grid.Point) => grid.traversalRange(a, b))
	}
	def singleRule: Rule1[List[grid.Point]] = rule {
		cell ~> ((a: grid.Point) => List(a))
	}

	def cell: Rule1[grid.Point] = rule {
		capture(oneOrMore(CharPredicate.UpperAlpha) ~ oneOrMore(CharPredicate.Digit)) ~> ((s: String) => new grid.Point(s))
	}

}

