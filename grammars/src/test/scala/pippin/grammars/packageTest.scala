package pippin.grammars

import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

class packageTest extends AnyPropSpec with TableDrivenPropertyChecks with Matchers {

	property("dewhitespacer, only double quotes") {
		val de = new Dewhitespacer()
		de("abc def \"xxx yyy\" zzz") should equal ("abcdef\"xxx yyy\"zzz")
		de("abc def \"xxx yyy\"") should equal ("abcdef\"xxx yyy\"")
		de("\"xxx yyy\"") should equal ("\"xxx yyy\"")
		de("xxx yyy") should equal ("xxxyyy")
		de("") should equal ("")
		de(" ") should equal ("")
		de("\"\"") should equal ("\"\"")
		de("\"") should equal ("\"")
		de("	") should equal ("")
		de("\"	\"") should equal ("\"	\"")
	}

	property("dewhitespacer, two kinds of quotes") {
		val de = new Dewhitespacer(quoteChars = Set('\'', '"'))
		de("abc 'xxx yyy' def \"zzz www\" m") should equal ("abc'xxx yyy'def\"zzz www\"m")
		de("'abc def'") should equal ("'abc def'")
		de("\"'abc def' xxx\"") should equal ("\"'abc def' xxx\"")
		de("\"'abc def \" xxx' xxx\"") should equal ("\"'abc def \"xxx' xxx\"")
	}

	property("dewhitespacer, weird options") {
		val de = new Dewhitespacer(quoteChars = Set('/', '|'), isWhitespace = _ == '.')
		de("a \"b . c\" d") should equal ("a \"b  c\" d")
		de("a |b . c| d") should equal ("a |b . c| d")
	}

	property("prep") {
		prepExpression("inf") should equal ("Infinity")
		prepExpression("123–inf abc") should equal ("123-Infinityabc")
	}

}
