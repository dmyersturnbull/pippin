package kokellab.utils.grammars

import breeze.stats.distributions.{RandBasis, ThreadLocalRandomGenerator}
import org.apache.commons.math3.random.MersenneTwister

object GrammarUtils {

	def randBasis(seed: Int): RandBasis = new RandBasis(new ThreadLocalRandomGenerator(new MersenneTwister(seed)))
}
