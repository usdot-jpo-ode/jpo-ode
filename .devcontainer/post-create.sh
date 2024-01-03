cd ~/kafka/
# start zookeeper
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
# start kafka
bin/kafka-server-start.sh -daemon config/server.properties
# wait 2 seconds for the server to start and be able to add partitions
sleep 2s
# add topics
bin/kafka-topics.sh --create --topic "topic.OdeBsmPojo" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeBsmJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.FilteredOdeBsmJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeTimJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeTimBroadcastJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.J2735TimBroadcastJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeDriverAlertJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1DecoderInput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1DecoderOutput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1EncoderInput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1EncoderOutput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.SDWDepositorInput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeTIMCertExpirationTimeJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeRawEncodedMessageJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1