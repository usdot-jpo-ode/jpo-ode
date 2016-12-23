FROM ubuntu:latest
MAINTAINER 583114@bah.com

#Set environment variables
ENV KAFKA_VERSION 0.10.1.0
ENV SCALA_VERSION 2.11
ENV KAFKA_HOME /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"

#Expose ports for use with the container
EXPOSE 9092

#Install necessary software
RUN apt-get update && \
    apt-get install -y zookeeper wget supervisor dnsutils && \
    rm -rf /var/lib/apt/lists/* && \
    wget -q http://apache.mirrors.spacedump.net/kafka/"$KAFKA_VERSION"/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -O /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz && \
    tar xfz /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -C /opt && \
    rm /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz
RUN apt-get update && \
	apt-get install -y software-properties-common && \
	apt-add-repository universe && \
	apt-get install -y maven
RUN apt-get install -y git
RUN apt-get install -y default-jdk
RUN apt-get update && \
	apt-get install -y vim
RUN apt-get update && \
	apt-get install -y nano && \
	apt-get clean

#Add files
ADD jpo-ode-svcs/target/jpo-ode-svcs-0.0.1-SNAPSHOT.jar /home
ADD docker/start.sh /start.sh
ADD docker/createTopic.sh /createTopic.sh
RUN echo "hostname=kafka" >> /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"/config/server.properties
RUN echo "advertised.host.name=<Docker_Container_IP>" >> /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"/config/server.properties
RUN echo "advertised.port=9092" >> /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"/config/server.properties

#Change permissions and run scripts
RUN chmod +x start.sh
RUN chmod +x createTopic.sh
CMD bash -C './start.sh' ; './createTopic.sh' ; '/bin/bash'
