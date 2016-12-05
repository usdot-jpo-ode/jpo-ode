# Docker Images
	jpo-ode:v5
## ODE Service Image
## Steps to Run Docker image from windows 7:

 Note: Make sure visualization is enabled in your BIOS
 1. Change to dirctory with Dockerfile in it
 2. Run Command: docker-machine start default
 3. Run Command: docker-machine env
 4. Run Command: @FOR /f "tokens=*" %i IN ('docker-machine env') DO @%i
 5. Run Command: docker build -t jpo-ode:<tagName> .
 6. Run Command: docker run -p 18080:18080 -it jpo-ode:<tagName> /bin/bash
 7. Change to home directory in the image
 8. Run Command: mvn clean install if project is not already built
 9. Run Command: java -Dloader.path=. -jar jpo-ode-svcs=0.0.1-SNAPSHOT.jar
## Helpful commands
 1. docker ps ;Lists running images
 2. docker-machine ls ;Lists docker containers and the associated ip and port numbers
 
## Kafka Broker Image
	Version 0.10.1.0
## Running Zookeeper and Kafka in the image
	$KAFKA_HOME/bin/zookeeper-server-start.sh  $KAFKA_HOME/config/zookeeper.properties
#	After the command is run press ctrl-z to pause the process, then put the process in the background by running bg
	$KAFKA_HOME/bin/kafka-server-start.sh  $KAFKA_HOME/config/server.properties
#	After the command is run press ctrl-z to pause the process, then put the process in the background by running bg
Note: At this point you should be able to access the ode webpage from the port name specified in the run command @line12
## Identity and Access Management Image

