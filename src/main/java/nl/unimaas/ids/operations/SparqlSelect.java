package nl.unimaas.ids.operations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class SparqlSelect extends AbstractSparqlOperation {

	public SparqlSelect(String endpoint, String username, String password) {
		super(endpoint, username, password);
	}

	public void executeQuery(RepositoryConnection conn, File f) throws RepositoryException, MalformedQueryException, IOException {
		System.out.println("Selecting: " + f);
		
		TupleQuery query = conn.prepareTupleQuery(FileUtils.readFileToString(f));
	    // A QueryResult is also an AutoCloseable resource, so make sure it gets
	    // closed when done.
		TupleQueryResultHandler tsvWriter = new SPARQLResultsTSVWriter(System.out);
		query.evaluate(tsvWriter);
	}

}
