# Docker Image
	jpo-ode:v5
## ODE Service Image
## Kafka Broker Image
	Version 0.10.1.0
## Steps to Run Docker image from windows 7:

 Note: Make sure visualization is enabled in your BIOS
 1. Change to dirctory with Dockerfile in it
 2. Run Command: docker-machine start default
 3. Run Command: docker-machine env
 4. Manually set the environment variables shown in your system environment(make them static)
 5. Run Command: @FOR /f "tokens=*" %i IN ('docker-machine env') DO @%i
 6. Run Command: `docker build -t jpo-ode:<tagName> .`
 7. Run Command: `docker run -p 8080:8080 -it jpo-ode:<tagName> /bin/bash`
## Commands to run in the container
1. export GIT_SSL_NO_VERIFY=1
2. cd home
3. $KAFKA_HOME/bin/zookeeper-server-start.sh  $KAFKA_HOME/config/zookeeper.properties &
3. $KAFKA_HOME/bin/kafka-server-start.sh  $KAFKA_HOME/config/server.properties &
4. java -jar jpo-ode-svcs=0.0.1-SNAPSHOT.jar

At this point you should be able to access the ode webpage from port 8080

## Helpful commands
 1. docker ps ;Lists running images
 2. docker-machine ls ;Lists docker containers and the associated ip and port numbers
