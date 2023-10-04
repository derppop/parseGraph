package core

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job
import io.ShardInputFormat
import preprocessing.Processor.{createShards, generateGraphs}
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.hadoop.io.{DoubleWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import util.Config.Job.{shardDirectory, jobOutputDirectory}
import org.apache.hadoop.fs.Path
import util.HdfsUploader.uploadFromLocal

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
  job.setOutputFormatClass(classOf[TextOutputFormat[Text, DoubleWritable]])

  job.setMapperClass(classOf[GraphMapper])
  job.setReducerClass(classOf[GraphReducer])

  job.setOutputKeyClass(classOf[Text])
  job.setOutputValueClass(classOf[DoubleWritable])

  // calls preprocessor to generate graph
  val (graphName: String, perturbedGraphName: String) = generateGraphs()
  val numShards = createShards(graphName, perturbedGraphName)
  job.getConfiguration.set("num.shards", numShards.toString)
  
  uploadFromLocal(shardDirectory, "/shards/")

  FileInputFormat.addInputPath(job, new Path(shardDirectory+"/"))
  FileOutputFormat.setOutputPath(job, new Path(jobOutputDirectory+"/"))

  // start job
  val success = job.waitForCompletion(true)
  System.exit(if (success) 0 else 1)
}
}
