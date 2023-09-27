import NetGraphAlgebraDefs.{GraphPerturbationAlgebra, NetGraph, NetModel, NetModelAlgebra}
import NetGraphAlgebraDefs.NetModelAlgebra.{outputDirectory, propValueRange}
import NetGraphAlgebraDefs.GraphStore
import Utilz.NGSConstants

import java.io.File
object Main{
  def main(args: Array[String]): Unit = {
    val graph: NetGraph = NetModelAlgebra().get
    val graphName = NGSConstants.OUTPUTFILENAME
    graph.persist(outputDirectory, graphName) // serialize graph
    graph.toDotVizFormat("Before graph: "+graphName, outputDirectory, graphName)
    val perturbation: GraphPerturbationAlgebra#GraphPerturbationTuple = GraphPerturbationAlgebra(graph.copy)
    perturbation._1.persist(outputDirectory, graphName.concat(".perturbed"))
    perturbation._1.toDotVizFormat("After graph: "+graphName+".perturbed", outputDirectory, graphName+".perturbed")
    GraphPerturbationAlgebra.persist(perturbation._2, outputDirectory.concat(graphName.concat(".yaml")))

    val visualizeGraphs1 = new ProcessBuilder(
      "sfdp",
      "-x",
      "-Goverlap=scale",
      "-Tpng",
      outputDirectory + graphName + ".dot"
    )
    visualizeGraphs1.redirectOutput(ProcessBuilder.Redirect.to(new File(outputDirectory + graphName + ".png")))
    val process1 = visualizeGraphs1.start()

    val exitCode1 = process1.waitFor()

    if (exitCode1 == 0) {
      println("Graph visualization complete.")
    } else {
      System.err.println("Graph visualization failed with exit code: " + exitCode1)
    }

    val visualizeGraphs2 = new ProcessBuilder(
      "sfdp",
      "-x",
      "-Goverlap=scale",
      "-Tpng",
      outputDirectory + graphName + ".perturbed" + ".dot"
    )
    visualizeGraphs2.redirectOutput(ProcessBuilder.Redirect.to(new File(outputDirectory + graphName + ".perturbed" + ".png")))
    val process2 = visualizeGraphs2.start()

    val exitCode2 = process2.waitFor()

    if (exitCode2 == 0) {
      println("Graph visualization complete.")
    } else {
      System.err.println("Graph visualization failed with exit code: " + exitCode2)
    }
  }
}