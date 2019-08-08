package nl.unimaas.ids.operations;

import java.sql.SQLException;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class SparqlRepositoryFactory {
	
	// Try to load HTTPRepository, load SPARQLRepository if fail
	public static Repository getRepository(String endpointUrl, String username, String password) throws SQLException, ClassNotFoundException {
		try {
			HTTPRepository httpRepo;
        	httpRepo = new HTTPRepository(endpointUrl);
        	httpRepo.setUsernameAndPassword(username, password);
        	httpRepo.initialize();
        	return httpRepo;
		} catch (Exception e) {
			e.printStackTrace();
			SPARQLRepository sparqlRepo;
   	 		sparqlRepo = new SPARQLRepository(endpointUrl);
   	 		sparqlRepo.setUsernameAndPassword(username, password);
   	 		sparqlRepo.initialize();
   	 		return sparqlRepo;
		}
	}
 
}