package nl.unimaas.ids.operations;

import java.sql.SQLException;

public class SparqlOperationFactory {
	
	public static SparqlExecutorInterface getSparqlExecutor(QueryOperation operation, String endpoint, String username, String password,  String varInputGraph, String varOutputGraph, String varServiceUrl) throws SQLException, ClassNotFoundException {
		SparqlExecutorInterface q;
		
		switch (operation) {
        case update:
 		 q = new SparqlUpdate(endpoint, username, password, varInputGraph, varOutputGraph, varServiceUrl);
       	 break;
        case construct:
    	 q = new SparqlConstruct(endpoint, username, password, varInputGraph, varOutputGraph, varServiceUrl);
       	 break;
        case select:
       	 q = new SparqlSelect(endpoint, username, password, varInputGraph, varOutputGraph, varServiceUrl);
       	 break;
   	 	default:
   		 throw new UnsupportedOperationException("Supported operations: update, construct and select.");
		}
		return q;
	}


}
