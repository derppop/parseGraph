package core

import org.apache.hadoop.io.{NullWritable, Text, DoubleWritable}
import org.apache.hadoop.mapreduce.Reducer
import util.Config.Reducer.simScoreThreshold

import java.lang

//REPLACE IN AND OUT KEY VALUE PAIRS
class GraphReducer() extends Reducer[Text, DoubleWritable, Text, DoubleWritable]{
  // reducer class
  // inherit reducer from hadoop
  // input would be
  // output would be key: node pairs value: similarity score

  override def reduce(key: Text, values: lang.Iterable[DoubleWritable], context: Reducer[Text, DoubleWritable, Text, DoubleWritable]#Context): Unit = {
    var score = 0.0

    val valIterator = values.iterator()
    while (valIterator.hasNext) {
      val currentScore = valIterator.next().get()
      if (currentScore > score) score = currentScore
    }

    if (score >= simScoreThreshold) context.write(key, new DoubleWritable(score))
  }
}
