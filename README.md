# About
A project to execute a set of SPARQL queries using rdf4j. 

The user has to provide the path to the directory where the queries are stored in `.rq` text files.

*Update*, *construct* and *select* queries are currently supported. 

It is possible to optionally define username and password for the SPARQL endpoint.

Examples queries: https://github.com/vemonet/insert-data2services



# Docker
## Build
```shell
docker build -t rdf4j-sparql-operations .
```
## Usage
```shell
docker run -it --rm rdf4j-sparql-operations -?
```
## Run
```shell
# Update (insert) on graphdb.dumontierlab.com 
# GraphDB requires to add /statements at the end of the endpoint URL for INSERT
docker run -it --rm -v /data/operations:/data rdf4j-sparql-operations -rq "/data" -url "http://graphdb.dumontierlab.com/repositories/test/statements" -un username -pw password

# Construct using local SPARQL endpoint
docker run -it --rm -v /data/operations:/data rdf4j-sparql-operations -rq "/data" -url "http://localhost:7200/repositories/test" -op "construct"

# Using GraphDB docker
docker run -it --rm --link graphdb:graphdb -v /data/operations:/data rdf4j-sparql-operations -rq "/data" -url "http://graphdb:7200/repositories/test/statements"
```
