FROM maven:3.5.4-jdk-8-alpine as builder
MAINTAINER 583114@bah.com

VOLUME /root/.m2

WORKDIR /home
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:8u171-jre-alpine

WORKDIR /home

COPY --from=builder /home/jpo-ode-svcs/src/main/resources/application.properties /home
COPY --from=builder /home/jpo-ode-svcs/src/main/resources/logback.xml /home
COPY --from=builder /home/jpo-ode-svcs/target/jpo-ode-svcs.jar /home

ENTRYPOINT ["java", \
	"-Djava.rmi.server.hostname=$DOCKER_HOST_IP", \
	"-Dcom.sun.management.jmxremote.port=9090", \
	"-Dcom.sun.management.jmxremote.rmi.port=9090", \
	"-Dcom.sun.management.jmxremote", \
	"-Dcom.sun.management.jmxremote.local.only=true", \
	"-Dcom.sun.management.jmxremote.authenticate=false", \
	"-Dcom.sun.management.jmxremote.ssl=false", \
	"-Dlogback.configurationFile=/home/logback.xml", \
	"-jar", \
	"/home/jpo-ode-svcs.jar"]
