package app

import preprocessing.Processor
import Processor.{generateGraphs, createShards}
import org.apache.hadoop.conf.Configuration
import core.Driver

object Main {
  def main(args: Array[String]): Unit = {
    Driver.runJob(args)
  }
}
