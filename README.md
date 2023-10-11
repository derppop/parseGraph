# CS 441 HW 1 - Graph Comparison

### Joseph David - jdavid31@uic.edu

## Functionality
GraphComparison utilizes [netgamesim](https://github.com/0x1DOCD00D/NetGameSim) 
for graph generation and perturbation. The graphs are split into sub-graphs 
of a size specified in [application.conf](src/main/resources/application.conf)
via the subGraphRatio and minSubGraphSize variables. The subgraphs are paired in such a way that every subgraph in the 
original graph is paired with every subgraph in the perturbed graph. These pairs are wrapped in a [Shard](src/main/scala/models/Shard.scala) object and serialized in the specified 
shard directory. Each shard is then passed to a mapper which computes the similarity score between each node in the original and perturbed subgraphs then
returns a (node-pair, similarity score) key value pair. The reducer takes these similarity scores and filters the ones that are above
the specified threshold and writes the score to the specified result directory.

## Usage
 Use the following command to produce the project jar.
 ````bash
 sbt assembly
 ````
Once the jar is built, the job can be run with the following command.
````
hadoop jar graphComparison.jar Main
````

Hadoop 3.2.1 was used to develop and test this project

## Configuration
The project configuration variables can be found in [application.conf](src/main/resources/application.conf).  
Along with the configurations for graph generation and perturbation that are inherited from [netgamesim](https://github.com/0x1DOCD00D/NetGameSim) 
the following is a list of config variables specific to this project.

### Job
* baseDirectory - The root directory of your s3 bucket or hdfs path
* shardDirectory - The folder you want shards to be placed in 
* graphDirectory - Local directory for graphs to be outputted and read from
* jobOutputDirectory - The folder you want the results of the job to be stored

### Preprocessor
* subGraphRatio - The percentage size you want the subgraphs to be relative to the original graph's size, represented by a decimal
* minSubGraphSize - The minimum amount of nodes you want to be in each subgraph, in case the ratio results in less than 1 node per subgraph

### Reducer
* simScoreThreshold - The minimum similarity score to be considered for output of the job

### SimRank
* propertySimWeight - Weight of properties similarity in computing similarity score
* childrenSimWeight - Weight of children similarity in computing similarity score
* depthSimWeight - Weight of depth similarity in computing similarity score
* branchFactorSimWeight - Weight of branchFactor similarity in computing similarity score
* storedValSimWeight - Weight of storedVal similarity in computing similarity score
