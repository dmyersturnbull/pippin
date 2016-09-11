package kokellab.utils.webservices

import com.chemspider.www.MassSpecAPIStub.{ArrayOfInt, GetExtendedCompoundInfoArray}
import com.chemspider.www.{MassSpecAPIStub, SearchStub}
import com.typesafe.scalalogging.LazyLogging
import java.io.IOException
import kokellab.utils.core.exceptions.ServiceFailedException
import kokellab.utils.core.parseConfig

import scala.xml.XML

import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions}

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

	def csidToMoles(csid: Int) =  try {
		val xml = XML.load(s"http://www.chemspider.com/InChI.asmx/CSIDToMol?csid=$csid&token=$token")
		xml.text
	} catch {
		case e: IOException => throw new ServiceFailedException(s"Could fetch molecule for $csid", e)
	}

	def fetchBasicInfo(id: Int): BasicChemspiderInfo = {
		val stupidArray = new ArrayOfInt
		stupidArray.set_int(Array(id))
		val info = new GetExtendedCompoundInfoArray
		info.setCSIDs(stupidArray)
		info.setToken(token)
		val results = new MassSpecAPIStub().getExtendedCompoundInfoArray(info).getGetExtendedCompoundInfoArrayResult.getExtendedCompoundInfo
		if (results.length > 1) throw new ServiceFailedException(s"Ambiguous ChemSpider ID $id; got ${results.length} results")
		if (results.length < 1) throw new ServiceFailedException(s"No results for ChemSpider ID $id")
		val r = results.head
		assert(id == r.getCSID)
		new BasicChemspiderInfo(r.getCSID, r.getInChI, r.getInChIKey, r.getSMILES, r.getCommonName, r.getMolecularWeight.toFloat)
	}

	def fetchChemspiderIds(smiles: String): Seq[Int] = {
		val search = new SearchStub.SimpleSearch()
		search.setQuery(smiles)
		search.setToken(token)
		new SearchStub().simpleSearch(search).getSimpleSearchResult.get_int()
	}
	def fetchUniqueChemspiderId(smiles: String): Int = {
		val results = fetchChemspiderIds(smiles)
		if (results.length > 1) throw new ServiceFailedException(s"Ambiguous smiles string $smiles; got ${results.length} ChemSpider IDs")
		if (results.length < 1) throw new ServiceFailedException(s"No ChemSpider IDs for smiles string $smiles")
		results.head
	}

}


case class BasicChemspiderInfo(chemspiderId: Int, inchi: String, inchikey: String, smiles: String, commonName: String, molWeight: Float)
