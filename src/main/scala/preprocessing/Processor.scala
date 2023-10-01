package preprocessing

import NetGraphAlgebraDefs.*
import NetGraphAlgebraDefs.NetModelAlgebra.{outputDirectory, perturbationCoeff, propValueRange}
import Utilz.NGSConstants
import com.typesafe.config.ConfigFactory

import java.io.File
import scala.collection.mutable
import scala.collection.mutable.Set
import scala.jdk.CollectionConverters.*
import scala.math.max
object Processor{
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
  def splitGraphs(graphName: String, perturbedGraphName: String): ( List[mutable.Set[NodeObject]],  List[mutable.Set[NodeObject]]) = {
    val config = ConfigFactory.load()
    val subGraphRatio = config.getDouble("Preprocessor.subGraphRatio")
    val minSubGraphSize = config.getInt("Preprocessor.minSubGraphSize")
    val graph = NetGraph.load(fileName = graphName).get
    val perturbedGraph = NetGraph.load(fileName = perturbedGraphName).get
    val subGraphs = graph.sm.nodes().asScala.grouped(max(minSubGraphSize, (graph.totalNodes * subGraphRatio).toInt)).toList
    val perturbedSubGraphs = perturbedGraph.sm.nodes().asScala.grouped(max(minSubGraphSize, (perturbedGraph.totalNodes * subGraphRatio).toInt)).toList
    // serialize sub graphs and deserialize them in driver
    (subGraphs, perturbedSubGraphs)
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