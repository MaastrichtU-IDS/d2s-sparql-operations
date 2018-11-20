package nl.unimaas.ids.sparql;

import java.sql.SQLException;

public class SparqlQueryFactory {
	
	public static SparqlQueryInterface getSparqlQuery(QueryOperations operation, String endpoint, String username, String password) throws SQLException, ClassNotFoundException {
		SparqlQueryInterface q;
		
		switch (operation) {
        case insert:
 		 q = new SparqlInsert(endpoint, username, password);
       	 break;
        case construct:
    	 q = new SparqlConstruct(endpoint, username, password);
       	 break;
        case select:
       	 q = new SparqlSelect(endpoint, username, password);
       	 break;
   	 	default:
   		 throw new UnsupportedOperationException("Supported operations: insert and construct. select coming soon.");
		}
		return q;
	}


}
