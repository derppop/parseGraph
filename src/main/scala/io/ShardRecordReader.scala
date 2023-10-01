package io

class ShardRecordReader {
  // reads data from inputSplit, converts it to key, value pairs for mappers
  // reads serialized data from preprocessor and deserialize it back into shard instance
  // passes pair of subgraph inside shard to mapper as value, key as shard id
  // each mapper gets a recordReader to read its assigned inputSplit
}
