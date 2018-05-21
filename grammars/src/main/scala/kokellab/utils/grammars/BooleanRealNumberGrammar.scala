package kokellab.utils.grammars

import breeze.stats.distributions.RandBasis
import org.parboiled2._

object BooleanRealNumberGrammar {

	val DEFAULT_TOLERANCE = 0.000001

	def eval(expression: String, tolerance: Double = DEFAULT_TOLERANCE, randBasis: Option[RandBasis] = None) = {
		val functions = randBasis map (rb => RealNumberGrammar.defaultFunctionMap ++ RealNumberGrammar.stochasticFunctionMap(rb)) getOrElse RealNumberGrammar.defaultFunctionMap
		call[Boolean, BooleanRealNumberGrammar](e => new BooleanRealNumberGrammar(e, tolerance, randBasis, functions), p => p.booleanLine.run().get, expression)
	}
}

class BooleanRealNumberGrammar(override val input: ParserInput, tolerance: Double = BooleanRealNumberGrammar.DEFAULT_TOLERANCE,
					 randBasis: Option[RandBasis] = None, functions: Map[String, Seq[Double] => Double] = RealNumberGrammar.defaultFunctionMap
					) extends RealNumberGrammar(input, functions) {

	def booleanLine = rule { booleanExpression ~ EOI }

	def booleanExpression = rule {
		wrappedJunction ~ zeroOrMore(
			("and" | "∧") ~ wrappedJunction ~> ((_: Boolean) && _)
				| ("or" | "∨") ~ wrappedJunction ~> ((_: Boolean) || _)
				| ("nor" | "⊽") ~ wrappedJunction ~> ((a: Boolean, b: Boolean) => !(a || b))
				| ("nand" | "⊼") ~ wrappedJunction ~> ((a: Boolean, b: Boolean) => !(a && b))
				| ("xor" | "⊻") ~ wrappedJunction ~> ((_: Boolean) ^ _)
		)
	}

	def wrappedJunction: Rule1[Boolean] = rule {
		('(' ~ booleanExpression ~ ')') | junction
	}

	def junction: Rule1[Boolean] = rule {
		expression ~ oneOrMore(condition) ~> ((firstValue: Double, conditions: Seq[Condition]) => {
			var lhs = firstValue
			conditions forall {condition =>
				val satisfied = condition.operation(lhs)
				lhs = condition.rhs
				satisfied
			}
		})
	}

	def condition: Rule1[Condition] = rule {
		capture(anyOf("=≠<>≤≥≈≉")) ~ expression ~> ((op: String, b: Double) => Condition(b, op match {
			case "=" => (a: Double) => a == b
			case "≠" => (a: Double) => a != b
			case "<" => (a: Double) => a < b
			case ">" => (a: Double) => a > b
			case "≤" => (a: Double) => a <= b
			case "≥" => (a: Double) => a >= b
			case "≈" => (a: Double) => 2 * math.abs(a-b) / (a+b) < tolerance
			case "≉" => (a: Double) => 2 * math.abs(a-b) / (a+b) >= tolerance
		}))
	}

	/**
	  * A boolean operator with its right-hand side.
	  * @param rhs The right-hand side value
	  * @param operation A function that maps a left-hand side value to a boolean (equivalent to previousValue operation nextValue)
	  */
	case class Condition(rhs: Double, operation: Double => Boolean)

}
