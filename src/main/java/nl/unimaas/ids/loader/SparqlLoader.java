package nl.unimaas.ids.loader;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.File;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class SparqlLoader {

	private static SPARQLRepository repo;

	public static void uploadRdf(boolean isRdf4jSparql, String filePath, String endpoint, String username, String password) throws Exception {

		if(isRdf4jSparql == true) {
			repo = new SPARQLRepository(endpoint, endpoint + "/statements");
		} else {
			repo = new SPARQLRepository(endpoint);
		}

		repo.setUsernameAndPassword(username, password);
		repo.initialize();


		try (RepositoryConnection conn = repo.getConnection()) {
			//File inputFile = new File(filePath);
			
			GraphQueryResult graphResult = conn.prepareGraphQuery("CONSTRUCT { ?s ?p ?o } WHERE {<http://ids.unimaas.nl/rdf2xml/model/drugbank/drug> ?p ?o . ?s ?p ?o }").evaluate();
			
			Model resultModel = QueryResults.asModel(graphResult);
			


			Rio.write(resultModel, System.out, RDFFormat.RDFXML);


			
			FileUtils.writeStringToFile(new File("/data/test.txt"), resultModel.toString());
			
		} catch (Exception e) {
			throw e;
		}

		//repo.shutDown();
	}
}
