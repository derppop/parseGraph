package core

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job
import io.ShardInputFormat
import preprocessing.Processor.{createShards, generateGraphs}
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.hadoop.io.{DoubleWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import util.Config.Job.{bucketDirectory, jobOutputDirectory, shardDirectory}
import NetGraphAlgebraDefs.NetModelAlgebra.outputDirectory
import org.apache.hadoop.fs.{FileSystem, Path}
import org.slf4j.LoggerFactory
import util.HdfsUploader.uploadFromLocal

object Driver {
private val logger = LoggerFactory.getLogger(Driver.getClass)

def runJob(args: Array[String]): Unit = {
  // Configure job
  val conf = new Configuration()
  val job = Job.getInstance(conf, "Graph comparison")
  val fileSystem = FileSystem.get(new java.net.URI(bucketDirectory), conf)
  val inputPath = new Path(shardDirectory)
  val outputPath = new Path(jobOutputDirectory)
  val graphPath = new Path(outputDirectory)
  job.setJarByClass(this.getClass)


  // clear graphs directory of old graphs
  if (fileSystem.exists(graphPath)) {
    logger.info(s"Clearing graphs path at $outputDirectory")
    fileSystem.listStatus(graphPath).foreach { graph =>
      fileSystem.delete(graph.getPath, false)
    }
  } else {
    logger.info(s"Graphs path at $outputDirectory does not exist, creating it")
    fileSystem.mkdirs(graphPath)
  }

  // Clear input directory of old shards
  if (fileSystem.exists(inputPath)) {
    logger.info(s"Clearing shards path at $shardDirectory")
    fileSystem.listStatus(inputPath).foreach { file =>
      fileSystem.delete(file.getPath, false)
    }
  } else {
    logger.info(s"Shards path at $shardDirectory does not exist, creating it")
    fileSystem.mkdirs(inputPath)
  }

  // Clear output directory of old results
  if (fileSystem.exists(outputPath)) {
    logger.info(s"Result path at $jobOutputDirectory already exists, deleting it along with its contents")
    fileSystem.delete(outputPath, true)
  }

  // Set input and output classes
  job.setInputFormatClass(classOf[ShardInputFormat])
  job.setOutputFormatClass(classOf[TextOutputFormat[Text, DoubleWritable]])

  // Set mapper and reducer
  job.setMapperClass(classOf[GraphMapper])
  job.setReducerClass(classOf[GraphReducer])

  // Set output key value classes
  job.setOutputKeyClass(classOf[Text])
  job.setOutputValueClass(classOf[DoubleWritable])

  // calls preprocessor to generate graphs and shards
  val (graphName: String, perturbedGraphName: String) = generateGraphs()
  val numShards = createShards(graphName, perturbedGraphName)
  job.getConfiguration.set("num.shards", numShards.toString)

  // send shards to hdfs
//  uploadFromLocal(shardDirectory, "hdfs://0.0.0.0:19000/user/yortb/shards/")

  // Set shard and result directories
  FileInputFormat.addInputPath(job, new Path(shardDirectory))
  logger.info(s"Set input path to $shardDirectory")
  FileOutputFormat.setOutputPath(job, new Path(jobOutputDirectory))
  logger.info(s"Set output path to $jobOutputDirectory")

  // start job
  val success = job.waitForCompletion(true)
  System.exit(if (success) 0 else 1)
}
}
