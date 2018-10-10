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
import java.io.FileOutputStream;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class SparqlConstruct {

	private static SPARQLRepository repo;

	public static void executeConstructFiles(String filePath, String endpoint, String username, String password) throws Exception {

		repo = new SPARQLRepository(endpoint);
		repo.setUsernameAndPassword(username, password);
		repo.initialize();

		try (RepositoryConnection conn = repo.getConnection()) {
			//File inputFile = new File(filePath);
			
			GraphQueryResult graphResult = conn.prepareGraphQuery("CONSTRUCT { ?s ?p ?o } WHERE {<http://ids.unimaas.nl/rdf2xml/model/drugbank/drug> ?p ?o . ?s ?p ?o }").evaluate();
			
			Model resultModel = QueryResults.asModel(graphResult);
			
			Rio.write(resultModel, new FileOutputStream("/data/test.txt"), RDFFormat.TURTLE);
			Rio.write(resultModel, System.out, RDFFormat.TURTLE);
			
		} catch (Exception e) {
			throw e;
		}

		//repo.shutDown();
	}
}
