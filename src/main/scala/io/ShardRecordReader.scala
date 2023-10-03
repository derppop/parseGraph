package io
import org.apache.hadoop.mapreduce.{InputSplit, RecordReader, TaskAttemptContext}
import org.apache.hadoop.io.NullWritable
import models.Shard
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.lib.input.FileSplit
import java.io.{FileInputStream, IOException, ObjectInputStream}

class ShardRecordReader extends RecordReader[NullWritable, Shard]{
  // reads data from inputSplit, converts it to key, value pairs for mappers
  // reads serialized data from preprocessor and deserialize it back into shard instance
  // passes pair of subgraph inside shard to mapper as value, key as shard id
  // each mapper gets a recordReader to read its assigned inputSplit
  private var key: NullWritable = NullWritable.get()
  private var value: Shard = _
  private var objectInputStream: ObjectInputStream = _
  private var processed: Boolean = false

  override def initialize(split: InputSplit, context: TaskAttemptContext): Unit = {
    val fileSplit = split.asInstanceOf[FileSplit]
    val path: Path = fileSplit.getPath
    val fileSystem = path.getFileSystem(context.getConfiguration)
    val fileInputStream = fileSystem.open(path)
    objectInputStream = new ObjectInputStream(fileInputStream)
  }

  // reads shard from file for mapper to process
  override def nextKeyValue(): Boolean = {
    if (!processed) {
      try {
        value.readFields(objectInputStream)
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
    if (objectInputStream != null) {
      objectInputStream.close()
    }
  }
}
