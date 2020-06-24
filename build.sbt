name := "flink-demo"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.apache.flink" %% "flink-scala" % "1.10.0",
  "org.apache.flink" %% "flink-connector-kinesis" % "1.10.0",
  "org.apache.flink" %% "flink-connector-kafka" % "1.10.0",
  "org.apache.flink" %% "flink-streaming-scala" % "1.10.0"
)
