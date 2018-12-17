package nl.unimaas.ids.operations;

import java.sql.SQLException;

public class SparqlOperationFactory {
	
	public static SparqlExecutorInterface getSparqlExecutor(QueryOperation operation, String endpoint, String username, String password, String[] variables) throws SQLException, ClassNotFoundException {
		SparqlExecutorInterface q;
		
		switch (operation) {
        case update:
 		 q = new SparqlUpdate(endpoint, username, password, variables);
       	 break;
        case construct:
    	 q = new SparqlConstruct(endpoint, username, password, variables);
       	 break;
        case select:
       	 q = new SparqlSelect(endpoint, username, password, variables);
       	 break;
   	 	default:
   		 throw new UnsupportedOperationException("Supported operations: update, construct and select.");
		}
		return q;
	}


}
