# Docker Image
	jpoode_ode:latest
## Kafka Broker Image
	Version 0.10.1.0
## Prerequisites for windows 7:
 0. Make sure visualization is enabled in your BIOS
 1. Run Command: docker-machine start default
 3. Run Command: docker-machine env
 4. Set DOcker environment variables:
	 a. Manually set the environment variables shown in your system environment permanently (make them static)
   OR
     b. Run Command: @FOR /f "tokens=*" %i IN ('docker-machine env') DO @%i

## Step 1 - Build the application
Run ```build.cmd``` or ```build.sh``` according to your platform. This shell script will install jpo-ode-private and then install jpo-ode projects.

## Step 2 - Run docker compose to build and run a multi-container application
 6. Set the environment variable ```DOCKER_HOST_IP``` to the IP address of the machine that will be hosting the application containers. Alternatively, create a ```.env``` file in the same directory as ```docker-compose.yml```. 
 7. Run Command: ```docker-compose up -d``` to start the containers for the applications. Containers include: One zookeeper instance bound to port 2181, one kafka instance bound to port 9092 and jpo-ode-svcs application bound to port *18080*.

## Helpful hints
 8. Run Command: export GIT_SSL_NO_VERIFY=1 1. docker ps ;Lists running images
 2. docker-machine ls ;Lists docker containers and the associated ip and port numbers
