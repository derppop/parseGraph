package io
import org.apache.hadoop.mapreduce.{InputSplit, RecordReader, TaskAttemptContext}
import org.apache.hadoop.io.NullWritable
import models.Shard
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.lib.input.FileSplit

import java.io.{DataInputStream, FileInputStream, IOException, ObjectInputStream}

class ShardRecordReader extends RecordReader[NullWritable, Shard]{
  // Reads data from inputSplit, converts it to key, value pairs for mappers
  // Passes pair of subgraph inside shard to mapper as value, key as shard id
  // Each mapper gets a recordReader to read its assigned inputSplit
  private var key: NullWritable = NullWritable.get()
  private var value: Shard = Shard()
  private var dataInputStream: DataInputStream = _
  private var processed: Boolean = false

  override def initialize(split: InputSplit, context: TaskAttemptContext): Unit = {
    val fileSplit = split.asInstanceOf[FileSplit]
    val path: Path = fileSplit.getPath
    val fileSystem = path.getFileSystem(context.getConfiguration)
    val fileInputStream = fileSystem.open(path)
    dataInputStream = new DataInputStream(fileInputStream)
  }

  // reads shard from file for mapper to process
  override def nextKeyValue(): Boolean = {
    // Reads serialized data from preprocessor and deserialize it back into shard instance
    if (!processed) {
      try {
        value.readFields(dataInputStream)
        processed = true
        return true
      } catch {
        case e: IOException =>
          System.err.println(e)
          return false
      }
    }
    false
  }

  override def getCurrentKey: NullWritable = {
    key
  }

  override def getCurrentValue: Shard = {
    value
  }

  override def getProgress: Float = {
    if (processed) 1.0f else 0.0f
  }

  override def close(): Unit = {
    if (dataInputStream != null) {
      dataInputStream.close()
    }
  }
}
