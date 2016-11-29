# Docker Images
	jpo-ode:v5
## ODE Service Image

## Kafka Broker Image
	Version 0.10.1.0
## Identity and Access Management Image

## Steps to Run Docker image:
#Change to dirctory with Dockerfile in it
#Run Command: docker-machine start default
#Run Command: docker-machine env
#Run Command: @FOR /f "tokens=\*" %i IN ('docker-machine env') DO @%i
#Run Command: docker build -t jpo-ode:<tagName> .
#Run Command: docker run -i -t jpo-ode:<tagName> /bin/bash
#Change to home directory in the image
#Run Command: mvn clean install if project is not already built
#Run Command: java -Dloader.path=. -jar jpo-ode-svcs=0.0.1-SNAPSHOT.jar
