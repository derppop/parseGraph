import NetGraphAlgebraDefs.{GraphPerturbationAlgebra, NetGraph, NetModel, NetModelAlgebra}
import NetGraphAlgebraDefs.NetModelAlgebra.outputDirectory
import Utilz.NGSConstants
object Main{
  def main(args: Array[String]): Unit = {
    val graph: NetGraph = NetModelAlgebra().get
    val graphName = NGSConstants.OUTPUTFILENAME
    graph.persist(outputDirectory, graphName) // serialize graph
    val perturbation: GraphPerturbationAlgebra#GraphPerturbationTuple = GraphPerturbationAlgebra(graph.copy)
    perturbation._1.persist(outputDirectory, graphName.concat(".perturbed"))
    GraphPerturbationAlgebra.persist(perturbation._2, outputDirectory.concat(graphName.concat("yaml")))

  }
}