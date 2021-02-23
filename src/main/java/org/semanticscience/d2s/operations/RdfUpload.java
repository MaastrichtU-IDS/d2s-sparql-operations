package org.semanticscience.d2s.operations;

import org.apache.tools.ant.DirectoryScanner;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.io.File;

/**
 * Upload RDF to a RDF4J repository
 */
public class RdfUpload {	

	public static void uploadRdf(String filePath, Repository repo, String graphUri) throws Exception {

		RepositoryConnection conn = repo.getConnection();
		ValueFactory vf = SimpleValueFactory.getInstance();
		
		DirectoryScanner scanner = new DirectoryScanner();
		// Remove / if present at start of filepath
		// scanner.setIncludes(new String[]{filePath.substring(filePath.startsWith("/") ? 1 : 0)});
		scanner.setIncludes(new String[]{filePath});
		scanner.setBasedir(new File(System.getProperty("user.dir")));
		scanner.setCaseSensitive(false);
		scanner.scan();
		
		for(String inputFilePath : scanner.getIncludedFiles()) {
			System.out.println("Uploading: " + inputFilePath);
			File f = new File(inputFilePath);
			if (graphUri != null) {
				conn.add(f, null, Rio.getParserFormatForFileName(f.getName()).get(), vf.createIRI(graphUri));
			} else {
				conn.add(f, null, Rio.getParserFormatForFileName(f.getName()).get());
			}
		}

		repo.shutDown();
	}
}