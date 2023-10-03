package core
import models.Shard
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.Mapper

//REPLACE OUT KEY-VALUE PAIR
class GraphMapper() extends Mapper[NullWritable, Shard, NullWritable, NullWritable]{
  // mapper class
  // inherit mapper from hadoop
  // input, utilize inputFormat Shard, key: shard-id value: subgraph pair
  // output, (node pairs) value: similarity score

}
