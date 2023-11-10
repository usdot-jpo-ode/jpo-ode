# jpo-ode

## GitHub Repository Link
https://github.com/usdot-jpo-ode/jpo-ode

## Purpose
The purpose of the Operational Data Environment is to ingest, process and disseminate connected vehicle data in the form of messages defined in J2735. The primary program utilizes a number of internal modules and external submodules.

## How to pull the latest image
The latest image can be pulled using the following command:
> docker pull usdotjpoode/jpo-ode:latest

## Required environment variables
The image expects the following environment variables to be set:
- DOCKER_HOST_IP
- DOCKER_SHARED_VOLUME

## Direct Dependencies
The ODE will fail to start up if the following containers are not already present:
- Kafka
- Zookeeper (relied on by Kafka)

## Indirect Dependencies
Some functionality will be unreachable without the participation of the following programs (except by directly pushing to kafka topics):
- asn1_codec
- jpo-cvdp
- jpo-security-svcs
- jpo-sdw-depositor
- jpo-s3-deposit

## Example docker-compose.yml with direct dependencies:
```
version: '3'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    ports:
      - "2181:2181"
    logging:
      options:
        max-size: "10m"
        max-file: "5"

  kafka:
    image: wurstmeister/kafka
    ports:
      - "9092:9092"
    environment:
      DOCKER_HOST_IP: ${DOCKER_HOST_IP}
      ZK: ${DOCKER_HOST_IP}:2181
      KAFKA_ADVERTISED_HOST_NAME: ${DOCKER_HOST_IP}
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"
      KAFKA_CREATE_TOPICS: "topic.OdeBsmPojo:1:1,topic.OdeSpatTxPojo:1:1,topic.OdeSpatPojo:1:1,topic.OdeSpatJson:1:1,topic.FilteredOdeSpatJson:1:1,topic.OdeSpatRxJson:1:1,topic.OdeSpatRxPojo:1:1,topic.OdeBsmJson:1:1,topic.FilteredOdeBsmJson:1:1,topic.OdeTimJson:1:1,topic.OdeTimBroadcastJson:1:1,topic.J2735TimBroadcastJson:1:1,topic.OdeDriverAlertJson:1:1,topic.Asn1DecoderInput:1:1,topic.Asn1DecoderOutput:1:1,topic.Asn1EncoderInput:1:1,topic.Asn1EncoderOutput:1:1,topic.SDWDepositorInput:1:1,topic.OdeTIMCertExpirationTimeJson:1:1,topic.OdeRawEncodedBSMJson:1:1,topic.OdeRawEncodedSPATJson:1:1,topic.OdeRawEncodedTIMJson:1:1,topic.OdeRawEncodedMAPJson:1:1,topic.OdeMapTxPojo:1:1,topic.OdeMapJson:1:1,topic.OdeRawEncodedSSMJson:1:1,topic.OdeSsmPojo:1:1,topic.OdeSsmJson:1:1,topic.OdeRawEncodedSRMJson:1:1,topic.OdeSrmTxPojo:1:1,topic.OdeSrmJson:1:1"
      KAFKA_DELETE_TOPIC_ENABLED: "true"
      KAFKA_CLEANUP_POLICY: "delete" # delete old logs
      KAFKA_LOG_RETENTION_HOURS: 2
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 3000
      KAFKA_RETENTION_MS: 7200000    # delete old logs after 2 hours
      KAFKA_SEGMENT_MS:   7200000    # roll segment logs every 2 hours.
                                     # This configuration controls the period of time after
                                     # which Kafka will force the log to roll even if the segment
                                     # file isn't full to ensure that retention can delete or compact old data.
    depends_on:
      - zookeeper
    volumes:
      - ${DOCKER_SHARED_VOLUME_WINDOWS}/var/run/docker.sock:/var/run/docker.sock
    logging:
      options:
        max-size: "10m"
        max-file: "5"

  ode:
    image: usdotjpoode/jpo-ode:release_q3
    ports:
      - "8080:8080"
      - "9090:9090"
      - "46753:46753/udp"
      - "46800:46800/udp"
      - "47900:47900/udp"
      - "44900:44900/udp"
      - "44910:44910/udp"
      - "44920:44920/udp"
      - "44930:44930/udp"
      - "5555:5555/udp"
      - "6666:6666/udp"
    environment:
      DOCKER_HOST_IP: ${DOCKER_HOST_IP}
      ZK: ${DOCKER_HOST_IP}:2181
      ODE_SECURITY_SVCS_SIGNATURE_URI: ${ODE_SECURITY_SVCS_SIGNATURE_URI}
      ODE_RSU_USERNAME: ${ODE_RSU_USERNAME}
      ODE_RSU_PASSWORD: ${ODE_RSU_PASSWORD}
    depends_on:
      - kafka
    volumes:
      - ${DOCKER_SHARED_VOLUME}:/jpo-ode
      - ${DOCKER_SHARED_VOLUME}/uploads:/home/uploads
    logging:
      options:
        max-size: "10m"
        max-file: "5"
```

## Expected startup output
The last console message outputted should be the following:
> 2023-11-09 15:14:05 [main] INFO  OdeSvcsApplication - Started OdeSvcsApplication in 21.83 seconds (JVM running for 22.766)