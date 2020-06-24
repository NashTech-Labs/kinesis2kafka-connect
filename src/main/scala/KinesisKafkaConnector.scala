
import java.util.Properties
import ConfigLoader._
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer
import org.apache.flink.streaming.connectors.kinesis.config.{AWSConfigConstants, ConsumerConfigConstants}

object KinesisKafkaConnector extends App {
  System.setProperty("com.amazonaws.sdk.disableCbor", "true")
  System.setProperty("org.apache.flink.kinesis.shaded.com.amazonaws.sdk.disableCbor", "true")

  val kinesisConsumerConfig = new Properties()
  kinesisConsumerConfig.put(AWSConfigConstants.AWS_REGION, awsRegion)
  kinesisConsumerConfig.put(ConsumerConfigConstants.STREAM_INITIAL_POSITION, ConsumerConfigConstants.InitialPosition.TRIM_HORIZON.name())
  kinesisConsumerConfig.put(AWSConfigConstants.AWS_ACCESS_KEY_ID, "dummy")
  kinesisConsumerConfig.put(AWSConfigConstants.AWS_SECRET_ACCESS_KEY, "dummy")
  kinesisConsumerConfig.put(AWSConfigConstants.AWS_ENDPOINT, kinesisEndPoint)
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)

  val kinesis = env.addSource(new FlinkKinesisConsumer(kinesisStream, new SimpleStringSchema, kinesisConsumerConfig))
  val kafkaProducerProperties = new Properties
  kafkaProducerProperties.setProperty("bootstrap.servers", bootStrapUrl)

  val producer = new FlinkKafkaProducer[String](kafkaTopic, new SimpleStringSchema(), kafkaProducerProperties)
  kinesis.addSink(producer)
  env.execute("kinesis consumer")
}
