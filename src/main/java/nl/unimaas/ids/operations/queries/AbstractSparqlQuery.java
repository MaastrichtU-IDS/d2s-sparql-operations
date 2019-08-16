package nl.unimaas.ids.operations.queries;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public abstract class AbstractSparqlQuery implements SparqlExecutorInterface {
	protected Logger logger = LoggerFactory.getLogger(AbstractSparqlQuery.class.getName());
	private Repository repo;
	
	String varInputGraph;
	String varOutputGraph;
	String varServiceUrl;
	
	public AbstractSparqlQuery(Repository repo, String varInputGraph, String varOutputGraph, String varServiceUrl) {
		this.repo = repo;
		
		this.varInputGraph = varInputGraph;
		this.varOutputGraph = varOutputGraph;
		this.varServiceUrl = varServiceUrl;
	}

	// Executed when files are provided. Execute from single file from URL or file path, or multiple files from directory
	public void executeFiles(String filePath) throws Exception {
		
		try (RepositoryConnection conn = repo.getConnection()) {
			if (filePath.startsWith("https://github.com/")) {
				// Crawl a given path in a github repository to execute .rq files
				logger.info("Crawling GitHub page: " + filePath);
				ArrayList<URL> queryList = crawlGithubToGetQueries(filePath);
				for (URL queryUrl : queryList) {
					logger.info("Executing GitHub URL: " + queryUrl.toString());
					executeFromUrl(conn, queryUrl);
				}
			} else if (filePath.matches("^(http|https|ftp)://.*$")) {
				// If user provide a URL
				executeFromUrl(conn, new URL(filePath));
			} else {
				// File or dir path provided
				File inputFile = new File(filePath);
				if(!inputFile.exists())
					throw new IllegalArgumentException("Input file \"" + inputFile.getAbsolutePath() + "\" does not exist");
				if(!inputFile.canRead())
					throw new SecurityException("Can not read from input file \"" + inputFile.getAbsolutePath() + "\"");
				
				// if input file is a directory 
				if (inputFile.isDirectory()) {
					Collection<File> files = FileUtils.listFiles(
							inputFile,
							new RegexFileFilter(".*\\.(rq|sparql)"),
							DirectoryFileFilter.DIRECTORY
					);
					List<File> fileList = new ArrayList<File>(files);
					Collections.sort(fileList);
					// Recursively iterate over files in the directory in the alphabetical order
					Iterator<File> iterator = fileList.iterator();
					while (iterator.hasNext()) {
						File f = iterator.next();
						String queryString = resolveVariables(FileUtils.readFileToString(f, "UTF-8"));
						executeQuery(conn, queryString, f.getPath());
					}
					
				} else if (FilenameUtils.getExtension(inputFile.getName()).equals("yaml")) { 
					// If input file is YAML we parse it to execute provided queries
					parseQueriesYaml(conn, inputFile);
				} else {
					// Single file provided
					String queryString = resolveVariables(FileUtils.readFileToString(inputFile, "UTF-8"));
					executeQuery(conn, queryString, inputFile.getPath());
				}
			}
		} catch (Exception e) {
			throw e;
		}
		//repo.shutDown();
	}
	
	// We replace ?_var with the corresponding value
	private String resolveVariables(String query) {
		//scanForVariables(query);
		query = query.replaceAll("\\?_inputGraph", varInputGraph);
		query = query.replaceAll("\\?_outputGraph", varOutputGraph);
		query = query.replaceAll("\\?_serviceUrl", varServiceUrl);
		//logger.info("    SPARQL query after replace all: " + query);
	    return query;
	}
	
	// TO REMOVE? Scan files to check for the variables
	public ArrayList<String> scanForVariables(String query) {
		ArrayList<String> queryVariables = new ArrayList<String>();
		Pattern p = Pattern.compile("<\\?_(.*?)>");
	    Matcher m = p.matcher(query);
	    
	    logger.info("    VARIABLES of the query:");
	    while (m.find()) {
	    	// Get first group. Use m.group(0) for the whole match expression
	    	logger.info(m.group(1));
	        queryVariables.add(m.group(1));
	    }
	    return queryVariables;
	}
	
	// Execute a single SPARQL query string
	public void executeSingleQuery(String queryString) throws Exception {
		try (RepositoryConnection conn = repo.getConnection()) {
			queryString = resolveVariables(queryString);
			executeQuery(conn, queryString, null);
			
		} catch (Exception e) {
			throw e;
		}
		//repo.shutDown();
	}
		
	private void executeFromUrl(RepositoryConnection conn, URL url) throws Exception {
		File urlFile = File.createTempFile("data2services-sparql-operations-", null); // generate a .tmp
		FileUtils.copyURLToFile(url, urlFile);
		
		if (url.toString().endsWith(".yaml")) {
			// If input file is YAML we parse it to execute provided queries
			parseQueriesYaml(conn, urlFile);
			
		} else {	
			String queryString = resolveVariables(FileUtils.readFileToString(urlFile, "UTF-8"));				
			logger.info("Executing single file from URL: " + url.toString());
			executeQuery(conn, queryString, null);
		}
	}

	// Execute queries from a YAML file.
	@SuppressWarnings("unchecked")
	public void parseQueriesYaml(RepositoryConnection conn, File inputFile) throws Exception {
		logger.info("Parsing YAML...");
		Yaml yaml = new Yaml();
		Map<String, Object> yamlFile = (Map<String, Object>)yaml.load(new FileInputStream(inputFile));
		
		List<String> queries = (List<String>)yamlFile.get("queries");
		int queryCount = 0;
		for(String query : queries) {
			String queryString = resolveVariables(query);
			executeQuery(conn, queryString, FilenameUtils.removeExtension(inputFile.getPath()) + "_query_" + queryCount++);
		}		
	}
	
	public ArrayList<URL> crawlGithubToGetQueries(String githubUrl) throws IOException {
		ArrayList<URL> queryList = new ArrayList<URL>();
		String html = Jsoup.connect(githubUrl).get().html();
		Pattern pattern = Pattern.compile("href=\"(\\/.*?\\.rq)\"");
        Matcher matcher = pattern.matcher(html);
        logger.info("SPARQL queries URL found by crawling " + githubUrl + " :");
        while (matcher.find()) {
        	queryList.add(new URL("https://raw.githubusercontent.com" + matcher.group(1).replace("blob/", "")));
        	logger.info("https://raw.githubusercontent.com" + matcher.group(1).replace("blob/", ""));
        }
		return queryList;
	}
	
}
