package core

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job
import io.ShardInputFormat
import preprocessing.Processor.{createShards, generateGraphs}
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.hadoop.io.{DoubleWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import util.Config.Job.{shardDirectory, jobOutputDirectory, bucketDirectory}
import NetGraphAlgebraDefs.NetModelAlgebra.outputDirectory
import org.apache.hadoop.fs.{FileSystem, Path}
import util.HdfsUploader.uploadFromLocal

object Driver {
// configures job
// assigns input format
// starts job
def runJob(args: Array[String]): Unit = {
  // configure job
  val conf = new Configuration()
//  conf.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")

  val job = Job.getInstance(conf, "Graph comparison")
  val fileSystem = FileSystem.get(new java.net.URI(bucketDirectory), conf)
  val inputPath = new Path(shardDirectory)
  val outputPath = new Path(jobOutputDirectory)
  val graphPath = new Path(outputDirectory)
  job.setJarByClass(this.getClass)


  // clear graphs directory of old graphs
  if (fileSystem.exists(graphPath)) {
//    fileSystem.listStatus(graphPath).foreach { graph =>
//      fileSystem.delete(graph.getPath, false)
//    }
  } else {
    fileSystem.mkdirs(graphPath)
  }

  // Clear input directory of old shards
  if (fileSystem.exists(inputPath)) {
    fileSystem.listStatus(inputPath).foreach { file =>
      fileSystem.delete(file.getPath, false)
    }
  } else {
    fileSystem.mkdirs(inputPath)
  }

  // clear output directory of old results
  if (fileSystem.exists(outputPath)) {
    fileSystem.delete(outputPath, true)
  }

  job.setInputFormatClass(classOf[ShardInputFormat])
  job.setOutputFormatClass(classOf[TextOutputFormat[Text, DoubleWritable]])

  // Set mapper and reducer
  job.setMapperClass(classOf[GraphMapper])
  job.setReducerClass(classOf[GraphReducer])

  // Set output key value classes
  job.setOutputKeyClass(classOf[Text])
  job.setOutputValueClass(classOf[DoubleWritable])

  // calls preprocessor to generate graphs and shards
//  val (graphName: String, perturbedGraphName: String) = generateGraphs()
  val numShards = createShards()
  job.getConfiguration.set("num.shards", numShards.toString)
  
//  uploadFromLocal(shardDirectory, "hdfs://0.0.0.0:19000/user/yortb/shards/")

  FileInputFormat.addInputPath(job, new Path(shardDirectory+"/"))
  FileOutputFormat.setOutputPath(job, new Path(jobOutputDirectory+"/"))

  // start job
  val success = job.waitForCompletion(true)
  System.exit(if (success) 0 else 1)
}
}
