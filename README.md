# About
A project to execute a set of SPARQL queries using rdf4j. 

The user has to provide the path to the directory where the queries are stored in `.rq` text files.

*Insert* and *construct* queries are currently supported, *select* to come soon. 

It is possible to optionally define username and password for the SPARQL endpoint.



TODO: If doing an insert we will generate Metadata for the generated dataset using  

# Docker
## Build
```shell
docker build -t sparql-dataformer .
```
## Usage
```shell
docker run -it --rm sparql-dataformer -?
```
## Run
```shell
# Insert on graphdb.dumontierlab.com 
# GraphDB requires to add /statements at the end of the endpoint URL for INSERT
docker run -it --rm -v /data/dataformer:/data sparql-dataformer -rq "/data" -url "http://graphdb.dumontierlab.com/repositories/test/statements" -un username -pw password

# Construct using local SPARQL endpoint
docker run -it --rm -v /data/dataformer:/data sparql-dataformer -rq "/data" -url "http://localhost:7200/repositories/test" -op "construct"

# Using GraphDB docker
docker run -it --rm --link graphdb:graphdb -v /data/dataformer:/data sparql-dataformer -rq "/data" -url "http://graphdb:7200/repositories/test/statements"
```



# ShEx validation

http://shexjava.lille.inria.fr/

```shell
# Start the UI
java -jar shexjapp-0.0.1.jar

# Commandline
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass="fr.inria.lille.shexjava.commandLine.Validate" -Dexec.args="-s ../../shexTest/schemas/1dotSemi.shex -d file:///home/jdusart/Documents/Shex/workspace/shexTest/validation/Is1_Ip1_Io1.ttl -l http://a.example/S1 -f http://a.example/s1 -a recursive"
```

