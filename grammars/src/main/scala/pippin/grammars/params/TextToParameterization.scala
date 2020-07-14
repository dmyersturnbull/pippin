package pippin.grammars.params

import java.util.regex.Pattern

import pippin.grammars.GrammarException

class TextToParameterizationExpression(message: String, verboseMessage: Option[String] = None, underlying: Option[Exception] = None) extends GrammarException(message, verboseMessage, underlying)

/**
  * This weird code parses a block of text into a map of substitutions. The format (ignoring whitespace) is:
  * <code>
  *     \$abc = "xyz"
  *     \$def = ["x", "y", "z"]
  *     \$ghi = [
  *     "x"
  *     "y"
  *     "z"
  *     ]
  * </code>
  */
class TextToParameterization(
		val failOnUnexpected: Boolean = false,
		val quote: Boolean = false,
		val pattern: Pattern = Pattern.compile(".*")
) {

	private final val multiLineArrayPattern = """=\s*\[\s*\n([^\]]*)\n\s*\]""".r

	def parse(
			originalText: String,
			params: Set[DollarSignParam],
			lengths: Map[String, Int]
	): Map[DollarSignParam, DollarSignSub] = {

		val text = multiLineArrayPattern.replaceAllIn(originalText, m => "= [" + middle(m.group(1)) + "]")

		val mapped: Seq[DollarSignSub] = text.split("\n") flatMap { s =>
			if (s.trim.isEmpty) None else {
				if (!(s contains '=')) throw new TextToParameterizationExpression(s"Non-empty line $s does not contain an equals sign")
				val key = s.substring(0, s.indexOf('=')).trim
				val value = s.substring(s.indexOf('=') + 1).trim
				if (!(params exists (p => p.name == key))) {
					if (failOnUnexpected) throw new TextToParameterizationExpression(s"A parameter in: \n[\n$text\n]\n ... is not defined")
					else None
				} else {
					Some(mapIt(params, lengths, key, value))
				}
			}
		}
		(mapped groupBy (_.key) map { case (key, values) => key -> values.head}).toMap
	}

	private def mapIt(params: Set[DollarSignParam], lengths: Map[String, Int], key: String, value: String): DollarSignSub = {
		val param = params.find(p => p.name == key).get
		if (param.isList && (value startsWith "/") && (value endsWith "/")) {
			DollarSignSub(param, List(value))
		} else if (param.isList) {
			if (!(value startsWith "[") || !(value endsWith "]")) throw new TextToParameterizationExpression(s"The parameter ${param.name} is a list type, but '$value' is not enclosed in []")
			val zs = value.substring(1, value.length - 1) split "," map (_.trim) map quoteIfNeeded
			assert(lengths contains param.name, s"Length is missing for parameter $param")
			if (lengths(param.name) != zs.length) throw new TextToParameterizationExpression(s"The parameter ${param.name} has length ${lengths(param.name)}, but the value '$value' has length ${zs.length}")
			if (!(zs forall (z => pattern.matcher(z).matches))) throw new TextToParameterizationExpression(s"The value '$value' does not match the required pattern ${pattern.pattern} (for parameter ${param.name}")
			DollarSignSub(param, zs.toList)
		} else {
			//if (!pattern.matcher(value).matches()) throw new TextToParameterizationExpression(s"The value '$value' does not match the required pattern ${pattern.pattern} (for parameter ${param.name}")
			DollarSignSub(param, List(quoteIfNeeded(value)))
		}
	}

	private def quoteIfNeeded(s: String): String = if (quote && !s.startsWith("\"") && !s.endsWith("\"")) "\"" + s + "\"" else s

	private def middle(s: String): String = {
		s.split("\n") map (_.trim) map {l =>
			if (l.endsWith(",")) l.substring(0, l.length - 1) else l
		}
	}.mkString(", ")

}
