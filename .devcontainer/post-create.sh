cd ~/kafka/
# start zookeeper
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
# start kafka
bin/kafka-server-start.sh -daemon config/server.properties
# wait 2 seconds for the server to start and be able to add partitions
sleep 2s
# add topics
bin/kafka-topics.sh --create --topic "topic.OdeBsmPojo" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeBsmJson" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.FilteredOdeBsmJson" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeTimJson" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeTimBroadcastJson" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.J2735TimBroadcastJson" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeDriverAlertJson" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1DecoderInput" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1DecoderOutput" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1EncoderInput" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1EncoderOutput" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.SDWDepositorInput" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeTIMCertExpirationTimeJson" --zookeeper localhost:2181 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeRawEncodedMessageJson" --zookeeper localhost:2181 --replication-factor 1 --partitions 1