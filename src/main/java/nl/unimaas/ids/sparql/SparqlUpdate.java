package nl.unimaas.ids.sparql;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class SparqlUpdate extends AbstractSparqlQuery {

	public SparqlUpdate(String endpoint, String username, String password) {
		super(endpoint, username, password);
	}

	public void executeQuery(RepositoryConnection conn, File f) throws RepositoryException, MalformedQueryException, IOException {
		System.out.println("Inserting: " + f);
		
		// Query the SPARQL endpoint
		Update update = conn.prepareUpdate(QueryLanguage.SPARQL, FileUtils.readFileToString(f));
		update.execute();
	}
}
