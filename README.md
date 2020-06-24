## This repository is using kinesis as source and kafka as a sink.

#### Values for local testing
com.amazonaws.sdk.disableCbor
org.apache.flink.kinesis.shaded.com.amazonaws.sdk.disableCbor
The following values are set to true just for local testing. 
For the real application to work, set them to false.
AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY are set to dummy.

#### Test the application

1. git clone https://github.com/localstack/localstack.git

2. cd localstack

3. Change docker compose file to:

version: '2.1'
services:
  localstack:
    container_name: "${LOCALSTACK_DOCKER_NAME-localstack_main}"
    image: localstack/localstack
    ports:
      - "4568:4568"
    environment:
      - SERVICES=kinesis
      - DEBUG=1
      - DOCKER_HOST=unix:///var/run/docker.sock

4. Run aws configure
Set aws key and secret key as dummy.
Region as us-east-1
format as json.

5. List all the kineis streams.

aws kinesis list-streams --endpoint-url=http://localhost:4568

6. Let's create our stream.

aws --endpoint-url=http://localhost:4568  kinesis create-stream --shard-count 1 --stream-name kinesisStream

Check the stream is created using the command : aws kinesis list-streams --endpoint-url=http://localhost:4568

7. Add the record to kinesis stream.

aws --endpoint-url=http://localhost:4568  kinesis put-record --stream-name kinesisStream --partition-key 123 --data "Abcd1"

8. Now, we will start the kafka as a sink. 

sudo docker run -d --rm --name=zookeeper \
-e ZOOKEEPER_CLIENT_PORT=2181 \
-p 2181:2181 \
confluentinc/cp-zookeeper:5.3.1
zookeeper_host=$(sudo docker inspect zookeeper | jq -r .[].NetworkSettings.Networks.bridge.IPAddress) && echo $zookeeper_host:2181


sudo docker run -d --rm --name=kafka \
  -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=${zookeeper_host}:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://172.17.0.3:9092 \
  -e KAFKA_BROKER_ID=0 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e CONFLUENT_SUPPORT_CUSTOMER_ID=c0 \
  confluentinc/cp-kafka:5.3.1

9. The follwing command gives me 172.17.0.3:9092 which is my bootstrap.servers.

kafka_host=$(sudo docker inspect kafka | jq -r .[].NetworkSettings.Networks.bridge.IPAddress) && echo $kafka_host:9092

10. Run docker ps and exec into the kafka container using command:

docker exec -it <container-id> bash

11. Run the following command to see the data from kinesis.

kafka-console-consumer --bootstrap-server 172.17.0.3:9092 --topic kafkaTopic --from-beginning