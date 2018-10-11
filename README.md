# About
This project perform a construct query on a defined SPARQL endpoint. It is possible to optionally define username and password.

This Docker container is part of the data2services pipeline (https://github.com/MaastrichtU-IDS/data2services-pipeline/).

# Docker
## Build
```shell
docker build -t data-constructor .
```
## Usage
```shell
# docker run -it --rm rdf-upload -?

Usage: rdfupload [-?] [-ep=<endpoint>] -if=<inputFile> [-pw=<passWord>]
                 -rep=<repository> [-uep=<updateEndpoint>] [-un=<userName>]
                 -url=<url>
  -?, --help   display a help message
      -ep, --endPoint=<endpoint>
               SPARQL endpoint URL
      -rq, --request-dir=<RequestDir>
               RDF file path
      -pw, --Password=<passWord>
               Password used for authentication
      -rep, --repository=<repository>
               Repository ID
      -un, --userName=<userName>
               Username userd for authentication
      -url, --graphdb-url=<url>
               URL to access GraphDB (e.g.: http://localhost:7200)

```
## Run
### For SPARQLRepository

* Linux / OSX

```shell
# Insert on graphdb.dumontierlab.com
docker run -it --rm -v /data/data-constructor:/data data-constructor -rq "/data" -url "http://graphdb.dumontierlab.com/repositories/test/statements" -un import_user -pw test

# Using local SPARQL endpoint
docker run -it --rm -v /data/data-constructor:/data rdf-upload -rq "/data" -url "http://localhost:7200/repositories/test"

# Using GraphDB docker
docker run -it --rm --link graphdb:graphdb -v /data/data-constructor:/data data-constructor -rq "/data" -url "http://graphdb:7200/repositories/test" -un import_user -pw test
```
* Windows

```powershell
docker run -it --rm -v /c/data/data-constructor:/data rdf-upload -rq "/data" -ep "http://localhost:7200/repositories/test"
```



