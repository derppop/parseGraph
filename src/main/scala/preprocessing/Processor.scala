package preprocessing

import NetGraphAlgebraDefs.*
import NetGraphAlgebraDefs.NetModelAlgebra.{outputDirectory, perturbationCoeff, propValueRange}
import Utilz.NGSConstants

import java.io.{DataInput, DataInputStream, DataOutput, DataOutputStream, File, FileInputStream, FileOutputStream}
import scala.collection.mutable
import scala.collection.mutable.Set
import scala.jdk.CollectionConverters.*
import scala.math.max
import models.Shard
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.slf4j.LoggerFactory
import util.Config.Preprocessor.{minSubGraphSize, subGraphRatio}
import util.Config.Job.shardDirectory
import org.apache.hadoop.io.IOUtils


object Processor{
  private val logger = LoggerFactory.getLogger(Processor.getClass)

  def generateGraphs(): (String, String) = {
    // Generate graph
    val graph: NetGraph = NetModelAlgebra().get
    val graphName = NGSConstants.OUTPUTFILENAME
    val perturbedGraphName = graphName + ".perturbed"
    graph.persist(outputDirectory, graphName) // Serialize original graph
    // Perturb graph
    val (perturbedGraph, changes): GraphPerturbationAlgebra#GraphPerturbationTuple = GraphPerturbationAlgebra(graph.copy)
    perturbedGraph.persist(outputDirectory, perturbedGraphName) // Serialize perturbed graph
    GraphPerturbationAlgebra.persist(changes, outputDirectory.concat(graphName.concat(".yaml")))
    (graphName, perturbedGraphName)
  }
  def createShards(graphName: String, perturbedGraphName: String): Int = {
    val graph = NetGraph.load(fileName = graphName, outputDirectory).get
    val perturbedGraph = NetGraph.load(fileName = perturbedGraphName, outputDirectory).get
    val subGraphs = graph.sm.nodes().asScala.grouped(max(minSubGraphSize, (graph.totalNodes * subGraphRatio).toInt)).toList
    logger.info(s"Split original graph into ${subGraphs.size} sub-graphs")
    val perturbedSubGraphs = perturbedGraph.sm.nodes().asScala.grouped(max(minSubGraphSize, (perturbedGraph.totalNodes * subGraphRatio).toInt)).toList
    logger.info(s"Split perturbed graph into ${perturbedSubGraphs.size} sub-graphs")

    // Place pairs into shard objects (also used in InputFormat) with shard id's
    var shardID = 0
    val shards: List[Shard] = subGraphs.flatMap(subgraph => perturbedSubGraphs.map(perturbedSubGraph => {
      shardID += 1
      Shard(shardID, subgraph.toSet, perturbedSubGraph.toSet)
    }))
    logger.info(s"Created ${shards.size} shards")

    // Serialize each shard and write them to shards folder
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val shardDirectoryPath = new Path(shardDirectory)

    shards.foreach(shard => {
      // Create file named shard-{ID OF SHARD}
      val shardPath = new Path(shardDirectoryPath,s"shard-${shard.id}")
      val fsOut = fs.create(shardPath)
      shard.write(fsOut)
      IOUtils.closeStream(fsOut)
    })
    fs.close()
    shardID
  }
}