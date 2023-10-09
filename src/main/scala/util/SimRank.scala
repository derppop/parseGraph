package util

import NetGraphAlgebraDefs.NodeObject
import org.apache.hadoop.io.DoubleWritable
import scala.math.{min, abs}
import Config.SimRank.{childrenSimWeight, storedValSimWeight, depthSimWeight, propertySimWeight, branchFactorSimWeight}

object SimRank {
//  currentDepth: Int
//  propValueRange: Int
//  maxDepth: Int
//  maxBranchingFactor: Int
//  maxProperties: Int
//  storedValue: Double

  private def propertyScore(originalNode: NodeObject, perturbedNode: NodeObject): Double = {
    // if properties are the same, 1.0, else 0.0
    // big weight
    val originalProps = originalNode.properties
    val perturbedProps = perturbedNode.properties
    var score = 0.0
    val increment = 1.0 / min(originalNode.props, perturbedNode.props)
    (0 until min(originalNode.props, perturbedNode.props)).foreach { prop =>
      val diff = compareValues(originalProps(prop), perturbedProps(prop))
      if (diff > 0.8) score += diff*increment else score -= increment
    }

    if (score <= 0.0) 0.0 else score
  }

  private def compareValues(originalVal: Int, perturbedVal: Int): Double = {
    val difference = Math.pow(originalVal - perturbedVal, 2)
    Math.pow(Math.E, -0.05*difference)
  }

  private def getValSim(originalVal: Double, perturbedVal: Double): Double = {
    1.0-abs(originalVal-perturbedVal)
  }

  def getSimilarityScore(firstNode: NodeObject, secondNode: NodeObject): DoubleWritable = {
    // linear combination of weighted node properties
    val propSim = propertyScore(firstNode, secondNode)
    val childrenSim = compareValues(firstNode.children, secondNode.children)
    val depthSim = compareValues(firstNode.currentDepth, secondNode.currentDepth)
    val maxBranchFactorSim = compareValues(firstNode.maxBranchingFactor, secondNode.maxBranchingFactor)
    val valSim = getValSim(firstNode.storedValue, secondNode.storedValue)
    // replace weights with config values
    new DoubleWritable(0.2*propSim + 0.3*childrenSim + 0.15*depthSim + 0.1*maxBranchFactorSim + 0.25*valSim)
  }

}
