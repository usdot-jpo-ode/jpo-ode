#!/bin/bash

$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &

$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties &

echo 'Kafka and zookeeper started'

$KAFKA_HOME/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic BSM

echo 'Topic Created'