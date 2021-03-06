package org.semanticscience.d2s;

import org.eclipse.rdf4j.repository.Repository;
import org.semanticscience.d2s.operations.QueryOperations;
import org.semanticscience.d2s.operations.RdfUpload;
import org.semanticscience.d2s.operations.Split;
import org.semanticscience.d2s.operations.queries.SparqlExecutorInterface;
import org.semanticscience.d2s.operations.queries.SparqlQueryFactory;

import picocli.CommandLine;

public class SparqlOperation {
	

	public static void main(String[] args) throws Exception {
		try { 
			// TODO: logger
			CliOptions cli = CommandLine.populateCommand(new CliOptions(), args);
			if(cli.help)
				printUsageAndExit();
			
			String username = System.getenv("D2S_USERNAME");
			String password = System.getenv("D2S_PASSWORD");
			if (cli.username != null) {
				username = cli.username;
			}
			if (cli.password != null) {
				password = cli.password;
			}
			Repository repo = SparqlRepositoryFactory.getRepository(cli.endpointUrl, cli.repositoryId, username, password);
			
			if (cli.queryOperation == QueryOperations.split) {
				// If the query operation is split
				Split splitter = new Split(repo, cli.varOutput, cli.splitBufferSize);
				splitter.executeSplit(cli.splitClass, cli.splitProperty, cli.splitDelimiter,  cli.splitQuote, cli.splitDelete, cli.uriExpansion);
			} else if (cli.queryOperation == QueryOperations.upload) {
				RdfUpload.uploadRdf(cli.inputFile, repo, cli.graph, cli.useLib);
			} else {
				// If the query operation is SPARQL: get the SPARQL executor
				System.out.println("Performing operation: " + cli.queryOperation.toString());
				SparqlExecutorInterface sparqlExecutor = SparqlQueryFactory.getSparqlExecutor(cli.queryOperation, repo, 
						cli.varInput, cli.varOutput, cli.varService);
				
				if (cli.sparqlQuery != null) {
					// Execute SPARQL query string passed to -sp
					// TODO: Properly get select results using asList https://rdf4j.eclipse.org/documentation/programming/repository/
					sparqlExecutor.executeSingleQuery(cli.sparqlQuery);
				}
				if (cli.inputFile != null) {
					// Execute SPARQL queries from files passed to -f
					sparqlExecutor.executeFiles(cli.inputFile);
				}
			}
		} catch (Exception e) {
			printUsageAndExit(e);
		}
	}
	
	private static void printUsageAndExit() {
		printUsageAndExit(null);
	}
	
	private static void printUsageAndExit(Throwable e) {
		CommandLine.usage(new CliOptions(), System.out);
		if(e == null)
			System.exit(0);
		e.printStackTrace();
		System.exit(-1);
	}
}