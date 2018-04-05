package kokellab.utils.grammars

import breeze.stats.distributions.{RandBasis, ThreadLocalRandomGenerator}
import org.apache.commons.math3.random.MersenneTwister
import org.parboiled2.{ErrorFormatter, ParseError, Parser}

object GrammarUtils {

	def randBasis(seed: Int): RandBasis = new RandBasis(new ThreadLocalRandomGenerator(new MersenneTwister(seed)))

	def replaceCommon(expression: String): String = {
		val string = commonReplacements.foldLeft(expression) ((e, s) => e.replaceAllLiterally(s._1, s._2))
		// TODO This is terrible
		var inQuote: Boolean = false
		val flat = string flatMap {c =>
			if (c == '"') { inQuote = !inQuote }
			if (!c.isWhitespace || inQuote) Seq(c) else Seq.empty[Char]
		}
		flat.mkString("")
	}

	def wrapGrammarException[A](expression: String, parser: Parser, parse: () => A): A = try {
			parse()
		} catch {
			case e: ParseError =>
				val formatted = parser.formatError(e, new ErrorFormatter(showExpected = true, showFrameStartOffset = true, showLine = true, showPosition = true, showTraces = true))
				throw new GrammarException(s"The expression $expression could not be parsed by ${parser.getClass.getSimpleName}",
					Some(formatted), Some(e))
			case e: IllegalArgumentException =>
				throw new GrammarException(s"Error parsing expression $expression by ${parser.getClass.getSimpleName}: ${e.getMessage}", None, Some(e))
		}

	private val commonReplacements = Map("−" -> "-", "!=" -> "≠", "<=" -> "≤", ">=" -> "≥", "==" -> "=", "~=" -> "≈", "!~=" -> "≉", "∞" -> "Infinity", "infinity" -> "Infinity", "inf" -> "Infinity", "Inf" -> "Infinity")
}
