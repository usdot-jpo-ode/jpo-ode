FROM anapsix/alpine-java

MAINTAINER 572682

RUN apk add --update unzip wget curl docker jq coreutils

ENV KAFKA_VERSION="0.10.1.0" SCALA_VERSION="2.11"
ADD download-kafka.sh /tmp/download-kafka.sh
RUN chmod a+x /tmp/download-kafka.sh && sync && /tmp/download-kafka.sh && tar xfz /tmp/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz -C /opt && rm /tmp/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz

VOLUME ["/kafka"]

ENV KAFKA_HOME /opt/kafka_${SCALA_VERSION}-${KAFKA_VERSION}
ADD start-kafka.sh /usr/bin/start-kafka.sh
ADD broker-list.sh /usr/bin/broker-list.sh
ADD create-topics.sh /usr/bin/create-topics.sh
ADD start-console-consumer.sh /usr/bin/start-console-consumer.sh
ADD start-console-producer.sh /usr/bin/start-console-producer.sh

# The scripts need to have executable permission
RUN chmod a+x /usr/bin/start-kafka.sh && \
    chmod a+x /usr/bin/broker-list.sh && \
    chmod a+x /usr/bin/create-topics.sh && \
    chmod a+x /usr/bin/start-console-consumer.sh && \
    chmod a+x /usr/bin/start-console-producer.sh

# Use "exec" form so that it runs as PID 1 (useful for graceful shutdown)
CMD ["start-kafka.sh"]
