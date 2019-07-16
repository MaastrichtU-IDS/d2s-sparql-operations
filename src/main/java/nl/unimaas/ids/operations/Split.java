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
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.util.*;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class Split {

//	private SparqlExecutorInterface sparqlSelectExecutor;
//	private SparqlExecutorInterface sparqlUpdateExecutor;
	private HTTPRepository repo;
	// TODO: test if RDF4J HTTP repo perform better. 
	private HTTPRepository updateRepo;
	
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
		
		HashMap<String, String> variablesHash = new HashMap<String, String>();
		if (variables != null) {
	        for (int i=0; i<variables.length; i++)
	        {
	            String[] variableSplitted = variables[i].split(":", 2);
	            if (variableSplitted != null) {
		            // Split on first : (varGraph:http://graph gives {"?_varGraph": "http://graph"}
	            	variablesHash.put("\\?_" + variableSplitted[0], variableSplitted[1]);
	            }
	        }
		}
		
		// With SPARQL executors
//		sparqlSelectExecutor = SparqlOperationFactory.getSparqlExecutor(QueryOperation.select, endpointUrl, username, password, variables);
//		sparqlUpdateExecutor = SparqlOperationFactory.getSparqlExecutor(QueryOperation.update, endpointUrl, username, password, variables);
	}

	public TupleQueryResult executeSplit(String classToSplit, String propertyToSplit, String delimiter, boolean deleteSplittedTriples, String trimDelimiter, String uriExpansion) throws RepositoryException, MalformedQueryException, IOException {
		String queryString = "SELECT ?s ?p ?toSplit ?g WHERE {"
				+ "    GRAPH ?g {"
				+ "    	?s a <" + classToSplit + "> ;"
				+ "      ?p ?toSplit ."
				+ "    	FILTER(?p = <" + propertyToSplit + ">)."
				+ "FILTER(regex(?toSplit, '" + delimiter+"(?=\")" + "'))"
				+ "    } }";
		
		System.out.println(queryString);
		System.out.println();
		
		RepositoryConnection conn = repo.getConnection();
		RepositoryConnection updateConn = updateRepo.getConnection();
				
		TupleQuery query = conn.prepareTupleQuery(queryString);
		TupleQueryResult selectResults = query.evaluate();
		
		ValueFactory f = updateRepo.getValueFactory();
		
		System.out.println("Values to Split:");
		//TupleQueryResult result = query.evaluate();
		
    	ModelBuilder builder = new ModelBuilder();
    	
    	Model bulkUpdate = builder.build();
    	
    	int count = 0;
    	int accum = 0;
    	
		try {
		  while (selectResults.hasNext()) {
		    BindingSet bindingSet = selectResults.next();
		    
		    IRI subjectIri = f.createIRI(bindingSet.getValue("s").stringValue());
		    IRI predicateIri = f.createIRI(bindingSet.getValue("p").stringValue());
		    String stringToSplit = bindingSet.getValue("toSplit").stringValue();
		    //IRI graphIri = f.createIRI(bindingSet.getValue("g").stringValue());
		    IRI graphIri = f.createIRI("http://test/split");
		    
		    	
	    	String[] splitFragments = stringToSplit.split(delimiter+"(?=\")");
	    	
	    	for (String splitFragment: splitFragments) {          
			    
	    		if(trimDelimiter != null) {
			    	splitFragment = splitFragment.replaceAll("^"+trimDelimiter+"|"+trimDelimiter+"$", "");;
			    }
	    		
	    		if(uriExpansion != null) {
	    			splitFragment = uriExpansion + splitFragment;
	    		}
			    
			    bulkUpdate.add(subjectIri, predicateIri, f.createLiteral(splitFragment), graphIri);
			    count++;
			    //System.out.println(count); 
			}
		    
		    if((count > 10000)) {
	    		
		    	updateConn.add(bulkUpdate, graphIri);
	    		bulkUpdate = builder.build();
	    		
	    		accum += count;
	    		System.out.println("Updated triples: "+ accum); 
	    		count = 0;
	    		
	    	}else if((count <= 10000) && !selectResults.hasNext()) {
	    		updateConn.add(bulkUpdate, graphIri);
	    		accum += count;
	    		System.out.println("Total updated triples: " + accum); 
	    	}
		    
		  }
		}
		finally {
			selectResults.close();
			conn.close();
			if (deleteSplittedTriples) {
				String deleteQueryString = "DELETE { "
						+ "GRAPH ?g {"
						+ "?s ?p ?o."
						+ "} "
						+ "}WHERE {"    
						+ "GRAPH ?g {"    	
						+ "?s a <" + classToSplit + "> ;"
						+ "?p ?o ."  	
						+ "FILTER(?p = <" + propertyToSplit + ">)."  
						+ "FILTER(regex(?o, '" + delimiter+"(?=\")" + "'))} } ";
				
				System.out.println();
				System.out.println(deleteQueryString);
				
				Update update = updateConn.prepareUpdate(deleteQueryString);
				update.execute();
			}
			updateConn.close();
		}
		return selectResults;
	}

}