package core

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job
import io.ShardInputFormat
import preprocessing.Processor.{generateGraphs, createShards}

object Driver {
// configures job
// assigns input format
// starts job
def runJob(args: Array[String]): Unit = {
  // configure job
  val conf = new Configuration()
  val job = Job.getInstance(conf, "Graph comparison")
  job.setJarByClass(this.getClass)
  job.setInputFormatClass(classOf[ShardInputFormat])
  job.setMapperClass(classOf[GraphMapper])
  job.setReducerClass(classOf[GraphReducer])

  // calls preprocessor to generate graph
  val (graphName: String, perturbedGraphName: String) = generateGraphs()
  val numShards = createShards(graphName, perturbedGraphName)
  job.getConfiguration.set("num.shards", numShards.toString)

  // start job
  val success = job.waitForCompletion(true)
  System.exit(if (success) 0 else 1)
}
}
