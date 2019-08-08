package nl.unimaas.ids;

import nl.unimaas.ids.operations.QueryOperations;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "data2services-sparql-operations")
public class CliOptions {
	@Option(names = { "-h", "--help" }, usageHelp = true, description = "Display a help message")
	boolean help = false;
	
	@Option(names= {"-sp", "--sparql-query"}, description = "SPARQL query string to execute.")
	String sparqlQuery = null;
	
	@Option(names= {"-f", "--filepath"}, description = "Path of file(s) to execute. Single file from URL or filepath. Multiple files from directory (query files must have .rq extension). YAML file.")
	String inputFile = null;
	
	@Option(names= {"-op", "--operation"}, description = "SPARQL query operation (update, construct, select, split). Default is update")
	QueryOperations queryOperation = QueryOperations.update;
	
	
	// SPARQL endpoint params
	@Option(names= {"-ep", "--sparql-endpoint"}, required = true, 
			description = "URL of the SPARQL Endpoint to query or RDF4J Server. e.g. http://graphdb.dumontierlab.com/repositories/test or http://graphdb.dumontierlab.com")
	String endpointUrl = null;
	
	@Option(names= {"-rep", "--repositoryId"}, description = "Repository ID for RDF4J Server. E.g. test")
	String repositoryId = null;

	@Option(names= {"-un", "--username"}, description = "Username used for SPARQL endpoint authentication")
	String username = null;

	@Option(names= {"-pw", "--password"}, description = "Password used for SPARQL endpoint authentication")
	String password = null;
	
	
	// SPARQL query variables
	@Option(names= {"--var-inputGraph"}, description = "Input graph URI variable to replace in the SPARQL query. E.g.: https://w3id.org/data2services/input")
	String varInputGraph;
	
	@Option(names= {"--var-outputGraph"}, description = "Output graph URI variable to replace in the SPARQL query. E.g.: https://w3id.org/data2services/output")
	String varOutputGraph;
	
	@Option(names= {"--var-serviceUrl"}, description = "A SPARQL service URL variable to replace in the SPARQL query. E.g.: http://localhost:7200/repositories/test")
	String varServiceUrl;
	
	
	// Split params
	@Option(names= {"--split-delimiter"}, description = "Delimiter for the Split operation. Default: ','")
	char splitDelimiter = ',';
	
	@Option(names= {"--split-quote"}, description = "Delimiter for the Trim operation. Default: '\"'")
	char splitQuote = '"'; // TODO: is null char good here? Should we let free?
	
	@Option(names= {"--split-property"}, description = "Property to split. e.g.: 'http://www.w3.org/2000/01/rdf-schema#label'")
	String splitProperty = null;
	
	@Option(names= {"--split-class"}, description = "Class to split. e.g.: 'http://w3id.org/biolink/vocab/GeneGrouping'")
	String splitClass = null;
	
	@Option(names= {"--split-delete"}, description = "Should we delete the splitted statements? Default: false")
	boolean splitDelete = false;
	
	@Option(names= {"--split-buffer-size"}, description = "Number of statements in the RDF4J model before loading it to the SPARQL endpoint. Default: 1000000")
	int splitBufferSize = 1000000;
	
	
	// URI expansion params
	@Option(names= {"-uex", "--uri-expansion"}, description = "Expan splitted values with URI, use \"infer\" to do it automatically")
	String uriExpansion = null;
	
}