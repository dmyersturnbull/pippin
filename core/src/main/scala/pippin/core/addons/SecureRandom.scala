/*

Copyright 2016–2017 kl-common-scala authors
See https://github.com/kokellab/kl-common-scala

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.


Some of the code here is adapted from the Scala API. That copyright notice is given below:

---------------------------------------------------------------------------------------------------------------------------------------------------------------
Scala License
Copyright (c) 2002-2017 EPFL
Copyright (c) 2011-2017 Lightbend, Inc.

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.
Neither the name of the EPFL nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior
written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
---------------------------------------------------------------------------------------------------------------------------------------------------------------

 */

package kokellab.utils.core.addons

import scala.language.higherKinds
import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.{List, Stream}
import scala.collection.mutable.ArrayBuffer


class SecureRandom extends java.security.SecureRandom {

	/** Returns a pseudorandomly generated String.  This routine does
	  *  not take any measures to preserve the randomness of the distribution
	  *  in the face of factors like unicode's variable-length encoding,
	  *  so please don't use this for anything important.  It's primarily
	  *  intended for generating test data.
	  *
	  *  @param  length    the desired length of the String
	  *  @return           the String
	  */
	def nextString(length: Int): String = {
		def safeChar() = {
			val surrogateStart: Int = 0xD800
			val res = nextInt(surrogateStart - 1) + 1
			res.toChar
		}
		List.fill(length)(safeChar()).mkString
	}

	/** Returns the next pseudorandom, uniformly distributed value from the ASCII range 33-126. */
	def nextPrintableChar(): Char = {
		val low  = 33
		val high = 127
		(this.nextInt(high - low) + low).toChar
	}

	/** Returns a new collection of the same type in a randomly chosen order.
	  *
	  *  @return         the shuffled collection
	  */
	def shuffle[T, CC[X] <: TraversableOnce[X]](xs: CC[T])(implicit bf: CanBuildFrom[CC[T], T, CC[T]]): CC[T] = {
		val buf = new ArrayBuffer[T] ++= xs
		def swap(i1: Int, i2: Int): Unit = {
			val tmp = buf(i1)
			buf(i1) = buf(i2)
			buf(i2) = tmp
		}
		for (n <- buf.length to 2 by -1) {
			val k = nextInt(n)
			swap(n - 1, k)
		}
		(bf(xs) ++= buf).result()
	}

	/** Returns a Stream of pseudorandomly chosen alphanumeric characters,
	  *  equally chosen from A-Z, a-z, and 0-9.
	  */
	def alphanumeric: Stream[Char] = {
		def nextAlphaNum: Char = {
			val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
			chars charAt (this nextInt chars.length)
		}
		Stream continually nextAlphaNum
	}

	def nextHexadecimal: Stream[Char] = {
		alphanumeric filter (c => c.isDigit || (Set('a', 'c', 'b', 'd', 'e', 'f') contains c))
	}

}
