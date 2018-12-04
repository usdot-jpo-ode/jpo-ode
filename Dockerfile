FROM maven:3.5.4-jdk-8-alpine as builder
MAINTAINER 583114@bah.com

# Set up the private repo first
WORKDIR /home/jpo-ode-private
	
COPY ./jpo-ode-private/pom.xml ./
COPY ./jpo-ode-private/j2735/pom.xml ./j2735/
COPY ./jpo-ode-private/j2735/src ./j2735/src
COPY ./jpo-ode-private/ieee1609dot2/pom.xml ./ieee1609dot2/
COPY ./jpo-ode-private/ieee1609dot2/src ./ieee1609dot2/src
COPY ./jpo-ode-private/lib/*.jar ./lib/

RUN mvn clean
RUN mvn install -DskipTests

# Now set up the public repo
WORKDIR /home

COPY ./pom.xml ./
COPY ./jpo-ode-core/pom.xml ./jpo-ode-core/
COPY ./jpo-ode-core/src ./jpo-ode-core/src/
COPY ./jpo-ode-svcs/pom.xml ./jpo-ode-svcs/
COPY ./jpo-ode-svcs/src ./jpo-ode-svcs/src
COPY ./jpo-ode-plugins/pom.xml ./jpo-ode-plugins/
COPY ./jpo-ode-plugins/src ./jpo-ode-plugins/src
COPY ./jpo-ode-common/pom.xml ./jpo-ode-common/
COPY ./jpo-ode-common/src ./jpo-ode-common/src
COPY ./jpo-security/pom.xml ./jpo-security/
COPY ./jpo-security/src ./jpo-security/src

RUN mvn clean install -DskipTests

FROM openjdk:8u171-jre-alpine

WORKDIR /home

COPY --from=builder /home/jpo-ode-svcs/src/main/resources/application.properties /home
COPY --from=builder /home/jpo-ode-svcs/src/main/resources/logback.xml /home
COPY --from=builder /home/jpo-ode-svcs/target/jpo-ode-svcs-0.0.1-SNAPSHOT.jar /home

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
	"/home/jpo-ode-svcs-0.0.1-SNAPSHOT.jar"]
