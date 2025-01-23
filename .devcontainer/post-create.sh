cd ~/kafka/
KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"

bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/kraft/server.properties

# start kafka
bin/kafka-server-start.sh -daemon config/kraft/server.properties
# wait 2 seconds for the server to start and be able to add partitions
sleep 2s
# add topics

# BSM
bin/kafka-topics.sh --create --topic "topic.OdeBsmPojo" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeBsmJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.FilteredOdeBsmJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeRawEncodedBSMJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# TIM
bin/kafka-topics.sh --create --topic "topic.OdeTimJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeTimJsonTMCFiltered" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeTimBroadcastJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.J2735TimBroadcastJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeTIMCertExpirationTimeJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeRawEncodedTIMJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# PSM
bin/kafka-topics.sh --create --topic "topic.OdePsmPojo" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdePsmJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeRawEncodedPSMJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# SSM
bin/kafka-topics.sh --create --topic "topic.OdeSsmPojo" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeSsmJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeRawEncodedSSMJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# SRM
bin/kafka-topics.sh --create --topic "topic.OdeSrmPojo" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeSrmJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeRawEncodedSRMJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# MAP
bin/kafka-topics.sh --create --topic "topic.OdeRawEncodedMAPJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeMapTxPojo" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeMapJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# SPaT
bin/kafka-topics.sh --create --topic "topic.OdeSpatTxPojo" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeSpatPojo" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeSpatJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.FilteredOdeSpatJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeSpatRxJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeSpatRxPojo" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# ASN1
bin/kafka-topics.sh --create --topic "topic.Asn1DecoderInput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1DecoderOutput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1EncoderInput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.Asn1EncoderOutput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# ETC
bin/kafka-topics.sh --create --topic "topic.SDWDepositorInput" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeRawEncodedMessageJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
bin/kafka-topics.sh --create --topic "topic.OdeDriverAlertJson" --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
