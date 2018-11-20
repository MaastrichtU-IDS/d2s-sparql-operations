package nl.unimaas.ids.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class SparqlConstruct extends AbstractSparqlOperation {

	public SparqlConstruct(String endpoint, String username, String password) {
		super(endpoint, username, password);
	}

	public void executeQuery(RepositoryConnection conn, File f) throws RepositoryException, MalformedQueryException, IOException {
		System.out.println("Constructing: " + f);
		
		// Query the SPARQL endpoint
		GraphQueryResult graphResult = conn.prepareGraphQuery(FileUtils.readFileToString(f)).evaluate();
		System.out.println("SPARQL endpoint query done");
		// Convert query results to a RDF4J model
		Model resultModel = QueryResults.asModel(graphResult);
		System.out.println("Model generated");
		// Write the model to a file
		Rio.write(resultModel, new FileOutputStream("/data/data-constructor/" + f.getName() + ".ttl"), RDFFormat.TURTLE);
		//Rio.write(resultModel, System.out, RDFFormat.TURTLE);
		
		//conn.add(f, null, Rio.getParserFormatForFileName(f.getName()).get());
	}

}
