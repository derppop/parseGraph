package app

import preprocessing.Processor
import Processor.{generateGraphs, createShards}

object Main {
  def main(args: Array[String]): Unit = {
    val (graphName: String, perturbedGraphName: String) = generateGraphs()
    val numOfShards = createShards(graphName, perturbedGraphName)
    // call preprocessor
    // call driver
  }
}
