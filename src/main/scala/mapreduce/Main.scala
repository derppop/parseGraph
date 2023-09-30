package mapreduce
import mapreduce.Preprocessor
import mapreduce.Preprocessor.{generateGraphs, splitGraphs}

object Main {
  def main(args: Array[String]): Unit = {
    val (graphName: String, perturbedGraphName: String) = generateGraphs()
    val (subGraphs, perturbedSubGraphs) = splitGraphs(graphName, perturbedGraphName)
  }
}
