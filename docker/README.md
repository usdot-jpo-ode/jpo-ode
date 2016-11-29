# Docker Images
	jpo-ode:v5
## ODE Service Image
## Steps to Run Docker image:

 1. Change to dirctory with Dockerfile in it
 2. Run Command: docker-machine start default
 3. Run Command: docker-machine env
 4. Run Command: @FOR /f "tokens=\*" %i IN ('docker-machine env') DO @%i
 5. Run Command: docker build -t jpo-ode:<tagName> .
 6. Run Command: docker run -i -t jpo-ode:<tagName> /bin/bash
 7. Change to home directory in the image
 8. Run Command: mvn clean install if project is not already built
 9. Run Command: java -Dloader.path=. -jar jpo-ode-svcs=0.0.1-SNAPSHOT.jar


## Kafka Broker Image
	Version 0.10.1.0
## Identity and Access Management Image
