package kokellab.utils.grammars

import org.parboiled2.ParseError

class GrammarException(val message: String, val verboseMessage: Option[String] = None, val underlying: Option[Exception] = None) extends Exception(message + "\n" + verboseMessage.getOrElse(""), underlying.orNull) {

}
