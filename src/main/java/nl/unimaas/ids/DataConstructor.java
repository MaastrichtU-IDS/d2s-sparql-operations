package nl.unimaas.ids;

import javax.management.NotificationBroadcasterSupport;

import nl.unimaas.ids.sparql.QueryOperations;
import nl.unimaas.ids.sparql.SparqlConstruct;
import nl.unimaas.ids.sparql.SparqlInsert;
import picocli.CommandLine;

public class DataConstructor {

	public static void main(String[] args) throws Exception {
		try {
			
			CliOptions cli = CommandLine.populateCommand(new CliOptions(), args);
			if(cli.help)
				printUsageAndExit();

			//SparqlConstruct.executeConstructFiles(cli.inputFile, cli.dbUrl, cli.username, cli.password);

			switch (cli.queryOperation) {
	         case insert:
	        	 SparqlInsert.executeFiles(cli.inputFile, cli.dbUrl, cli.username, cli.password);
	         case construct:
	        	 SparqlConstruct.executeFiles(cli.inputFile, cli.dbUrl, cli.username, cli.password);
	         case select:
	        	 throw new UnsupportedOperationException("select operation not supported at the moment. Supported operations: insert and construct");
        	 default:
        		 throw new UnsupportedOperationException("Supported operations: insert and construct. select coming soon.");
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
