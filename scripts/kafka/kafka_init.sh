sleep 2s
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --list
echo 'Creating kafka topics'

# Create topics
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeBsmPojo" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeSpatTxPojo" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeSpatPojo" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeSpatJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.FilteredOdeSpatJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeSpatRxJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeSpatRxPojo" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeBsmJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.FilteredOdeBsmJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeTimJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeTimBroadcastJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.J2735TimBroadcastJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeDriverAlertJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.Asn1DecoderInput" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.Asn1DecoderOutput" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.Asn1EncoderInput" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.Asn1EncoderOutput" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.SDWDepositorInput" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeTIMCertExpirationTimeJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeRawEncodedBSMJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeRawEncodedSPATJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeRawEncodedTIMJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeRawEncodedMAPJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeMapTxPojo" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeMapJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeRawEncodedSSMJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeSsmPojo" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeSsmJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeRawEncodedSRMJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeSrmTxPojo" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeSrmJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdeRawEncodedPSMJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdePsmTxPojo" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
/opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists  --topic "topic.OdePsmJson" --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1

echo 'Kafka created with the following topics:'
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --list
exit