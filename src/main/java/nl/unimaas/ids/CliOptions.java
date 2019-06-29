package nl.unimaas.ids;

import nl.unimaas.ids.operations.QueryOperation;
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
	QueryOperation queryOperation = QueryOperation.update;
	
	@Option(names= {"-var", "--variables"}, arity = "0..*", paramLabel = "STRING", description = "Variables to replace in the SPARQL query. E.g.: varGraphInput:http://data2services/input varGraphOutput:http://data2services/output")
	String[] variables;
	
	@Option(names= {"-spd", "--split-delimiter"}, description = "Delimiter for the Split operation. Default: ','")
	String splitDelimiter = ",";
	
	@Option(names= {"-spp", "--split-property"}, description = "Property to split. e.g.: 'http://www.w3.org/2000/01/rdf-schema#label'")
	String splitProperty = null;
	
	@Option(names= {"-spc", "--split-class"}, description = "Class to split. e.g.: 'http://w3id.org/biolink/vocab/GeneGrouping'")
	String splitClass = null;
	
	@Option(names= {"--split-delete"}, description = "Should we delete the splitted statements? Default: false")
	boolean splitDelete = false;

	@Option(names= {"-ep", "--sparql-endpoint"}, description = "URL of the SPARQL Endpoint to query", required = true)
	String endpointUrl = null;
	
	@Option(names= {"-uep", "--update-sparql-endpoint"}, description = "URL of the Update SPARQL Endpoint to use for update operations (add /statements for RDF4J endpoints). Using -ep as default.", required = false)
	String endpointUpdateUrl = endpointUrl;

	@Option(names= {"-rep", "--repositoryId"}, description = "RDF4J Repository ID for HTTPRepository file upload (only required in case of RDF4JSPARQL or HTTP method)")
	String repositoryId = null;

	@Option(names= {"-un", "--username"}, description = "Username used for triplestore authentication")
	String username = null;

	@Option(names= {"-pw", "--password"}, description = "Password used for triplestore authentication")
	String password = null;


}