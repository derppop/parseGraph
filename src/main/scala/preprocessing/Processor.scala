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
import org.slf4j.LoggerFactory
import util.Config.Preprocessor.{minSubGraphSize, subGraphRatio}
import util.Config.Job.shardDirectory

object Processor{
  private val logger = LoggerFactory.getLogger(Processor.getClass)
  // generate graph
  // perturb graph
  // pair every subgraph in original with every subgraph in perturbed
  // place pairs into shard objects (also used in InputFormat) with shard id's
  // serialize each shard and write each to a file in /shards

  def generateGraphs(): (String, String) = {
    val graph: NetGraph = NetModelAlgebra().get
    val graphName = NGSConstants.OUTPUTFILENAME
    graph.persist(outputDirectory, graphName) // serialize graph
//    graph.toDotVizFormat("Before graph: " + graphName, outputDirectory, graphName)
    val (perturbedGraph, changes): GraphPerturbationAlgebra#GraphPerturbationTuple = GraphPerturbationAlgebra(graph.copy)
    perturbedGraph.persist(outputDirectory, graphName.concat(".perturbed"))
//    perturbedGraph.toDotVizFormat("After graph: " + graphName + ".perturbed", outputDirectory, graphName + ".perturbed")
    GraphPerturbationAlgebra.persist(changes, outputDirectory.concat(graphName.concat(".yaml")))
    (graphName, graphName+".perturbed")
  }
  def createShards(graphName: String, perturbedGraphName: String): Int = {
    val graph = NetGraph.load(fileName = graphName).get
    val perturbedGraph = NetGraph.load(fileName = perturbedGraphName).get
    val subGraphs = graph.sm.nodes().asScala.grouped(max(minSubGraphSize, (graph.totalNodes * subGraphRatio).toInt)).toList
    logger.info(s"Split original graph into ${subGraphs.size} sub-graphs")
    val perturbedSubGraphs = perturbedGraph.sm.nodes().asScala.grouped(max(minSubGraphSize, (perturbedGraph.totalNodes * subGraphRatio).toInt)).toList
    logger.info(s"Split perturbed graph into ${perturbedSubGraphs.size} sub-graphs")
    // serialize sub graphs and deserialize them in driver
    (subGraphs, perturbedSubGraphs)
    var shardID = 0
    val shards: List[Shard] = subGraphs.flatMap(subgraph => perturbedSubGraphs.map(perturbedSubGraph => {
      shardID += 1
      Shard(shardID, subgraph.toSet, perturbedSubGraph.toSet)
    }))
    logger.info(s"Created ${shards.size} shards")

    shards.foreach(shard => {
      // create file named shard-{NUM OF SHARD}
      val out = new FileOutputStream(shardDirectory + "/" + "shard-"+shard.id)
      val dataOut: DataOutput = new DataOutputStream(out)
      shard.write(dataOut)
      dataOut.asInstanceOf[DataOutputStream].close()
      out.close()
    })
    // DELETE, ONLY FOR TESTING SERIALIZATION
    // -------
//    val deserializedShards: List[Shard] = (1 to shardID).map(id => {
//      val in = new FileInputStream(shardDirectory + "/" + "shard-"+id)
//      val dataIn: DataInput = new DataInputStream(in)
//      val shard: Shard = Shard()
//      shard.readFields(dataIn)
//      shard
//    }).toList
    // ------
    shardID
  }
}


//val visualizeGraphs1 = new ProcessBuilder(
//  "sfdp",
//  "-x",
//  "-Goverlap=scale",
//  "-Tpng",
//  outputDirectory + graphName + ".dot"
//)
//visualizeGraphs1.redirectOutput(ProcessBuilder.Redirect.to(new File(outputDirectory + graphName + ".png")))
//val process1 = visualizeGraphs1.start()
//
//val exitCode1 = process1.waitFor()
//
//if (exitCode1 == 0) {
//  println("Graph visualization complete.")
//} else {
//  System.err.println("Graph visualization failed with exit code: " + exitCode1)
//}
//
//val visualizeGraphs2 = new ProcessBuilder(
//  "sfdp",
//  "-x",
//  "-Goverlap=scale",
//  "-Tpng",
//  outputDirectory + graphName + ".perturbed" + ".dot"
//)
//visualizeGraphs2.redirectOutput(ProcessBuilder.Redirect.to(new File(outputDirectory + graphName + ".perturbed" + ".png")))
//val process2 = visualizeGraphs2.start()
//
//val exitCode2 = process2.waitFor()
//
//if (exitCode2 == 0) {
//  println("Graph visualization complete.")
//} else {
//  System.err.println("Graph visualization failed with exit code: " + exitCode2)
//}