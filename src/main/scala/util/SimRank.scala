package util

import NetGraphAlgebraDefs.NodeObject
import org.apache.hadoop.io.DoubleWritable
import scala.math.{min, abs}
import Config.SimRank.{childrenSimWeight, storedValSimWeight, depthSimWeight, propertySimWeight, branchFactorSimWeight}

object SimRank {

  private def propertyScore(originalNode: NodeObject, perturbedNode: NodeObject): Double = {
    val originalProps = originalNode.properties
    val perturbedProps = perturbedNode.properties
    var score = 0.0
    val increment = 1.0 / min(originalNode.props, perturbedNode.props)
    // Compare each value in list of properties, the closer they are the higher the property score
    (0 until min(originalNode.props, perturbedNode.props)).foreach { prop =>
      val diff = compareValues(originalProps(prop), perturbedProps(prop))
      if (diff > 0.8) score += diff*increment else score -= increment
    }

    if (score <= 0.0) 0.0 else score
  }

  private def compareValues(originalVal: Int, perturbedVal: Int): Double = {
    // Returns a value between 0 and 1 representing similarity between two integers
    val difference = Math.pow(originalVal - perturbedVal, 2)
    Math.pow(Math.E, -0.05*difference)
  }

  private def getValSim(originalVal: Double, perturbedVal: Double): Double = {
    // Returns a value between 0 and 1 representing similarity between two doubles
    1.0-abs(originalVal-perturbedVal)
  }

  def getSimilarityScore(firstNode: NodeObject, secondNode: NodeObject): DoubleWritable = {
    // Linear combination of weighted node properties
    val propSim = propertyScore(firstNode, secondNode)
    val childrenSim = compareValues(firstNode.children, secondNode.children)
    val depthSim = compareValues(firstNode.currentDepth, secondNode.currentDepth)
    val maxBranchFactorSim = compareValues(firstNode.maxBranchingFactor, secondNode.maxBranchingFactor)
    val valSim = getValSim(firstNode.storedValue, secondNode.storedValue)
    new DoubleWritable(propertySimWeight*propSim + childrenSimWeight*childrenSim + depthSimWeight*depthSim + branchFactorSimWeight*maxBranchFactorSim + storedValSimWeight*valSim)
  }

}
