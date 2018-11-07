package nl.unimaas.ids.sparql;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class SparqlConstruct {

	private static SPARQLRepository repo;

	public static void executeFiles(String filePath, String endpoint, String username, String password) throws Exception {
		
		repo = new SPARQLRepository(endpoint);
		repo.setUsernameAndPassword(username, password);
		repo.initialize();
		
		try (RepositoryConnection conn = repo.getConnection()) {
			File inputFile = new File(filePath);
			if(!inputFile.exists())
				throw new IllegalArgumentException("Input file \"" + inputFile.getAbsolutePath() + "\" does not exist");
			if(!inputFile.canRead())
				throw new SecurityException("Can not read from input file \"" + inputFile.getAbsolutePath() + "\"");
			
			if (inputFile.isDirectory()) {
				Collection<File> files = FileUtils.listFiles(
						inputFile,
						new RegexFileFilter(".*\\.(rq|sparql)"),
						DirectoryFileFilter.DIRECTORY
				);
				// Recursively iterate over files in the directory
				Iterator<File> iterator = files.iterator();
				while (iterator.hasNext()) {
					File f = iterator.next();
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
			} else {
				//conn.add(new File(filePath), null, Rio.getParserFormatForFileName(inputFile.getName()).get());
			}
			
			
			
			//GraphQueryResult graphResult = conn.prepareGraphQuery("CONSTRUCT { ?s ?p ?o } WHERE {<http://ids.unimaas.nl/rdf2xml/model/drugbank/drug> ?p ?o . ?s ?p ?o }").evaluate();
			
			//Model resultModel = QueryResults.asModel(graphResult);
			
			//Rio.write(resultModel, new FileOutputStream("/data/test.txt"), RDFFormat.TURTLE);
			//Rio.write(resultModel, System.out, RDFFormat.TURTLE);
			
		} catch (Exception e) {
			throw e;
		}

		//repo.shutDown();
	}
}
