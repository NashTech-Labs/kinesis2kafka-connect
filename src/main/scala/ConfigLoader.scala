import com.typesafe.config.{Config, ConfigFactory}

object ConfigLoader {
  val config: Config = ConfigFactory.load()
  val awsRegion: String = config.getString("kinesis.awsRegion")
  val bootStrapUrl: String = config.getString("kafka.bootstrapServerUrl")
  val kafkaTopic: String = config.getString("kafka.topicName")
  val kinesisStream: String = config.getString("kinesis.streamName")
  val kinesisEndPoint: String = config.getString("kinesis.endPoint")
  val parallelism: Int = config.getInt("app.parallelism")
}