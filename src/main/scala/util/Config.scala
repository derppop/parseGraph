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
    val baseDirectory: String = jobConfig.getString("baseDirectory")
    val graphDirectory: String = jobConfig.getString("graphDirectory")
  }

  object SimRank {
    private val simRankConfig = config.getConfig("SimRank")
    val propertySimWeight: Double = simRankConfig.getString("propertySimWeight").toDouble
    val childrenSimWeight: Double = simRankConfig.getString("childrenSimWeight").toDouble
    val depthSimWeight: Double = simRankConfig.getString("depthSimWeight").toDouble
    val branchFactorSimWeight: Double = simRankConfig.getString("branchFactorSimWeight").toDouble
    val storedValSimWeight: Double = simRankConfig.getString("storedValSimWeight").toDouble
  }
}
