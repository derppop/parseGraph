package util
import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()

  object Preprocessor {
    private val preprocessorConfig = config.getConfig("Preprocessor")
    
    val shardDirectory: String = preprocessorConfig.getString("shardDirectory")
    val subGraphRatio: Double = preprocessorConfig.getDouble("subGraphRatio")
    val minSubGraphSize: Int = preprocessorConfig.getInt("minSubGraphSize")
  }
}
