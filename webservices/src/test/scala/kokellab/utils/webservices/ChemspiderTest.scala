package kokellab.utils.webservices

import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}

class ChemspiderTest extends PropSpec with PropertyChecks with Matchers {

	def test() = {
		val spider = new Chemspider
		val smilesGen = Gen.oneOf("C[C@@H](c1ccc(cc1)CC(C)C)C(=O)O", "CC(=O)Oc1ccccc1C(=O)O")
		property(s"Should get the same smiles string back") {
			forAll(smilesGen) { (smiles: String) => {
				val ids = spider.fetchChemspiderIds(smiles)
				ids foreach (id => {
					val fetched = spider.fetchBasicInfo(id)
					smiles should equal (fetched.smiles)
				})
				}
			}
		}
	}

}
