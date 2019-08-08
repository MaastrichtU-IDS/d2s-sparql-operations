package nl.unimaas.ids.operations.queries;

import java.sql.SQLException;

import nl.unimaas.ids.operations.QueryOperations;

import org.eclipse.rdf4j.repository.Repository;

public class SparqlQueryFactory {
	
	public static SparqlExecutorInterface getSparqlExecutor(QueryOperations operation, Repository repo,  String varInputGraph, String varOutputGraph, String varServiceUrl) throws SQLException, ClassNotFoundException {
		SparqlExecutorInterface q;
		
		switch (operation) {
        case update:
 		 q = new SparqlUpdate(repo, varInputGraph, varOutputGraph, varServiceUrl);
       	 break;
        case construct:
    	 q = new SparqlConstruct(repo, varInputGraph, varOutputGraph, varServiceUrl);
       	 break;
        case select:
       	 q = new SparqlSelect(repo, varInputGraph, varOutputGraph, varServiceUrl);
       	 break;
   	 	default:
   		 throw new UnsupportedOperationException("Supported operations: update, construct and select.");
		}
		return q;
	}

}
