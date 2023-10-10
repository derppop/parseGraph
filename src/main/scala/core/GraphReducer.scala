package core

import org.apache.hadoop.io.{NullWritable, Text, DoubleWritable}
import org.apache.hadoop.mapreduce.Reducer
import util.Config.Reducer.simScoreThreshold

import java.lang

// Input: Node-pair, Sim-score
// Output: Node-pair, Sim-score (Above threshold)
class GraphReducer() extends Reducer[Text, DoubleWritable, Text, DoubleWritable]{

  override def reduce(key: Text, values: lang.Iterable[DoubleWritable], context: Reducer[Text, DoubleWritable, Text, DoubleWritable]#Context): Unit = {
    // Filter scores above threshold
    var score = 0.0
    val valIterator = values.iterator()
    while (valIterator.hasNext) {
      val currentScore = valIterator.next().get()
      if (currentScore > score) score = currentScore
    }

    if (score >= simScoreThreshold) context.write(key, new DoubleWritable(score))
  }
}
