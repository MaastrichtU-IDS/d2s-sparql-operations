package nl.unimaas.ids.operations;

import java.io.IOException;

import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.LoggerFactory;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class SparqlSelect extends AbstractSparqlOperation {

	public SparqlSelect(String endpoint, String username, String password, String[] variables) {
		super(endpoint, username, password, variables);
		logger = LoggerFactory.getLogger(SparqlSelect.class.getName());
	}

	public void executeQuery(RepositoryConnection conn, String queryString, String filepath) throws RepositoryException, MalformedQueryException, IOException {
		logger.info("Selecting from " + filepath);
		
		TupleQuery query = conn.prepareTupleQuery(queryString);
	    // A QueryResult is also an AutoCloseable resource, so make sure it gets
	    // closed when done.
		TupleQueryResultHandler tsvWriter = new SPARQLResultsTSVWriter(System.out);
		query.evaluate(tsvWriter);
	}

}
