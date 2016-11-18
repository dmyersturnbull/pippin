package kokellab.utils.webservices

import org.scalatest._
import prop._


class ChemspiderTest extends PropSpec with TableDrivenPropertyChecks with Matchers {

	val spider = new Chemspider

	val realSmiles = Table("C[C@@H](c1ccc(cc1)CC(C)C)C(=O)O", "CC(=O)Oc1ccccc1C(=O)O")
	property(s"Should get the same smiles string back") {
		forAll(realSmiles) { (smiles: String) => {
			val ids = spider.fetchChemspiderIds(smiles)
			ids foreach (id => {
				val fetched = spider.fetchBasicInfo(id)
				smiles should equal (fetched.smiles)
			})
		}
		}
	}

	val badSmiles = Table("[CC[12354235", "==CCSDF")
	property(s"Should return an empty sequence") {
		forAll(badSmiles) { (smiles: String) =>
			spider.fetchChemspiderIds(smiles) should be (empty)
		}
	}

}
