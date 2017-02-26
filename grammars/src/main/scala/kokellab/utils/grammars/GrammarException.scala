package kokellab.utils.grammars

import org.parboiled2.ParseError

class GrammarException(message: String, verboseMessage: Option[String] = None, underlying: Option[ParseError] = None) extends Exception(message + "\n" + verboseMessage.getOrElse(""), underlying.orNull)
