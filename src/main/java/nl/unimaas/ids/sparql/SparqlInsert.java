package nl.unimaas.ids.sparql;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class SparqlInsert {

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
					System.out.println("Inserting: " + f);
					
					// Query the SPARQL endpoint
					Update update = conn.prepareUpdate(QueryLanguage.SPARQL, FileUtils.readFileToString(f));
					update.execute();
					
				}
			} else {
				//TODO: if single file provided 
				//conn.add(new File(filePath), null, Rio.getParserFormatForFileName(inputFile.getName()).get());
			}
			
		} catch (Exception e) {
			throw e;
		}

		//repo.shutDown();
	}
}
