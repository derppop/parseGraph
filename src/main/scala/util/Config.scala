package util
import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()

  object Preprocessor {
    private val preprocessorConfig = config.getConfig("preprocessor")
    val subGraphSize: Double = preprocessorConfig.getDouble("subGraphSize")
  }

}
