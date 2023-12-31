package io

import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.{InputSplit, JobContext, RecordReader, TaskAttemptContext}
import models.Shard
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.lib.input.FileSplit
import util.Config.Job.shardDirectory


class ShardInputFormat extends FileInputFormat[NullWritable, Shard] {
  // Creates FileSplit(shard) instances
  override def getSplits(job: JobContext): java.util.List[InputSplit] = {
    // Reads file and creates FileSplit for each shard
    val splits = new java.util.ArrayList[InputSplit]()
    val numShards = job.getConfiguration.get("num.shards").toInt
    // Build splits (list of shard paths) to be used by record reader
    (1 to numShards).foreach(id => {
      val path = new Path(shardDirectory + "/" + "shard-"+id)
      val fileSystem = path.getFileSystem(job.getConfiguration)
      val fileStatus = fileSystem.getFileStatus(path)
      val length = fileStatus.getLen
      val start = 0L
      val hosts = Array.empty[String]
      val split = new FileSplit(path, start, length, hosts)
      splits.add(split)
    })
    splits
  }

  override def createRecordReader(split: InputSplit, context: TaskAttemptContext): RecordReader[NullWritable, Shard] = {
    new ShardRecordReader()
  }
}
