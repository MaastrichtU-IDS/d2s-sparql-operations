package nl.unimaas.ids.operations;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to upload to GraphDB SPARQL endpoint
 */
public class Split {

	protected Logger logger = LoggerFactory.getLogger(Split.class.getName());
	
	private Repository repo;
	
	private String varOutputGraph;
	
	private int splitBufferSize;
	
	public Split(Repository repo, String varOutputGraph, int splitBufferSize) {
		this.repo = repo;
		this.varOutputGraph = varOutputGraph;
		this.splitBufferSize = splitBufferSize;
		System.out.println("Split buffer size: " + splitBufferSize);

		// With SPARQL executors
		// sparqlSelectExecutor =
		// SparqlOperationFactory.getSparqlExecutor(QueryOperation.select,
		// endpointUrl, username, password, variables);
		// sparqlUpdateExecutor =
		// SparqlOperationFactory.getSparqlExecutor(QueryOperation.update,
		// endpointUrl, username, password, variables);
	}

	public TupleQueryResult executeSplit(String classToSplit,
			String propertyToSplit, char splitDelimiter,
			char splitQuote, boolean deleteSplittedTriples,
			String uriExpansion) throws RepositoryException,
			MalformedQueryException, IOException {

		String queryString = "SELECT ?s ?p ?toSplit ?g WHERE {"
				+ "    GRAPH ?g {" + "    	?s a <" + classToSplit + "> ;"
				+ "      ?p ?toSplit ." + "    	FILTER(?p = <"
				+ propertyToSplit + ">)." + "FILTER(regex(?toSplit, '"
				+ splitDelimiter + "'))" + "    } }";

		System.out.println(queryString);
		System.out.println();

		RepositoryConnection conn = repo.getConnection();

		TupleQuery query = conn.prepareTupleQuery(queryString);
		TupleQueryResult selectResults = query.evaluate();

		ValueFactory f = repo.getValueFactory();

		// If graph not defined in params, then we use the graph from the
		// statement
		IRI graphIri = null;
		boolean graphFromParam = false;
		if (varOutputGraph != null) {
			graphFromParam = true;
			graphIri = f.createIRI(varOutputGraph);
		}

		ModelBuilder builder = new ModelBuilder();
		Model bulkUpdate = builder.build();
		Map<String, String> registery = null;
		Map<String, String> prefixToReplace = null;

		if (uriExpansion != null && uriExpansion.equals("infer")) {
			// Identifier resolution

			File registeryFile = new File("registery.json");
			if (!registeryFile.exists()) {
				FileUtils.copyURLToFile(new URL("http://prefix.cc/context"),
						new File("registery.json"));
			}

			ObjectMapper mapper = new ObjectMapper();
			registery = new HashMap<String, String>();
			JsonNode node = mapper.readTree(registeryFile);
			JsonNode context = node.get("@context");
			Iterator<Map.Entry<String, JsonNode>> iter = context.fields();

			int regCount = 0;
			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> entry = iter.next();
				registery.put(entry.getKey(), entry.getValue().textValue());
				regCount++;
			}

			System.out.println("Registery build finished, total items: "
					+ regCount);

			// Some prefixes are not covered by PrefixCommons at the moment and will be added here.
			prefixToReplace = new HashMap<String, String>();
			prefixToReplace.put("keggcompound", "kegg");
			prefixToReplace.put("keggdrug", "kegg");
			prefixToReplace.put("drugbank", "drugbank");
			prefixToReplace.put("uniprotkb", "uniprot");
			prefixToReplace.put("clinicaltrials.gov", "clinicaltrials");
			prefixToReplace.put("drugsproductdatabase(dpd)", "dpd");
			prefixToReplace.put("nationaldrugcodedirectory", "ndc");
			prefixToReplace.put("therapeutictargetsdatabase", "ttd");
			prefixToReplace.put("fdadruglabelatdailymed", "dailymed");
			prefixToReplace.put("chebi:chebi", "chebi");
			prefixToReplace.put("pubchemcompound", "b2rpubchem");
		}

		int count = 0;
		int accum = 0;

		Map<String, String> availablePref = new HashMap<String, String>();

		try {
			while (selectResults.hasNext()) {
				BindingSet bindingSet = selectResults.next();

				IRI subjectIri = f.createIRI(bindingSet.getValue("s").stringValue());
				IRI predicateIri = f.createIRI(bindingSet.getValue("p").stringValue());
				String stringToSplit = bindingSet.getValue("toSplit").stringValue();
				// Use graph IRI directly from the data, if no graph URI provided
				if (!graphFromParam) {
					graphIri = f.createIRI(bindingSet.getValue("g").stringValue());
				}
				
				CSVFormat format = CSVFormat.newFormat(splitDelimiter);
				format = format.withQuote(splitQuote);
				
				CSVParser parser = CSVParser.parse(stringToSplit, format);
				
				for (CSVRecord csvRecord : parser) {
					for (int i=0; i<csvRecord.size(); i++){
						String splitFragment = csvRecord.get(i);
	
						if (uriExpansion != null) {
							if (!uriExpansion.equals("infer")) {
								splitFragment = uriExpansion + splitFragment;
								bulkUpdate.add(subjectIri, predicateIri,
										f.createIRI(splitFragment), graphIri);
	
							} else if (uriExpansion.equals("infer")) {
	
								if (splitFragment.indexOf("(") != -1) {
									splitFragment = splitFragment.substring(0,
											splitFragment.indexOf("("));
								}
								// splitFragment =
								// splitFragment.replaceAll("\\[.*?\\]", "").trim();
								// splitFragment =
								// splitFragment.replaceAll("\\\\([^()]*\\\\)",
								// "").trim();
								// splitFragment =
								// splitFragment.replaceAll("\\(.*?\\)", "").trim();
								// splitFragment =
								// splitFragment.replaceAll("[()?;{}]+",
								// " ").trim();
	
								if (splitFragment.contains(":")) {
	
									int p = 0;
	
									if (splitFragment.contains("url")) {
										p = splitFragment.indexOf(":");
									} else {
										p = splitFragment.lastIndexOf(":");
									}
	
									String prefix = splitFragment.substring(0, p)
											.toLowerCase().replace(" ", "").trim();
									String id = splitFragment.substring(p + 1);
	
									availablePref.put(prefix, "");
	
									if (prefixToReplace.containsKey(prefix)) {
										prefix = prefixToReplace.get(prefix);
									}
	
									if (registery.containsKey(prefix)) {
										// System.out.println(prefix);
										splitFragment = registery.get(prefix) + id;
										predicateIri = f.createIRI(propertyToSplit
												.substring(0, propertyToSplit
														.lastIndexOf("/") + 1)
												+ "x-" + prefix);
										bulkUpdate.add(subjectIri, predicateIri,
												f.createIRI(splitFragment),
												graphIri);
									} else {
										predicateIri = f.createIRI(propertyToSplit
												.substring(0, propertyToSplit
														.lastIndexOf("/") + 1)
												+ "x-ref");
										bulkUpdate.add(subjectIri, predicateIri,
												f.createLiteral(splitFragment),
												graphIri);
	
										// for (Entry<String, String[]> e :
										// registery.entrySet()) {
										// if (splitFragment.contains(e.getKey())) {
										//
										// splitFragment =
										// registery.get(e.getKey())[2] + id;
										//
										// predicateIri =
										// f.createIRI(propertyToSplit.substring(0,propertyToSplit.lastIndexOf("/")+1)+"x-"+prefix);
										//
										// bulkUpdate.add(subjectIri, predicateIri,
										// f.createLiteral(splitFragment),
										// graphIri);
										// }
										// }
									}
									
								} else {
									bulkUpdate.add(subjectIri, predicateIri,
											f.createLiteral(splitFragment),
											graphIri);
								} // if(splitFragment.contains(":"))
							} // if(!uriExpansion.equals("infer"))
							
						} else {
							bulkUpdate.add(subjectIri, predicateIri,
									f.createLiteral(splitFragment), graphIri);
						} // if(uriExpansion != null)
						count++;
					}
				} // for loop
				
				if ((count > splitBufferSize)) {
					conn.add(bulkUpdate, graphIri);
					bulkUpdate = builder.build();

					accum += count;
					System.out.println("Updated triples: " + accum);
					count = 0;

				} else if ((count <= splitBufferSize) && !selectResults.hasNext()) {
					conn.add(bulkUpdate, graphIri);
					accum += count;
					System.out.println("Total updated triples: " + accum);
				}
			} // while results
			// print the content of the cross references available in pharmgkb
			// Iterator it = availablePref.entrySet().iterator();
			// while (it.hasNext()) {
			// Map.Entry pair = (Map.Entry)it.next();
			// System.out.println(pair.getKey());
			// it.remove(); // avoids a ConcurrentModificationException
			// }

		} finally {
			selectResults.close();
			if (deleteSplittedTriples) {
				String deleteQueryString = "DELETE { " + "GRAPH ?g {"
						+ "?s ?p ?o." + "} " + "}WHERE {" + "GRAPH ?g {"
						+ "?s a <" + classToSplit + "> ;" + "?p ?o ."
						+ "FILTER(?p = <" + propertyToSplit + ">)."
						+ "FILTER(regex(?o, '" + splitDelimiter + "'))} } ";

				System.out.println();
				System.out.println(deleteQueryString);

				Update update = conn.prepareUpdate(deleteQueryString);
				update.execute();
			}
			conn.close();
		}
		return selectResults;
	}

	public static boolean isValid(String url) {
		/* Try creating a valid URL */
		try {
			new URL(url).toURI();
			return true;
		}
		
		// If there was an Exception
		// while creating URL object
		catch (Exception e) {
			return false;
		}
	}

}