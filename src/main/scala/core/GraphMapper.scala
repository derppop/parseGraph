package core
import models.Shard
import org.apache.hadoop.io.{NullWritable, DoubleWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import util.SimRank.getSimilarityScore

// Input: Null, Shard
// Output: Node-pair, Sim-score
class GraphMapper() extends Mapper[NullWritable, Shard, Text, DoubleWritable]{

  override def map(key: NullWritable, value: Shard, context: Mapper[NullWritable, Shard, Text, DoubleWritable]#Context): Unit = {
    value.subGraph.foreach(node => {
      value.perturbedSubGraph.foreach(perturbedNode => {
        context.write(new Text(node.id.toString + "-" + perturbedNode.id.toString), getSimilarityScore(node, perturbedNode))
      })
    })
  }

}
