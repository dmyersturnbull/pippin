
package kokellab.utils.grammers

import org.parboiled2._

import scala.math._

/**
  * Evaluates math expressions, including bit arithmetic.
  * Special notes:
  * <ul>
  *     <li><strong>Unary minus is currently not supported (TODO). Use 0 - expression instead.</strong></li>
  *     <li>Bitwise xor is denoted with a backtick to avoid confusion with anything else. The caret character is not used.</li>
  *     <li>"At most" and "at least" are denoted only using ≤ and ≥.</li>
  *     <li>Bitwise operations always round to integers. Beware!</li>
  *     <li>Bitwise and comparison operators have lower precedence than addition and subtraction.</li>
  * <ul>
  *
  * Ideas and snippets were written by petarvlahu on 2014-09-02: https://github.com/indyscala/Calc/blob/master/src/main/scala/org/indyscala/parboiled/Calc.scala.
  */
object Calculator {

	def eval(expression: String, substitutions: Map[String, Double] = Map.empty): Double = {
		// prepend 0 because unary minus isn't supported
		val substituted = substitutions.foldLeft(expression) ((e, s) => e.replaceAllLiterally(s._1, if (s._2 < 0) "(0" + s._2.toString + ")" else s._2.toString))
		eval(new Calculator(substituted).line.run().get)
	}

	private def eval(expression: Expression): Double =
		expression match {

			case Value(v) => v.toDouble

			case Addition(a, b) => eval(a) + eval(b)
			case Subtraction(a, b) => eval(a) - eval(b)
			case Multiplication(a, b) => eval(a) * eval(b)
			case Division(a, b) => eval(a) / eval(b)
			case Mod(a, b) => eval(a) % eval(b)

			case Sqrt(e) => math.sqrt(eval(e))
			case Power(a, b) => math.pow(eval(a), eval(b))
			case Exp(e) => math.exp(eval(e))
			case Ln(e) => math.log(eval(e))
			case Log10(e) => math.log10(eval(e))
			case Log2(e) => math.log10(eval(e)) / log10(2)

			case Equals(a, b) => if (eval(a) == eval(b)) 1.0 else 0.0
			case NotEquals(a, b) => if (eval(a) != eval(b)) 1.0 else 0.0
			case LessThan(a, b) => if (eval(a) < eval(b)) 1.0 else 0.0
			case MoreThan(a, b) => if (eval(a) > eval(b)) 1.0 else 0.0
			case Sign(a, b) => (eval(a) compareTo eval(b)).toDouble
			case AtLeast(a, b) => if (eval(a) >= eval(b)) 1.0 else 0.0
			case AtMost(a, b) => if (eval(a) <= eval(b)) 1.0 else 0.0

			case And(a, b) => (eval(a).toInt & eval(b).toInt).toDouble
			case Or(a, b) => (eval(a).toInt | eval(b).toInt).toDouble
			case Xor(a, b) => (eval(a).toInt ^ eval(b).toInt).toDouble

			case Abs(e) => math.abs(eval(e))
			case Round(e) => math.round(eval(e))
			case Ceil(e) => math.ceil(eval(e))
			case Floor(e) => math.floor(eval(e))
			case Min(a, b) => math.min(eval(a), eval(b))
			case Max(a, b) => math.max(eval(a), eval(b))

			case Rand(a, b) => math.random * (eval(b) - eval(a)) - eval(a)

			case Sin(e) => math.sin(eval(e))
			case Cos(e) => math.cos(eval(e))
			case Asin(e) => math.asin(eval(e))
			case Acos(e) => math.acos(eval(e))
			case Sinh(e) => math.sinh(eval(e))
			case Cosh(e) => math.cosh(eval(e))
			case Tan(e) => math.tan(eval(e))
			case Atan(e) => math.atan(eval(e))
		}

	sealed trait Expression

	case class Value(value: String) extends Expression

	case class Addition(lhs: Expression, rhs: Expression) extends Expression
	case class Subtraction(lhs: Expression, rhs: Expression) extends Expression
	case class Multiplication(lhs: Expression, rhs: Expression) extends Expression
	case class Division(lhs: Expression, rhs: Expression) extends Expression
	case class Mod(lhs: Expression, rhs: Expression) extends Expression
	case class Power(lhs: Expression, rhs: Expression) extends Expression

	case class Sqrt(e: Expression) extends Expression
	case class Exp(e: Expression) extends Expression
	case class Ln(e: Expression) extends Expression
	case class Log10(e: Expression) extends Expression
	case class Log2(e: Expression) extends Expression

	case class Equals(lhs: Expression, rhs: Expression) extends Expression
	case class NotEquals(lhs: Expression, rhs: Expression) extends Expression
	case class LessThan(lhs: Expression, rhs: Expression) extends Expression
	case class MoreThan(lhs: Expression, rhs: Expression) extends Expression
	case class Sign(lhs: Expression, rhs: Expression) extends Expression
	case class AtLeast(lhs: Expression, rhs: Expression) extends Expression
	case class AtMost(lhs: Expression, rhs: Expression) extends Expression

	case class And(lhs: Expression, rhs: Expression) extends Expression
	case class Or(lhs: Expression, rhs: Expression) extends Expression
	case class Xor(lhs: Expression, rhs: Expression) extends Expression

	case class Abs(e: Expression) extends Expression
	case class Round(e: Expression) extends Expression
	case class Ceil(e: Expression) extends Expression
	case class Floor(e: Expression) extends Expression
	case class Min(lhs: Expression, rhs: Expression) extends Expression
	case class Max(lhs: Expression, rhs: Expression) extends Expression

	case class Rand(a: Expression, b: Expression) extends Expression

	case class Sin(e: Expression) extends Expression
	case class Cos(e: Expression) extends Expression
	case class Asin(e: Expression) extends Expression
	case class Acos(e: Expression) extends Expression
	case class Sinh(e: Expression) extends Expression
	case class Cosh(e: Expression) extends Expression
	case class Tan(e: Expression) extends Expression
	case class Atan(e: Expression) extends Expression
}

class Calculator(val input: ParserInput) extends Parser {
	import Calculator._

	def line = rule { expression ~ EOI }

	def expression: Rule1[Expression] = rule {
		terms ~ zeroOrMore(
			'~' ~ terms ~> Sign |
			'=' ~ terms ~> Equals |
			'≠' ~ terms ~> NotEquals |
			'<' ~ terms ~> LessThan |
			'>' ~ terms ~> MoreThan |
			'≥' ~ terms ~> AtLeast |
			'≤' ~ terms ~> AtMost |
			'&' ~ terms ~> And |
			'|' ~ terms ~> Or |
			'`' ~ terms ~> Xor
		)
	}

	def terms: Rule1[Expression] = rule {
		term ~ zeroOrMore(
			'+' ~ term ~> Addition |
			'-' ~ term ~> Subtraction
		)
	}

	def term = rule {
		factor ~ zeroOrMore(
			'*' ~ factor ~> Multiplication |
			'/' ~ factor ~> Division
		)
	}

	def factor = rule { parentheses | sqrt | pow | exp  | ln | abs | round | ceil | floor | min | max | sin | cos | sinh | cosh | asin | acos | tan | atan | number }

	def parentheses = rule { '(' ~ terms ~ ')' }

	def number = rule { capture(digits) ~> Value }

	def digits = rule {
		zeroOrMore(CharPredicate.Digit) ~ optional(fraction)
	}

	def fraction = rule { ch('.') ~ oneOrMore(CharPredicate.Digit) }

	def sqrt = rule { "sqrt(" ~ terms ~ ')' ~> Sqrt }
	def pow = rule { "pow(" ~ terms ~ ',' ~ terms ~ ')' ~> Power }
	def exp = rule { "exp(" ~ terms ~ ')' ~> Exp }
	def ln = rule { "ln(" ~ terms ~ ')' ~> Ln }

	def abs = rule { "abs(" ~ terms ~ ')' ~> Abs }
	def round = rule { "round(" ~ terms ~ ')' ~> Round }
	def ceil = rule { "ceil(" ~ terms ~ ')' ~> Ceil }
	def floor = rule { "floor(" ~ terms ~ ')' ~> Floor }
	def min = rule { "min(" ~ terms ~ ',' ~ terms ~ ')' ~> Min }
	def max = rule { "max(" ~ terms ~ ',' ~ terms ~ ')' ~> Max }

	def rand = rule { "rand(" ~ terms ~ ',' ~ terms ~ ')' ~> Rand }

	def sin = rule { "sin(" ~ terms ~ ')' ~> Sin }
	def cos = rule { "cos(" ~ terms ~ ')' ~> Cos }
	def sinh = rule { "sinh(" ~ terms ~ ')' ~> Sinh }
	def cosh = rule { "cosh(" ~ terms ~ ')' ~> Cosh }
	def asin = rule { "asin(" ~ terms ~ ')' ~> Asin }
	def acos = rule { "acos(" ~ terms ~ ')' ~> Acos }
	def tan = rule { "tan(" ~ terms ~ ')' ~> Tan }
	def atan = rule { "atan(" ~ terms ~ ')' ~> Atan }
}
