package util

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import Config.Job.shardDirectory
import java.net.URI
import java.io.File

object HdfsUploader {
  def uploadFromLocal(localDir: String, hdfsDir: String): Unit = {
    val conf = new Configuration()
    val localPath = new Path(localDir)
    val hdfsPath = new Path(hdfsDir)
    val fs = FileSystem.get(URI.create(hdfsDir), conf)

    if (!fs.exists(hdfsPath)) {
      fs.mkdirs(hdfsPath)
    }

    val localFiles = new File(localDir).listFiles

    for (file <- localFiles) {
      val localFilePath = new Path(file.getAbsolutePath)
      val hdfsFilePath = new Path(hdfsPath, file.getName)
      fs.copyFromLocalFile(localFilePath, hdfsFilePath)
    }

    fs.close()
  }
}
