package util

import NetGraphAlgebraDefs.NodeObject
import org.apache.hadoop.io.DoubleWritable

object SimRank {

  def getSimilarityScore(firstNode: NodeObject, secondNode: NodeObject): DoubleWritable = {
    // linear combination of weighted node properties
    new DoubleWritable(0.0)
  }

}
