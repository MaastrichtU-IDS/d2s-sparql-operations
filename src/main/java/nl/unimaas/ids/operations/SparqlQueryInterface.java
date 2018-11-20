package nl.unimaas.ids.operations;

import java.io.IOException;

import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

public interface SparqlQueryInterface {
	
	public void executeFiles(String filePath) throws Exception;
	
	public void executeQuery(RepositoryConnection conn, String queryString) throws RepositoryException, MalformedQueryException, IOException ;

}
