package app

import preprocessing.Processor
import Processor.{generateGraphs, splitGraphs}

object Main {
  def main(args: Array[String]): Unit = {
    val (graphName: String, perturbedGraphName: String) = generateGraphs()
    val (subGraphs, perturbedSubGraphs) = splitGraphs(graphName, perturbedGraphName)
    // call preprocessor
    // call driver
  }
}
