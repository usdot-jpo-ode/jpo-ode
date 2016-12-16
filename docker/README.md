# Docker Images
	jpo-ode:MVP
## Steps to Run Docker image from windows 7:
Note: Make sure visualization is enabled in your BIOS
 1. In command line change to dirctory with Dockerfile in it
 2. Run Command: docker-machine start default
 3. Run Command: docker-machine env
 4. Manually set the environment variables shown in your system environment(make them static)
 5. Run Command: @FOR /f "tokens=*" %i IN ('docker-machine env') DO @%i
 6. Run Command: docker build -t jpo-ode:<tagName> .
 7. Run Command: docker run -p 9092:9092 --hostname kafka -it jpo-ode:<tagName> /bin/bash
 8. Run Command: export GIT_SSL_NO_VERIFY=1
### Helpful commands
 1. docker ps ;Lists running images
 2. docker-machine ls ;Lists docker containers and the associated ip and port numbers
## Kafka Broker Image
	Version 0.10.1.0
###Running Zookeeper, Kafka, and ODE in the image
 1. Run ./start.sh from root directory
 2. Run ./createTopic.sh from root directory
 3. Before exiting the container run ps and kill the two java processes for zookeeper and kafka
Note: At this point you should be able to access the ode webpage from the port name specified in the run command @line12
 3. Change to home directory in the image
 4. Run Command: mvn clean install if project is not already built
 5. Run Command: java -Dloader.path=. -jar jpo-ode-svcs=0.0.1-SNAPSHOT.jar
