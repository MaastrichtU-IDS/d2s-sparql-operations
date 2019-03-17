package nl.unimaas.ids;

import nl.unimaas.ids.operations.QueryOperation;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "rdf4j-sparql-operations")
public class CliOptions {
	@Option(names = { "-?", "--help" }, usageHelp = true, description = "Display a help message")
	boolean help = false;
	
	@Option(names= {"-sp", "--sparql-query"}, description = "SPARQL query string to execute.")
	String sparqlQuery = null;
	
	@Option(names= {"-f", "--filepath"}, description = "Path of file(s) to execute. Single file from URL or filepath. Multiple files from directory (query files must have .rq extension). YAML file.")
	String inputFile = null;
	
	@Option(names= {"-op", "--operation"}, description = "SPARQL query operation (update, construct, select). Default is update")
	QueryOperation queryOperation = QueryOperation.update;
	
	@Option(names= {"-var", "--variables"}, arity = "0..*", paramLabel = "STRING", description = "Variables to replace in the SPARQL query. E.g.: varGraphInput:http://data2services/input varGraphOutput:http://data2services/output")
	String[] variables;

	@Option(names= {"-ep", "--sparql-endpoint"}, description = "URL for SPARQL Endpoint", required = true)
	String dbUrl = null;

	@Option(names= {"-rep", "--repositoryId"}, description = "RDF4J Repository ID for HTTPRepository file upload (only required in case of RDF4JSPARQL or HTTP method)")
	String repositoryId = null;

	@Option(names= {"-un", "--username"}, description = "Username used for triplestore authentication")
	String username = null;

	@Option(names= {"-pw", "--password"}, description = "Password used for triplestore authentication")
	String password = null;


}