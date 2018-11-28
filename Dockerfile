FROM maven:3.5.4-jdk-8-alpine as builder
MAINTAINER 583114@bah.com

# Set up the private repo first
WORKDIR /home/jpo-ode-private

COPY ./jpo-ode-private/pom.xml ./pom.xml
COPY ./jpo-ode-private/j2735/pom.xml ./j2735/pom.xml
COPY ./jpo-ode-private/ieee1609dot2/pom.xml ./ieee1609dot2/pom.xml
COPY ./jpo-ode-private/lib/*.jar ./lib/

RUN mvn install:install-file -Dfile=./lib/LunaProvider.jar -DgroupId=com.safenet-inc -DartifactId=luna-provider -Dversion=6.2.2-4 -Dpackaging=jar
RUN mvn install:install-file -Dfile=./lib/jprov_sfnt.jar -DgroupId=com.safenet-inc -DartifactId=safenet-provider -Dversion=5.4 -Dpackaging=jar
RUN mvn install:install-file -Dfile=./lib/oss.jar -DgroupId=asn_1 -DartifactId=oss_soed -Dversion=6.3 -Dpackaging=jar
RUN mvn install:install-file -Dfile=./lib/osslean.jar -DgroupId=asn_1 -DartifactId=oss_lean -Dversion=6.3 -Dpackaging=jar
RUN mvn install:install-file -Dfile=./lib/osstoed.jar -DgroupId=asn_1 -DartifactId=oss_toed -Dversion=6.3 -Dpackaging=jar

RUN mvn dependency:go-offline -B

COPY ./jpo-ode-private .

RUN mvn clean
RUN mvn install -DskipTests

# Now set up the public repo
WORKDIR /home

# COPY ./pom.xml ./pom.xml
# COPY ./jpo-ode-core/pom.xml ./jpo-ode-core/pom.xml
# COPY ./jpo-ode-svcs/pom.xml ./jpo-ode-svcs/pom.xml
# COPY ./jpo-ode-plugins/pom.xml ./jpo-ode-plugins/pom.xml
# COPY ./jpo-ode-common/pom.xml ./jpo-ode-common/pom.xml
# COPY ./jpo-security/pom.xml ./jpo-security/pom.xml
# COPY ./jpo-security-svcs/pom.xml ./jpo-security-svcs/pom.xml
#
# RUN mvn dependency:go-offline package -B

COPY ./ ./

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
