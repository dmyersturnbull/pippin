
package kokellab.utils.grammers

import org.parboiled2._

import scala.math._

/**
  * Evaluates math expressions, including bit arithmetic.
  * Special notes:
  * <ul>
  *     <li><strong>Unary minus is currently not supported (TODO). Use 0 - expression instead.</strong></li>
  *     <li>The caret character is not used. Use pow(base, exponent).</li>
  *     <li>"Not equal to" is denoted using ≠ (though != will be replaced).</li>
  *     <li>"At most" and "at least" are denoted only using ≤ and ≥ (though <= and >= will be replaced).</li>
  *     <li>"Almost equal" and "not almost equal" are denoted only using ≈ and ≉. They use a tolerance of 1E-10.</li>
  *     <li>The modulo function is denoted using % or mod(a, b).
  *     <li>Bitwise operations implicitly round to integers and are denoted with the normal Scala operators ~, &, |, and ^.</li>
  *     <li>Logical operations use 1 for true and 0 for false: ¬, ∧, ∨, ⊽, ⊻, and → (material implication).</li>
  *     <li>Bitwise, logical, and comparison operators have lower precedence than addition and subtraction.</li>
  *     <li>The function bool(e) returns 0 if e==0 and 1 otherwise.</li>
  *     <li>Other functions of interest are rand(min, max), round(e), ceil(e), floor(e), min(a, b), max(a, b), and sgn.</li>
  * <ul>

  * Ideas and snippets were written by petarvlahu on 2014-09-02: https://github.com/indyscala/Calc/blob/master/src/main/scala/org/indyscala/parboiled/Calc.scala.
  */
object Calculator {

	val TOLERANCE = 1.0e-10

	def eval(expression: String, substitutions: Map[String, Double] = Map.empty): Double = {
		for (a <- substitutions.keys; b <- substitutions.keys) if (a != b) require (!(a contains b),
			s"Substitution $b is a substring of $a; this would be ambiguous")
		val fixed = Map(" " -> "", "!=" -> "≠", "<=" -> "≤", ">=" -> "≥").foldLeft(expression) ((e, s) => e.replaceAllLiterally(s._1, s._2))
		// prepend 0 because unary minus isn't supported
		val substituted = substitutions.foldLeft(fixed) ((e, s) => e.replaceAllLiterally(s._1, if (s._2 < 0) "(0" + s._2.toString + ")" else s._2.toString))
		eval(new Calculator(substituted).line.run().get)
	}

	private def eval(expression: Expression): Double =
		expression match {

			case Value(v) => v.toDouble

			case Bool(b) => if (eval(b) == 0) 0 else 1

			case Addition(a, b) => eval(a) + eval(b)
			case Subtraction(a, b) => eval(a) - eval(b)
			case Multiplication(a, b) => eval(a) * eval(b)
			case Division(a, b) => eval(a) / eval(b)
			case Mod(a, b) => eval(a) % eval(b)

			case Sign(e) => (eval(e) compareTo 0).toDouble
			case Sqrt(e) => math.sqrt(eval(e))
			case CubeRoot(e) => math.cbrt(eval(e))
			case Power(a, b) => math.pow(eval(a), eval(b))
			case Exp(e) => math.exp(eval(e))
			case Ln(e) => math.log(eval(e))
			case Log10(e) => math.log10(eval(e))
			case Log2(e) => math.log10(eval(e)) / log10(2)

			case Equals(a, b) => if (eval(a) == eval(b)) 1.0 else 0.0
			case NotEquals(a, b) => if (eval(a) != eval(b)) 1.0 else 0.0
			case ApproxEquals(a, b) => if (math.abs(eval(a) - eval(b)) < TOLERANCE) 1.0 else 0.0
			case NotApproxEquals(a, b) => if (math.abs(eval(a) - eval(b)) < TOLERANCE) 0.0 else 1.0
			case LessThan(a, b) => if (eval(a) < eval(b)) 1.0 else 0.0
			case MoreThan(a, b) => if (eval(a) > eval(b)) 1.0 else 0.0
			case AtLeast(a, b) => if (eval(a) >= eval(b)) 1.0 else 0.0
			case AtMost(a, b) => if (eval(a) <= eval(b)) 1.0 else 0.0

			case BitwiseNot(e) => (~eval(e).toInt).toDouble
			case BitwiseAnd(a, b) => (eval(a).toInt & eval(b).toInt).toDouble
			case BitwiseOr(a, b)  => (eval(a).toInt | eval(b).toInt).toDouble
			case BitwiseXor(a, b) => (eval(a).toInt ^ eval(b).toInt).toDouble


			case LogicalNot(e) => if (eval(e)==0) 1 else 0

			case LogicalAnd(a, b)         =>  if (eval(a)!=0 && eval(b)!=0) 1 else 0
			case LogicalOr(a, b)          =>  if (eval(a)!=0 || eval(b)!=0) 1 else 0
			case LogicalNand(a, b)        =>  if (eval(a)==0 && eval(b)==0) 1 else 0
			case LogicalNor(a, b)         =>  if (eval(a)==0 || eval(b)==0) 1 else 0
			case LogicalXor(a, b)         =>  if (eval(a)!=0 ^  eval(b)!=0) 1 else 0
			case LogicalImplication(a, b) =>  if (eval(a)==0 || eval(b)!=0) 1 else 0

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

	case class Bool(e: Expression) extends Expression

	case class Addition(lhs: Expression, rhs: Expression) extends Expression
	case class Subtraction(lhs: Expression, rhs: Expression) extends Expression
	case class Multiplication(lhs: Expression, rhs: Expression) extends Expression
	case class Division(lhs: Expression, rhs: Expression) extends Expression
	case class Mod(lhs: Expression, rhs: Expression) extends Expression
	case class Power(lhs: Expression, rhs: Expression) extends Expression

	case class Sqrt(e: Expression) extends Expression
	case class CubeRoot(e: Expression) extends Expression
	case class Exp(e: Expression) extends Expression
	case class Ln(e: Expression) extends Expression
	case class Log10(e: Expression) extends Expression
	case class Log2(e: Expression) extends Expression

	case class Sign(e: Expression) extends Expression
	case class Equals(lhs: Expression, rhs: Expression) extends Expression
	case class NotEquals(lhs: Expression, rhs: Expression) extends Expression
	case class ApproxEquals(lhs: Expression, rhs: Expression) extends Expression
	case class NotApproxEquals(lhs: Expression, rhs: Expression) extends Expression
	case class LessThan(lhs: Expression, rhs: Expression) extends Expression
	case class MoreThan(lhs: Expression, rhs: Expression) extends Expression
	case class AtLeast(lhs: Expression, rhs: Expression) extends Expression
	case class AtMost(lhs: Expression, rhs: Expression) extends Expression

	case class BitwiseNot(e: Expression) extends Expression
	case class BitwiseAnd(lhs: Expression, rhs: Expression) extends Expression
	case class BitwiseOr(lhs: Expression, rhs: Expression) extends Expression
	case class BitwiseXor(lhs: Expression, rhs: Expression) extends Expression

	case class LogicalNot(e: Expression) extends Expression
	case class LogicalAnd(lhs: Expression, rhs: Expression) extends Expression
	case class LogicalOr(lhs: Expression, rhs: Expression) extends Expression
	case class LogicalXor(lhs: Expression, rhs: Expression) extends Expression
	case class LogicalNand(lhs: Expression, rhs: Expression) extends Expression
	case class LogicalNor(lhs: Expression, rhs: Expression) extends Expression
	case class LogicalImplication(lhs: Expression, rhs: Expression) extends Expression

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
			'=' ~ terms ~> Equals |
			'≠' ~ terms ~> NotEquals |
			'≈' ~ terms ~> ApproxEquals |
			'≉' ~ terms ~> NotApproxEquals |
			'<' ~ terms ~> LessThan |
			'>' ~ terms ~> MoreThan |
			'≥' ~ terms ~> AtLeast |
			'≤' ~ terms ~> AtMost |
			'&' ~ terms ~> BitwiseAnd |
			'|' ~ terms ~> BitwiseOr |
			'∧' ~ terms ~> LogicalAnd |
			'∨' ~ terms ~> LogicalOr |
			'⊽' ~ terms ~> LogicalNor |
			'⊼' ~ terms ~> LogicalNand |
			'⊻' ~ terms ~> LogicalXor |
			'→' ~ terms ~> LogicalImplication
		)
	}

	def terms: Rule1[Expression] = rule {
		term ~ zeroOrMore(
			'+' ~ term ~> Addition |
			'-' ~ term ~> Subtraction |
			'−' ~ term ~> Subtraction
		)
	}

	def term = rule {
		factor ~ zeroOrMore(
			'*' ~ factor ~> Multiplication |
			'×' ~ factor ~> Multiplication |
			'/' ~ factor ~> Division |
			'%' ~ factor ~> Mod
		)
	}

	def factor = rule { parentheses | bool | sgn | logicalNot | logicalNot2 | bitwiseNot | sqrt | sqrt2 | cuberoot | pow | exp | ln | abs | round | ceil | floor | min | max | sin | cos | sinh | cosh | asin | acos | tan | atan | number }

	def parentheses = rule { '(' ~ terms ~ ')' }

	def number = rule { capture(digits) ~> Value }

	def digits = rule {
		zeroOrMore(CharPredicate.Digit) ~ optional(fraction)
	}

	def fraction = rule { ch('.') ~ oneOrMore(CharPredicate.Digit) }

	def bool = rule { "bool(" ~ terms ~ ')' ~> Bool }
	def sgn = rule { "sgn(" ~ terms ~ ')' ~> Sign }
	def logicalNot = rule { "not(" ~ terms ~ ')' ~> LogicalNot }
	def logicalNot2 = rule { "¬(" ~ terms ~ ')' ~> LogicalNot }
	def bitwiseNot = rule { "~(" ~ terms ~ ')' ~> BitwiseNot }

	def sqrt = rule { "sqrt(" ~ terms ~ ')' ~> Sqrt }
	def sqrt2 = rule { "√(" ~ terms ~ ')' ~> Sqrt }
	def cuberoot = rule { "∛(" ~ terms ~ ')' ~> CubeRoot }
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
