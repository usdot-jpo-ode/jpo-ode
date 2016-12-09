FROM ubuntu:latest
MAINTAINER 583114@bah.com

ENV KAFKA_VERSION 0.10.1.0
ENV SCALA_VERSION 2.11
ENV KAFKA_HOME /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"

RUN apt-get update && \
    apt-get install -y zookeeper wget supervisor dnsutils && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean && \
    wget -q http://apache.mirrors.spacedump.net/kafka/"$KAFKA_VERSION"/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -O /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz && \
    tar xfz /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -C /opt && \
    rm /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz
RUN apt-get update && \
	apt-get install -y software-properties-common && \
	apt-add-repository universe && \
	apt-get install -y maven
RUN apt-get install -y git
RUN apt-get install -y default-jdk
ADD jpo-ode-svcs/target/jpo-ode-svcs-0.0.1-SNAPSHOT.jar /home
RUN apt-get update && \
	apt-get install -y vim && \
	apt-get clean
RUN apt-get update && \
	apt-get install -y nano && \
	apt-get clean
