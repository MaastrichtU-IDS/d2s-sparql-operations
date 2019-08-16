package nl.unimaas.ids.operations.queries;

import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.LoggerFactory;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class SparqlConstruct extends AbstractSparqlQuery {

	public SparqlConstruct(Repository repo, String varInputGraph, String varOutputGraph, String varServiceUrl) {
		super(repo, varInputGraph, varOutputGraph, varServiceUrl);
		logger = LoggerFactory.getLogger(SparqlConstruct.class.getName());
	}

	public void executeQuery(RepositoryConnection conn, String queryString, String outputFilepath) throws RepositoryException, MalformedQueryException, IOException {
		logger.info("Executing CONSTRUCT query:");
		logger.info(queryString);
		
		// Query the SPARQL endpoint
		GraphQueryResult graphResult = conn.prepareGraphQuery(queryString).evaluate();
		logger.info("SPARQL endpoint query done");
		
		// Convert query results to a RDF4J model
		Model resultModel = QueryResults.asModel(graphResult);
		logger.info("Model generated");
		
		// Write the model to a file (for each rq file executed)
		if (outputFilepath == null) {
			Rio.write(resultModel, System.out, RDFFormat.TURTLE);
		} else {
			Rio.write(resultModel, new FileOutputStream(outputFilepath + ".ttl"), RDFFormat.TURTLE); // TODO: fix the name definition
		}
		
		//conn.add(f, null, Rio.getParserFormatForFileName(f.getName()).get());
	}

}
