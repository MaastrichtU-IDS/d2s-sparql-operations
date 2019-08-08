package nl.unimaas.ids.operations;

import java.sql.SQLException;

import org.eclipse.rdf4j.repository.Repository;

public class SparqlOperationFactory {
	
	public static SparqlExecutorInterface getSparqlExecutor(QueryOperation operation, Repository repo,  String varInputGraph, String varOutputGraph, String varServiceUrl) throws SQLException, ClassNotFoundException {
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
