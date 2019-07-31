package nl.unimaas.ids.operations;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class Split {

//	private SparqlExecutorInterface sparqlSelectExecutor;
//	private SparqlExecutorInterface sparqlUpdateExecutor;
	private HTTPRepository repo;
	// TODO: test if RDF4J HTTP repo perform better. 
	private HTTPRepository updateRepo;
	
	private HashMap<String, String> variablesHash;
	
	public Split(String endpointUrl, String endpointUpdateUrl, String username, String password, String[] variables) {
		// Abstract constructor
		
		repo = new HTTPRepository(endpointUrl, endpointUpdateUrl);
		//repo = new SPARQLRepository(endpointUrl);
		
		repo.setUsernameAndPassword(username, password);
		repo.initialize();
		
		updateRepo = new HTTPRepository(endpointUrl, endpointUpdateUrl);
		//updateRepo = new SPARQLRepository(endpointUpdateUrl);
		updateRepo.setUsernameAndPassword(username, password);
		updateRepo.initialize();
		
		variablesHash = new HashMap<String, String>();
		if (variables != null) {
	        for (int i=0; i<variables.length; i++)
	        {
	            String[] variableSplitted = variables[i].split(":", 2);
	            if (variableSplitted != null) {
		            // Split on first : (varGraph:http://graph gives {"?_varGraph": "http://graph"}
	            	variablesHash.put(variableSplitted[0], variableSplitted[1]);
	            }
	        }
		}
		
		// With SPARQL executors
//		sparqlSelectExecutor = SparqlOperationFactory.getSparqlExecutor(QueryOperation.select, endpointUrl, username, password, variables);
//		sparqlUpdateExecutor = SparqlOperationFactory.getSparqlExecutor(QueryOperation.update, endpointUrl, username, password, variables);
	}

	public TupleQueryResult executeSplit(String classToSplit, String propertyToSplit, String delimiter, boolean deleteSplittedTriples) throws RepositoryException, MalformedQueryException, IOException {
		String queryString = "SELECT ?s ?p ?toSplit ?g WHERE {"
				+ "    GRAPH ?g {"
				+ "    	?s a <" + classToSplit + "> ;"
				+ "      ?p ?toSplit ."
				+ "    	FILTER(?p = <" + propertyToSplit + ">)"
				+ "    } }";
		
		RepositoryConnection conn = repo.getConnection();
		RepositoryConnection updateConn = updateRepo.getConnection();
				
		TupleQuery query = conn.prepareTupleQuery(queryString);
		TupleQueryResult selectResults = query.evaluate();
		
		ValueFactory f = updateRepo.getValueFactory();
		
		IRI graphIri = f.createIRI("https://w3id.org/data2services/graph/split");
	    if (variablesHash.containsKey("outputGraph")) {
	    	System.out.println("Output Graph: " + variablesHash.get("outputGraph"));
	    	graphIri = f.createIRI(variablesHash.get("outputGraph"));
	    }
	    
	    System.out.println("Values to Split:");
		//TupleQueryResult result = query.evaluate();
		try {
		  while (selectResults.hasNext()) {
		    BindingSet bindingSet = selectResults.next();
		    IRI subjectIri = f.createIRI(bindingSet.getValue("s").stringValue());
		    IRI predicateIri = f.createIRI(bindingSet.getValue("p").stringValue());
		    String stringToSplit = bindingSet.getValue("toSplit").stringValue();
		    //IRI graphIri = f.createIRI(bindingSet.getValue("g").stringValue());
		    
		    if (stringToSplit.contains(delimiter)) {
		    	for (String splitFragment: stringToSplit.split(delimiter)) {           
				    System.out.println(splitFragment); 
				    updateConn.add(subjectIri, predicateIri, f.createLiteral(splitFragment), graphIri);
				}
		    }
		  }
		}
		finally {
			selectResults.close();
			conn.close();
			if (deleteSplittedTriples) {
				String deleteQueryString = "DELETE ?s ?p ?o WHERE {"    
						+ "?s a <" + classToSplit + "> ;"
						+ "?p ?o ."  	
						+ "FILTER(?p = <" + propertyToSplit + ">)"  
						+ "FILTER(contains(?o, '" + delimiter + "'))}";
				Update update = updateConn.prepareUpdate(deleteQueryString);
				update.execute();
			}
			updateConn.close();
		}
		return selectResults;
	}

}
