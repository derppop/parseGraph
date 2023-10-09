package util
import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()

  object Preprocessor {
    private val preprocessorConfig = config.getConfig("Preprocessor")
    
    val subGraphRatio: Double = preprocessorConfig.getDouble("subGraphRatio")
    val minSubGraphSize: Int = preprocessorConfig.getInt("minSubGraphSize")
    val graphName: String = preprocessorConfig.getString("graphName")
    val perturbedGraphName: String = preprocessorConfig.getString("perturbedGraphName")
  }

  object Reducer {
    private val reducerConfig = config.getConfig("Reducer")
    val simScoreThreshold: Double = reducerConfig.getDouble("simScoreThreshold")
  }

  object Job {
    private val jobConfig = config.getConfig("Job")
    val shardDirectory: String = jobConfig.getString("shardDirectory")
    val jobOutputDirectory: String = jobConfig.getString("jobOutputDirectory")
    val bucketDirectory: String = jobConfig.getString("bucketDirectory")
  }

  object SimRank {
    private val simRankConfig = config.getConfig("SimRank")
    val propertySimWeight: Double = simRankConfig.getDouble("propertySimWeight")
    val childrenSimWeight: Double = simRankConfig.getDouble("childrenSimWeight")
    val depthSimWeight: Double = simRankConfig.getDouble("depthSimWeight")
    val branchFactorSimWeight: Double = simRankConfig.getDouble("branchFactorSimWeight")
    val storedValSimWeight: Double = simRankConfig.getDouble("storedValSimWeight")
  }
}
