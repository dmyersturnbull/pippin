package kokellab.utils

import breeze.stats.distributions.{RandBasis, ThreadLocalRandomGenerator}
import org.apache.commons.math3.random.MersenneTwister
import org.parboiled2.{ErrorFormatter, ParseError, Parser}

package object grammars {

	class Wrapper[A, P <: Parser](parserGen: String => P, parse: P => A) {
		def apply(expression: String): A = {
			val parser = parserGen(expression)
			if (expression.isEmpty) throw new GrammarException(s"${parser.getClass.getSimpleName} could not parse: The expression is empty!")
			try {
				parse(parser)
			} catch {
				case e: ParseError =>
					val formatted = parser.formatError(e, new ErrorFormatter(showExpected = true, showFrameStartOffset = true, showLine = true, showPosition = true, showTraces = true))
					throw new GrammarException(s"The expression $expression could not be parsed by ${parser.getClass.getSimpleName}",
						Some(formatted), Some(e))
				case e: IllegalArgumentException =>
					throw new GrammarException(s"Error parsing expression $expression by ${parser.getClass.getSimpleName}: ${e.getMessage}", None, Some(e))
			}
		}
	}

	class Dewhitespacer(quoteChars: Set[Char] = Set('"'), isWhitespace: Char => Boolean = _.isWhitespace) {
		def apply(string: String): String = {
			var quotedWith: Option[Char] = None
			string flatMap { c =>
				if (isWhitespace(c) && quotedWith.isEmpty) Seq.empty else {
					if (quoteChars contains c) quotedWith = flip(quotedWith, c)
					Seq(c)
				}
			}
		}
		private def flip(q: Option[Char], c: Char): Option[Char] =
			if (q.isEmpty) Some(c)
			else if (q contains c) None
			else q
	}

	def call[A, P <: Parser](parserGen: String => P, parse: P => A, expression: String): A = {
		new Wrapper[A, P](parserGen, parse)(prepExpression(expression))
	}

	def randBasis(seed: Int): RandBasis = new RandBasis(new ThreadLocalRandomGenerator(new MersenneTwister(seed)))

	private val trans = Transliterations.dashes ++ Transliterations.math
	def prepExpression(expression: String): String = {
		// Java/Scala use "Infinity" in Double, so we do too
		val replaced = trans(expression).replace("∞", "Infinity")
		new Dewhitespacer()(replaced)
	}

}
