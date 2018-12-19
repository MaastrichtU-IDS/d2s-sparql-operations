package nl.unimaas.ids.operations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

public interface SparqlExecutorInterface {
	
	public void executeFiles(String filePath) throws Exception;
	
	public void executeQuery(RepositoryConnection conn, String queryString, String outputFilepath) throws RepositoryException, MalformedQueryException, IOException ;
		
	public void parseQueriesYaml(RepositoryConnection conn, File inputFile) throws Exception ;
	
	public ArrayList<String> scanForVariables(String query) ;


}
