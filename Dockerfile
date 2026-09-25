FROM maven:3.9-eclipse-temurin-25-noble AS builder
LABEL org.opencontainers.image.authors="583114@bah.com"

WORKDIR /home

COPY ./jpo-asn-pojos/pom.xml ./jpo-asn-pojos/

COPY ./jpo-asn-pojos/jpo-asn-runtime/pom.xml ./jpo-asn-pojos/jpo-asn-runtime/
COPY ./jpo-asn-pojos/jpo-asn-runtime/src ./jpo-asn-pojos/jpo-asn-runtime/src

COPY ./jpo-asn-pojos/jpo-asn-j2735-2024/pom.xml ./jpo-asn-pojos/jpo-asn-j2735-2024/
COPY ./jpo-asn-pojos/jpo-asn-j2735-2024/src ./jpo-asn-pojos/jpo-asn-j2735-2024/src

# First build and install the jpo-asn-pojos modules
RUN cd jpo-asn-pojos && mvn clean install -DskipTests

COPY ./pom.xml ./
COPY ./jpo-ode-common/pom.xml ./jpo-ode-common/
COPY ./jpo-ode-common/src ./jpo-ode-common/src
COPY ./jpo-ode-plugins/pom.xml ./jpo-ode-plugins/
COPY ./jpo-ode-plugins/src ./jpo-ode-plugins/src
COPY ./jpo-ode-core/pom.xml ./jpo-ode-core/
COPY ./jpo-ode-core/src ./jpo-ode-core/src/
COPY ./jpo-ode-svcs/pom.xml ./jpo-ode-svcs/
COPY ./jpo-ode-svcs/src ./jpo-ode-svcs/src

# Then build the rest of the project. The FFMLib dependency and its native libraries are resolved
# from Maven Central.
RUN mvn -pl jpo-ode-common,jpo-ode-plugins,jpo-ode-core,jpo-ode-svcs -am package -DskipTests

FROM eclipse-temurin:25-jre-noble

WORKDIR /home

COPY --from=builder /home/jpo-ode-svcs/src/main/resources/application.yaml /home
COPY --from=builder /home/jpo-ode-svcs/src/main/resources/logback.xml /home
COPY --from=builder /home/jpo-ode-svcs/target/jpo-ode-svcs.jar /home
COPY --from=builder /home/jpo-ode-svcs/target/libs/libasnapplication.so /home/libs/libasnapplication.so
COPY ./scripts/startup_jpoode.sh /home

RUN apt-get update \
    && apt-get install -y --no-install-recommends openssh-server curl \
    && mkdir -p /run/sshd \
    && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["sh", "/home/startup_jpoode.sh"]
