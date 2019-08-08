package nl.unimaas.ids;

import nl.unimaas.ids.operations.QueryOperations;
import nl.unimaas.ids.operations.Split;
import nl.unimaas.ids.operations.queries.SparqlExecutorInterface;
import nl.unimaas.ids.operations.queries.SparqlQueryFactory;

import org.eclipse.rdf4j.repository.Repository;

import picocli.CommandLine;

public class SparqlOperation {
	

	public static void main(String[] args) throws Exception {
		try { 
			// TODO: logger
			CliOptions cli = CommandLine.populateCommand(new CliOptions(), args);
			if(cli.help)
				printUsageAndExit();
			
			Repository repo = SparqlRepositoryFactory.getRepository(cli.endpointUrl, cli.repositoryId, cli.username, cli.password);
			
			if (cli.queryOperation == QueryOperations.split) {
				Split splitter = new Split(repo, cli.varOutputGraph, cli.splitBufferSize);
				splitter.executeSplit(cli.splitClass, cli.splitProperty, cli.splitDelimiter,  cli.splitQuote, cli.splitDelete, cli.uriExpansion);
			} else {
				// Execute SPARQL query operations
				System.out.println("Performing operation: " + cli.queryOperation.toString());
				SparqlExecutorInterface sparqlExecutor = SparqlQueryFactory.getSparqlExecutor(cli.queryOperation, repo, 
						cli.varInputGraph, cli.varOutputGraph, cli.varServiceUrl);
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