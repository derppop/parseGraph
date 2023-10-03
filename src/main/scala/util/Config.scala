package util
import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()

  object Preprocessor {
    private val preprocessorConfig = config.getConfig("Preprocessor")
    
    val subGraphRatio: Double = preprocessorConfig.getDouble("subGraphRatio")
    val minSubGraphSize: Int = preprocessorConfig.getInt("minSubGraphSize")
  }

  object Reducer {
    private val reducerConfig = config.getConfig("Reducer")
    val simScoreThreshold: Double = reducerConfig.getDouble("simScoreThreshold")
  }

  object Job {
    private val jobConfig = config.getConfig("Job")
    val shardDirectory: String = jobConfig.getString("shardDirectory")
    val jobOutputDirectory: String = jobConfig.getString("jobOutputDirectory")
  }
}
