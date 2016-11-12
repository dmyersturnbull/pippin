package kokellab.utils.webservices

import scala.util.{Try, Success, Failure}

import scala.xml.XML

import java.io.IOException

import com.chemspider.www.MassSpecAPIStub.{ArrayOfInt, GetExtendedCompoundInfoArray}
import com.chemspider.www.{MassSpecAPIStub, SearchStub}
import com.typesafe.scalalogging.LazyLogging
import kokellab.utils.core.exceptions.ServiceFailedException
import kokellab.utils.core.parseConfig

/**
  * Please note that using SMILESToInChI returns only a single, arbitrary steroisomer, at least if the sterocenters are not defined in the SMILES.
  * However, the mass spec API does not suffer from this issue.
  */
class Chemspider(token: String = parseConfig("config/app.properties").getString("chemspiderToken")) extends LazyLogging {

	/**
	  * @return The text of the mol file
	  */
	def csidToMoles(chemspiderId: Int): String =  try {
		val xml = XML.load(s"http://www.chemspider.com/InChI.asmx/CSIDToMol?csid=$chemspiderId&token=$token")
		xml.text
	} catch {
		case e: IOException => throw new ServiceFailedException(s"Could fetch molecule for $chemspiderId", e)
	}

	/**
	  * @throws kokellab.utils.core.exceptions.ServiceFailedException If the query was not found
	  */
	def fetchBasicInfo(chemspiderId: Int): BasicChemspiderInfo = {

		// this is the worst API I've ever seen
		val stupidArray = new ArrayOfInt
		stupidArray.set_int(Array(chemspiderId))
		val info = new GetExtendedCompoundInfoArray
		info.setCSIDs(stupidArray)
		info.setToken(token)

		val attempt = Try(new MassSpecAPIStub().getExtendedCompoundInfoArray(info).getGetExtendedCompoundInfoArrayResult.getExtendedCompoundInfo)
		attempt match {
			case Success(results) =>

				if (results.length > 1) throw new ServiceFailedException(s"Ambiguous ChemSpider ID $chemspiderId; got ${results.length} results")
				if (results.length < 1) throw new ServiceFailedException(s"No results for ChemSpider ID $chemspiderId")

				val r = results.head
				assert(chemspiderId == r.getCSID)
				BasicChemspiderInfo(r.getCSID, r.getInChI, r.getInChIKey, r.getSMILES, r.getCommonName, r.getMolecularWeight.toFloat)
			case Failure(e) =>
				throw new ServiceFailedException(s"No results for ChemSpider ID $chemspiderId")
		}
	}

	/**
	  * @param query A name, a SMILES string, a chemical formula, an inchi key, or an inchi string
	  * @return All ChemSpider IDs found
	  */
	def fetchChemspiderIds(query: String): Seq[Int] = {
		val search = new SearchStub.SimpleSearch()
		search.setQuery(query)
		search.setToken(token)
		val results = new SearchStub().simpleSearch(search).getSimpleSearchResult.get_int()
		if (results == null) Seq.empty[Int]
		else results
	}

	/**
	  * @param query A name, a SMILES string, a chemical formula, an inchi key, or an inchi string
	  * @throws kokellab.utils.core.exceptions.ServiceFailedException If the query was not found, or returned multiple matches
	  */
	def fetchUniqueChemspiderId(query: String): Int = {
		val results = fetchChemspiderIds(query)
		if (results.length > 1) throw new ServiceFailedException(s"Ambiguous smiles string $query; got ${results.length} ChemSpider IDs")
		if (results.length < 1) throw new ServiceFailedException(s"No ChemSpider IDs for smiles string $query")
		results.head
	}

}

case class BasicChemspiderInfo(chemspiderId: Int, inchi: String, inchikey: String, smiles: String, commonName: String, molWeight: Float)
