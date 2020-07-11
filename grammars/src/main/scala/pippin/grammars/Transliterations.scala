package kokellab.utils.grammars

import scala.collection.immutable.{ListMap, ListSet}

/**
  * Abstractly, a conversion between character sets; more concretely an ordered map of string replacements.
  * May or may not commute with other Transliterations.
  * TODO Still under development: Use with caution.
  * Example usage:
  * <code>
  * 	val greek = Transliterations.greek
  * 	greek("alpha") // α
  * 	greek.idempotent // true
  * 	val carrotToPossum = new Transliteration { override val replacements = ListMap("carrot" -> "possum") }
  * 	println(carrotToPossum ## greek) // true, they're commutative
  * 	val extendedGreek: Transliteration = greek ++ carrotToPossum
  * 	val alphaToPossum = new Transliteration { override val replacements = ListMap("α" -> "possum") }
  * 	val commutativePossum = greek ++ alphaToPossum     // will fail!!
  * 	val noncommutativePossum = greek ~> alphaToPossum  // use this instead: transliterate Greek, then alpha to possum
  *		noncommutativePossum("alpha") // "possum"
  *		noncommutativePossum("α") // "possum"
  *		(alphaToPossum ~> greek)("alpha") // "α"
  *		println(noncommutativePossum.hazardousSubstrings)
  * </code>
  */
trait Transliteration {

	final def apply(expression: String): String =  replacements.foldLeft(expression) ((e, s) => e.replaceAllLiterally(s._1, s._2))

	val replacements: ListMap[String, String]

	/**
	  * <strong>Excludes keys from self-referential mappings (a→a).</strong>
	  */
	final def keys: ListSet[String] = (ListSet.newBuilder[String] ++= (this.replacements filter (kv => kv._1 != kv._2)).keys).result

	/**
	  * <strong>Excludes values of self-referential mappings (a→a).</strong>
	  */
	final def values: ListSet[String] = (ListSet.newBuilder[String] ++= (this.replacements filter (kv => kv._1 != kv._2)).values).result

	/**
	  * @return (substring, string) pairs of the keys where the substring appears <em>before</em> the enclosing string
	  */
	final def hazardousSubstrings: Set[(String, String)] =
		hazardousSubstrings(keys, keys)
	private final def hazardousSubstrings(possibleSubstrings: Set[String], possibleSuperstrings: Set[String]): Set[(String, String)] =
		substrings(possibleSubstrings, possibleSuperstrings) filter { case (a, b) => keys.toList.indexOf(a) < keys.toList.indexOf(b)}

	/**
	  * @return (substring, string) pairs of the keys
	  */
	final def substrings: Set[(String, String)] =
		substrings(keys, keys)
	private final def substrings(possibleSubstrings: Set[String], possibleSuperstrings: Set[String]): Set[(String, String)] =
		possibleSubstrings filter (_.nonEmpty) flatMap (a => possibleSuperstrings filter (_.nonEmpty) filter (_ != a) filter (b => b contains a) map (b => (a, b)))

	/**
	  * Returns the composition, requiring that they commute and that there are no hazardous substrings or hazardous duplicate keys in the composition.
	  */
	final def ++(that: Transliteration): Transliteration = {
		require(this ## that, s"$this does not commute with $that; use `~>` if you wish to compose these anyway.")
		this ~> that
	}

	/**
	  * Returns the composition, <em>not</em> requiring that they commute. In other words, does <em>this and then that</em>.
	  */
	final def ~>(that: Transliteration): Transliteration = new Transliteration {
		override val replacements: ListMap[String, String] = Transliteration.this.replacements ++ that.replacements
	}

	/**
	  * See <em>definitelyCommutesWith</em>.
	  */
	final def ##(next: Transliteration): Boolean = definitelyCommutesWith(next)

	/**
	  * Only implements a sufficient condition. A case is:
	  * <pre>
	  * (a→x, ab→y) ++ (ab→y, a→x)
	  * </pre>
	  * TODO implement iff condition; is this already?
	  */
	final def definitelyCommutesWith(that: Transliteration): Boolean = this == that || (
			((this.keys intersect that.keys) forall (k => this.replacements(k) == that.replacements(k))) // mapping to different values is obviously bad
				&& (this.keys intersect that.values).isEmpty // and one mapping to the input of the other is also bad
				&& (this.values intersect that.keys).isEmpty
				&& substrings(this.keys, that.keys).isEmpty // and
				&& substrings(that.keys, this.keys).isEmpty
				&& substrings(this.keys, that.values).isEmpty
				&& substrings(that.keys, this.values).isEmpty
		)

	final def idempotent: Boolean = (
		(this.keys intersect this.values).isEmpty
			&& substrings(this.keys, this.values).isEmpty
		// a→a would cause this to fail EXCEPT that they're already excluded in keys and values
		// also, '' could be a substring, except that it's filtered in substrings()
		)

	override def equals(obj: scala.Any): Boolean =
		obj.isInstanceOf[Transliteration] && obj.asInstanceOf[Transliteration].replacements == this.replacements

	override def toString: String = s"Transliteration(${this.replacements.mkString(",").replaceAllLiterally(" ", "")})"
}

class MathTransliteration extends Transliteration {
	override val replacements = ListMap(
		" " -> "", "−" -> "-",
		"!=" -> "≠", "<=" -> "≤", ">=" -> "≥", "==" -> "=",
		"!~=" -> "≉", "~=" -> "≈", // note the order here
		"Infinity" -> "∞", "infinity" -> "∞", "inf" -> "∞", "Inf" -> "∞"
	)
}

class DashTransliteration extends Transliteration {
	override val replacements = ListMap("–" -> "-", "—" -> "-", "‒" -> "-", "−" -> "-")
}

class GreekTransliteration extends Transliteration {
	override val replacements = ListMap(
		"theta" -> "θ", "zeta" -> "ζ", "beta" -> "β", "epsilon" -> "ε", "upsilon" -> "υ", "alpha" -> "α", "gamma" -> "γ", "delta" -> "δ",
		"iota" -> "ι", "kappa" -> "κ", "lambda" -> "λ", "mu" -> "μ", "nu" -> "ν", "xi" -> "ξ", "omicron" -> "ο", "pi" -> "π",
		"rho" -> "ρ", "sigma" -> "σ", "tau" -> "τ", "phi" -> "φ", "chi" -> "χ", "omega" -> "ω", "psi" -> "ψ", "eta" -> "η"
	)
}

object Transliterations {
	def math = new MathTransliteration()
	def dashes = new DashTransliteration()
	def greek = new GreekTransliteration()
}
