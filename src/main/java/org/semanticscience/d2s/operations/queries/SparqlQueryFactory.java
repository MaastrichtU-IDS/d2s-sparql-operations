package org.semanticscience.d2s.operations.queries;

import java.sql.SQLException;

import org.eclipse.rdf4j.repository.Repository;
import org.semanticscience.d2s.operations.QueryOperations;

public class SparqlQueryFactory {
	
	public static SparqlExecutorInterface getSparqlExecutor(QueryOperations operation, Repository repo,  String varInput, String varOutput, String varService) throws SQLException, ClassNotFoundException {
		SparqlExecutorInterface q;
		
		switch (operation) {
        case update:
 		 q = new SparqlUpdate(repo, varInput, varOutput, varService);
       	 break;
        case construct:
    	 q = new SparqlConstruct(repo, varInput, varOutput, varService);
       	 break;
        case select:
       	 q = new SparqlSelect(repo, varInput, varOutput, varService);
       	 break;
   	 	default:
   		 throw new UnsupportedOperationException("Supported operations: update, construct and select.");
		}
		return q;
	}

}
