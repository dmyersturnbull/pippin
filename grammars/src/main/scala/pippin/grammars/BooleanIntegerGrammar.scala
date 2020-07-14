package pippin.grammars

import org.parboiled2._

object BooleanIntegerGrammar {

	def eval(expression: String): Boolean = {
		call[Boolean, BooleanIntegerGrammar](e => new BooleanIntegerGrammar(e), p => p.booleanLine.run().get, expression)
	}
}

class BooleanIntegerGrammar(override val input: ParserInput,
					functions: Map[String, Seq[Int] => Int] = IntegerGrammar.defaultFunctionMap
					) extends IntegerGrammar(input, functions) {

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

	/**
	  * A logical junction of booleans in the form val_1 operation_1 val_2 operation_2 ... operation_{n-1} val_n
	  * Ex: 5 < 10 = 10 > 1
	  */
	def junction: Rule1[Boolean] = rule {
		expression ~ oneOrMore(condition) ~> ((firstValue: Int, conditions: Seq[Condition]) => {
			var lhs = firstValue
			conditions forall {condition =>
				val satisfied = condition.operation(lhs)
				lhs = condition.rhs
				satisfied
			}
		})
	}

	def condition: Rule1[Condition] = rule {
		capture(anyOf("=≠<>≤≥")) ~ expression ~> ((op: String, b: Int) => Condition(b, op match {
			case "=" => (a: Int) => a == b
			case "≠" => (a: Int) => a != b
			case "<" => (a: Int) => a < b
			case ">" => (a: Int) => a > b
			case "≤" => (a: Int) => a <= b
			case "≥" => (a: Int) => a >= b
		}))
	}

	/**
	  * A boolean operator with its right-hand side.
	  * @param rhs The right-hand side value
	  * @param operation A function that maps a left-hand side value to a boolean (equivalent to previousValue operation nextValue)
	  */
	case class Condition(rhs: Int, operation: Int => Boolean)

}
