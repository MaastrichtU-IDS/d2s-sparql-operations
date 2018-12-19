package nl.unimaas.ids.operations;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public abstract class AbstractSparqlOperation implements SparqlExecutorInterface {
	protected Logger logger = LoggerFactory.getLogger(AbstractSparqlOperation.class.getName());
	private SPARQLRepository repo;
	HashMap<String, String> variablesHash = new HashMap<String, String>();
	
	public AbstractSparqlOperation(String endpoint, String username, String password, String[] variables) {
		repo = new SPARQLRepository(endpoint);
		repo.setUsernameAndPassword(username, password);
		repo.initialize();
		
        for (int i=0; i<variables.length; i++)
        {
            String[] variableSplitted = variables[i].split(":", 2);
            if (variableSplitted != null) {
	            // Split on first : (varGraph:http://graph gives {"?_varGraph": "http://graph"}
            	variablesHash.put("\\?_" + variableSplitted[0], variableSplitted[1]);
            }
        }
	}

	public void executeFiles(String filePath) throws Exception {
		
		try (RepositoryConnection conn = repo.getConnection()) {
			File inputFile = new File(filePath);
			if(!inputFile.exists())
				throw new IllegalArgumentException("Input file \"" + inputFile.getAbsolutePath() + "\" does not exist");
			if(!inputFile.canRead())
				throw new SecurityException("Can not read from input file \"" + inputFile.getAbsolutePath() + "\"");
			
			// if input file is yaml. 
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
					String queryString = resolveVariables(FileUtils.readFileToString(f));
					logger.info("Executing: ");
					logger.info(queryString);
					executeQuery(conn, queryString, f.getPath());
				}
				
			} else if (FilenameUtils.getExtension(inputFile.getName()).equals("yaml")) { 
				// YAML is provided
				parseQueriesYaml(conn, inputFile);
			} else {
				// Single file provided
				String queryString = resolveVariables(FileUtils.readFileToString(inputFile));
				logger.info("Executing: ");
				logger.info(queryString);
				executeQuery(conn, queryString, inputFile.getPath());
			}
			
		} catch (Exception e) {
			throw e;
		}

		//repo.shutDown();
	}

	@SuppressWarnings("unchecked")
	public void parseQueriesYaml(RepositoryConnection conn, File inputFile) throws Exception {
		logger.info("Parsing YAML...");
		Yaml yaml = new Yaml();
		Map<String, Object> yamlFile = (Map<String, Object>)yaml.load(new FileInputStream(inputFile));
		
		List<String> queries = (List<String>)yamlFile.get("queries");
		int queryCount = 0;
		for(String query : queries) {
			String queryString = resolveVariables(query);
			logger.info("Executing: ");
			logger.info(queryString);
			executeQuery(conn, queryString, FilenameUtils.removeExtension(inputFile.getPath()) + "_query_" + queryCount++);
		}		
	}
	
	// We replace ?_myVar with the corresponding value
	private String resolveVariables(String query) {
		// TODO: first scan for variables?
		scanForVariables(query);
		
		String replacedQuery = query;
		for (Map.Entry<String, String> entry : variablesHash.entrySet()) {
	        //System.out.println(entry.getKey() + " = " + entry.getValue());
			replacedQuery = replacedQuery.replaceAll(entry.getKey().toString(), entry.getValue().toString());
		}
	    return replacedQuery;
	}
	
	// Scan files to check for the variables
	public ArrayList<String> scanForVariables(String query) {
		ArrayList<String> queryVariables = new ArrayList<String>();
		
		Pattern p = Pattern.compile("<\\?_(.*?)>");
	    // create matcher for pattern p and given string
	    Matcher m = p.matcher(query);
	    
	    logger.info("This SPARQL query variables:");
	    while (m.find()) {
	    	// Get first group. Use m.group(0) for the whole match expression
	    	logger.info(m.group(1));
	        queryVariables.add(m.group(1));
	    }
		
	    return queryVariables;
	}
}
