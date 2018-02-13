package kokellab.utils.grammars

import breeze.stats.distributions.{RandBasis, ThreadLocalRandomGenerator}
import org.apache.commons.math3.random.MersenneTwister
import org.parboiled2.{ErrorFormatter, ParseError, Parser}

object GrammarUtils {

	private val trans = Transliterations.dashes ++ Transliterations.math

	def randBasis(seed: Int): RandBasis = new RandBasis(new ThreadLocalRandomGenerator(new MersenneTwister(seed)))

	def replaceCommon(expression: String): String =
		trans(expression).replace("∞", "Infinity")

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

	private val commonReplacements = Map(" " -> "", "−" -> "-", "!=" -> "≠", "<=" -> "≤", ">=" -> "≥", "==" -> "=", "~=" -> "≈", "!~=" -> "≉", "∞" -> "Infinity", "infinity" -> "Infinity", "inf" -> "Infinity", "Inf" -> "Infinity")
}
