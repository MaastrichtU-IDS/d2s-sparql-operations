A Java CLI to upload RDF files, and execute [SPARQL](https://www.w3.org/TR/sparql11-query/) queries from string, URL or multiple files using [RDF4J](http://rdf4j.org/).

* The user can execute **SPARQL queries** by
  * Passing a SPARQL **query string** in `-sp` param 
  * Providing a **URL** in `-f` param
  * Providing the **URL** of a **GitHub repository** containing `.rq` files to execute in `-f` param
  * Providing the **path to a directory** where the queries are stored in `.rq` text files and executed in the **alphabetical order** of their filename. 
  * A **YAML file** with multiple ordered queries.
* **Update**, **construct** and **select** SPARQL operations supported.
* It is possible to optionally define **username** and **password** for the SPARQL endpoint.

> See the [Data2Services framework documentation](http://d2s.semanticscience.org/) to run d2s-sparql-operations as part of workflows to generate RDF knowledge graph from structured data. 

---

# Use the jar

[![Java version](https://img.shields.io/badge/java-11-blue)](https://openjdk.java.net/install/) [![Download jar](https://img.shields.io/badge/download-jar-blueviolet)](https://github.com/MaastrichtU-IDS/d2s-sparql-operations/releases/latest/download/sparql-operations.jar) 

Download the `.jar` file from the latest GitHub release [here](https://github.com/MaastrichtU-IDS/d2s-sparql-operations/releases/latest/download/sparql-operations.jar), you can use this command to do it automatically in a Bash terminal:

```bash
wget https://github.com/MaastrichtU-IDS/d2s-sparql-operations/releases/latest/download/sparql-operations.jar
```

Move the jar somewhere you can call it easily, e.g. in a `bin` folder in your home folder:

```bash
mkdir -p ~/bin && mv sparql-operations.jar ~/bin/sparql-operations.jar
```

* Run the jar to upload RDF files:

```bash
java -jar ~/bin/sparql-operations.jar -o upload -i "*.ttl" -e "https://graphdb.dumontierlab.com/repositories/test/statements" -u 'username' -p 'password' -g "http://my-graph.com"
```

> Optionally use `-g "http://my-graph.com"` to specify a graph to upload the data to

You can also define the username and password using environment variables:

```bash
export D2S_USERNAME=myusername
export D2S_PASSWORD=mypassword
```

* Execute a SPARQL query provided as argument:

```bash
java -jar ~/bin/sparql-operations.jar -o select -q "SELECT * WHERE {?s ?p ?o .} LIMIT 10" -e "https://graphdb.dumontierlab.com/repositories/test"
```

See below for more example to execute SPARQL queries, and various operations.

### Build the jar

Compile the jar file from the source code:

```bash
mvn clean package
```

Move it:

```bash
mv target/sparql-operations-*-jar-with-dependencies.jar ~/bin/sparql-operations.jar
```

# Use Docker

### Pull

Available on [DockerHub](https://hub.docker.com/r/umids/d2s-sparql-operations) the `latest` image is automatically built from latest branch `master` commit on [GitHub](https://github.com/MaastrichtU-IDS/d2s-sparql-operations).

```bash
docker pull umids/d2s-sparql-operations
```

---

### Build

You can also clone the [GitHub repository](https://github.com/MaastrichtU-IDS/d2s-sparql-operations) and build the docker image locally (**unecessary if you do** `docker pull`)

```bash
git clone https://github.com/MaastrichtU-IDS/d2s-sparql-operations
cd d2s-sparql-operations
docker build -t umids/d2s-sparql-operations .
```
---

### Run

N.B.: you will need to remove the `\` and make the `docker run` commands one-line for **Windows PowerShell**.

#### Usage

```bash
docker run -it --rm umids/d2s-sparql-operations -h
```

---

#### Upload

Upload RDF files to a SPARQL endpoint:

```bash
docker run -it --rm -v $(pwd):/data umids/d2s-sparql-operations -op upload \
  -i "*.ttl" \
  -e "https://graphdb.dumontierlab.com/repositories/test/statements" \
  -u $USERNAME -pw $PASSWORD \
  -g "http://my-graph.com"
```

---

#### Select

On [DBpedia](http://dbpedia.org/sparql) using a SPARQL query string as argument.

```bash
docker run -it --rm umids/d2s-sparql-operations -op select \
  -q "select distinct ?Concept where {[] a ?Concept} LIMIT 10" \
  -e "http://dbpedia.org/sparql"
```

---

#### Update

Multiple `INSERT` on [graphdb.dumontierlab.com](https://graphdb.dumontierlab.com/), using files in a repository from the local file system.

```bash
docker run -it --rm umids/d2s-sparql-operations \
  -e "https://graphdb.dumontierlab.com" -rep "test" \
  #-e "https://graphdb.dumontierlab.com/repositories/test/statements" \
  -op update -u $USERNAME -pw $PASSWORD \
  -i "https://github.com/MaastrichtU-IDS/d2s-sparql-operations/tree/master/src/main/resources/insert-examples"
```

* Note that GraphDB and RDF4J Server require to add `/statements` at the end of the endpoint URL when doing an update.

---

#### Construct

On [graphdb.dumontierlab.com](https://graphdb.dumontierlab.com/) using GitHub URL to get the SPARQL query from a file.

```bash
docker run -it --rm umids/d2s-sparql-operations -op construct \
  -e "https://graphdb.dumontierlab.com/repositories/ncats-red-kg" \
  -i "https://raw.githubusercontent.com/MaastrichtU-IDS/d2s-sparql-operations/master/src/main/resources/example-construct-pathways.rq" 
```

---


#### GitHub repository

We crawl the [example GitHub repository](https://github.com/MaastrichtU-IDS/d2s-sparql-operations/tree/master/src/main/resources/select-examples) and execute each `.rq` file.

```bash
docker run -it --rm umids/d2s-sparql-operations \
  -op select -e "http://dbpedia.org/sparql" \
  -i "https://github.com/MaastrichtU-IDS/d2s-sparql-operations/tree/master/src/main/resources/select-examples" 
```

> Crawling GitHub repository from URL is based on HTML parsing, hence might be unstable

---

#### YAML

A YAML file can be used to provide multiple ordered queries. See [example from GitHub](https://github.com/MaastrichtU-IDS/d2s-sparql-operations/blob/master/src/main/resources/example-queries.yaml).

```bash
docker run -it --rm umids/d2s-sparql-operations \
  -op select -e "http://dbpedia.org/sparql" \
  -i "https://raw.githubusercontent.com/MaastrichtU-IDS/d2s-sparql-operations/master/src/main/resources/example-queries.yaml"
```

---

#### Split

`Beta` To split an object into multiple statements using a delimiter, and insert the statements generated by the split in the same graph. 

E.g.: a statement with value "1234,345,768" would be splitted in 3 statements "1234", "345" and "768".

```bash
docker run -it \
  umids/d2s-sparql-operations -op split \
  --split-property "http://w3id.org/biolink/vocab/has_participant" \
  --split-class "http://w3id.org/biolink/vocab/GeneGrouping" \
  --split-delimiter "," \
  --split-delete \ # Delete the splitted statement
  --uri-expansion "https://w3id.org/d2s/" \ # Use 'infer' to do it automatically using prefixcommons
  #--trim-delimiter '"' \
  -e "https://graphdb.dumontierlab.com" \ # RDF4J server URL
  -rep "test" \ # RDF4J server repository ID
  -u USERNAME -pw PASSWORD
  
# For SPARQLRepository
#  -e "https://graphdb.dumontierlab.com/repositories/test" \
#  -uep "https://graphdb.dumontierlab.com/repositories/test/statements" \
```

---

# Set variables

3 variables can be set in the SPARQL queries using a `?_`: `?_input`, `?_output` and `?_service`. See example:

```SPARQL
INSERT {
  GRAPH <?_output> {
    ?Concept a <https://w3id.org/d2s/Concept> .
  }
} WHERE {
  SERVICE <?_service> {
    GRAPH <?_input> {
      SELECT * {
        [] a ?Concept .
      } LIMIT 10 
} } }
```

Execute:

```bash
docker run -it --rm umids/d2s-sparql-operations \
  -op update -e "https://graphdb.dumontierlab.com/repositories/test/statements" \
  -u $USERNAME -pw $PASSWORD \
  -i "https://raw.githubusercontent.com/MaastrichtU-IDS/d2s-sparql-operations/master/src/main/resources/example-insert-variables.rq" \
  --var-input http://www.ontotext.com/explicit \
  --var-output https://w3id.org/d2s/output \
  --var-service http://localhost:7200/repositories/test
```

---

# Examples

From [data2services-transform-repository](https://github.com/MaastrichtU-IDS/data2services-transform-repository), use a [federated query](https://github.com/MaastrichtU-IDS/data2services-transform-repository/blob/master/sparql/insert-biolink/drugbank/insert_drugbank_drug_CategoryOrganism.rq) to transform generic RDF generated by [AutoR2RML](https://github.com/amalic/AutoR2RML) and [xml2rdf](https://github.com/MaastrichtU-IDS/xml2rdf) to the [BioLink](https://biolink.github.io/biolink-model/docs/) model, and load it to a different repository.

```bash
# DrugBank
docker run -it --rm -v "$PWD/sparql/insert-biolink/drugbank":/data \
  umids/d2s-sparql-operations \
  -i "/data" -u USERNAME -pw PASSWORD \
  -e "https://graphdb.dumontierlab.com/repositories/ncats-test/statements" \
  --var-service http://localhost:7200/repositories/test \ 
  --var-input http://data2services/graph/xml2rdf \ 
  --var-output https://w3id.org/d2s/graph/biolink/drugbank

# HGNC
docker run -it --rm -v "$PWD/sparql/insert-biolink/hgnc":/data \
  umids/d2s-sparql-operations \
  -i "/data" -u USERNAME -pw PASSWORD \
  -e "https://graphdb.dumontierlab.com/repositories/ncats-test/statements" \
  --var-service http://localhost:7200/repositories/test \
  --var-input http://data2services/graph/autor2rml \
  --var-output https://w3id.org/d2s/graph/biolink/hgnc
```

