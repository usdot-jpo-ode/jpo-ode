FROM maven:3.8-eclipse-temurin-21-alpine as builder
MAINTAINER 583114@bah.com

WORKDIR /home

# Copy only the files needed to avoid putting all sorts of junk from your local env on to the image
COPY ./pom.xml ./
COPY ./jpo-ode-common/pom.xml ./jpo-ode-common/
COPY ./jpo-ode-common/src ./jpo-ode-common/src
COPY ./jpo-ode-plugins/pom.xml ./jpo-ode-plugins/
COPY ./jpo-ode-plugins/src ./jpo-ode-plugins/src
COPY ./jpo-ode-core/pom.xml ./jpo-ode-core/
COPY ./jpo-ode-core/src ./jpo-ode-core/src/
COPY ./jpo-ode-svcs/pom.xml ./jpo-ode-svcs/
COPY ./jpo-ode-svcs/src ./jpo-ode-svcs/src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /home

COPY --from=builder /home/jpo-ode-svcs/src/main/resources/application.yaml /home
COPY --from=builder /home/jpo-ode-svcs/src/main/resources/logback.xml /home
COPY --from=builder /home/jpo-ode-svcs/target/jpo-ode-svcs.jar /home
COPY ./scripts/startup_jpoode.sh /home

RUN apk add openssh
RUN apk add openrc
RUN rc-update add sshd

ENTRYPOINT ["sh", "/home/startup_jpoode.sh"]
