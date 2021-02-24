package org.semanticscience.d2s.operations;

import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.system.Txn;
import org.apache.tools.ant.DirectoryScanner;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
//import org.apache.jena.rdfconnection.RDFConnection;

import java.io.File;

/**
 * Upload RDF to a RDF4J repository
 */
public class RdfUpload {	

	public static void uploadRdf(String filePath, Repository repo, String graphUri, String useLib) throws Exception {

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
			
			// Try with jena https://jena.apache.org/documentation/rdfconnection/
			if (useLib.equals("jena")) {
				System.out.println("Upload RDF file using Jena");
				String username = System.getenv("D2S_USERNAME");
				String password = System.getenv("D2S_PASSWORD");
				// RDFConnectionFactory.connect("http://localhost:"+PORT+"/ds")
				try ( RDFConnection jenaConn = RDFConnectionFactory.connectPW("https://data.index.semanticscience.org/sparql", username, password) ) {
					Txn.executeWrite(jenaConn, ()-> {
						System.out.println("in conn");
						jenaConn.load(inputFilePath) ;
						System.out.println("Load done");
						// conn.querySelect("SELECT DISTINCT ?s { ?s ?p ?o }", (qs)->
						// Resource subject = qs.getResource("s") ;
						// System.out.println("Subject: "+subject) ;
					}) ;
				}
				catch(Exception e) {
					System.out.println("Error loading with Jena");
					System.out.println(e);
				}
			// RDFConnectionRemoteBuilder builder = RDFConnection.create()
	        //     .destination("http://host/triplestore");

			} else {
			
				File f = new File(inputFilePath);
				// TODO: split RDF file to avoid crash with Virtuoso loading large files
				if (graphUri != null) {
					conn.add(f, null, Rio.getParserFormatForFileName(f.getName()).get(), vf.createIRI(graphUri));
				} else {
					conn.add(f, null, Rio.getParserFormatForFileName(f.getName()).get());
				}
			}
		}

		repo.shutDown();
	}
}