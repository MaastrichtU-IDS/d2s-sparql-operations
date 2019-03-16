package nl.unimaas.ids;

import nl.unimaas.ids.operations.SparqlOperationFactory;
import nl.unimaas.ids.operations.SparqlExecutorInterface;
import picocli.CommandLine;

public class SparqlOperation {

	public static void main(String[] args) throws Exception {
		try {
			// TODO: logger
			CliOptions cli = CommandLine.populateCommand(new CliOptions(), args);
			if(cli.help)
				printUsageAndExit();
			
			//System.out.println("Performing operation: " + cli.queryOperation.toString());
			
			SparqlExecutorInterface sparqlExecutor = SparqlOperationFactory.getSparqlExecutor(cli.queryOperation, cli.dbUrl, cli.username, cli.password, cli.variables);
			
			if (cli.sparqlQuery != null) {
				sparqlExecutor.executeSingleQuery(cli.sparqlQuery);
			}
			
			if (cli.inputFile != null) {
				sparqlExecutor.executeFiles(cli.inputFile);
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
