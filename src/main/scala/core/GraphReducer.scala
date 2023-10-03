package core

import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.Reducer

//REPLACE IN AND OUT KEY VALUE PAIRS
class GraphReducer() extends Reducer[NullWritable, NullWritable, NullWritable, NullWritable]{
  // reducer class
  // inherit reducer from hadoop
  // input would be
  // output would be key: node pairs value: similarity score
}
