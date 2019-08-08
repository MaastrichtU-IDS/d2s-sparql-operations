package nl.unimaas.ids;

import java.sql.SQLException;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class SparqlRepositoryFactory {
	
	// Try to load HTTPRepository, load SPARQLRepository if fail
	public static Repository getRepository(String endpointUrl, String username, String password) throws SQLException, ClassNotFoundException {
		try {
			HTTPRepository httpRepo = new HTTPRepository(endpointUrl);
        	httpRepo.setUsernameAndPassword(username, password);
        	httpRepo.initialize();
        	return httpRepo;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println("Triplestore is not a RDF4J server, using SPARQLRepository instead of HTTPRepository");
			SPARQLRepository sparqlRepo = new SPARQLRepository(endpointUrl);
   	 		sparqlRepo.setUsernameAndPassword(username, password);
   	 		sparqlRepo.initialize();
   	 		return sparqlRepo;
		}
	}
 
}