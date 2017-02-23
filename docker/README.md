# Docker Image
	jpoode_ode:latest
## Kafka Broker Image
	Version 0.10.1.0
	
##Pre-Requisites

- install docker-compose [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)
- Set the environment variable ```DOCKER_HOST_IP``` to the IP address of the machine that will be hosting the application containers. Alternatively, create a ```.env``` file in the same directory as ```docker-compose.yml```. 
- if you want to customise any Kafka parameters, simply add them as environment variables in ```docker-compose.yml```, e.g. in order to increase the ```message.max.bytes``` parameter set the environment to ```KAFKA_MESSAGE_MAX_BYTES: 2000000```. To turn off automatic topic creation set ```KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'false'```

## Additional Prerequisites for windows 7:
 0. Make sure visualization is enabled in your BIOS
 1. Run Command: docker-machine start default
 3. Run Command: docker-machine env
 4. Set Docker environment variables:
	 a. Manually set the environment variables shown in your system environment permanently (make them static)
   OR
     b. Run Command: @FOR /f "tokens=*" %i IN ('docker-machine env') DO @%i

 5. docker-machine ls ;Lists docker containers and the associated ip and port numbers

## Step 1 - Build the application
Run ```full-build.cmd``` or ```full-build.sh``` according to your platform. This shell script will clone jpo-ode-private under jpo-ode, installs it, and then installs jpo-ode projects.

## Step 2 - Run docker compose to build and run a multi-container application
 2. Run Command: ```docker-compose up -d``` to start the containers for the applications. Containers include: One zookeeper instance bound to port 2181, one kafka instance bound to port 9092 and jpo-ode-svcs application bound to port *18080*.

## Helpful hints
 1. Run Command: export GIT_SSL_NO_VERIFY=1
 2. docker-compose up --no-recreate -d ; To deploy and start all services. 
 3. docker-compose ls ; Lists the running containers under docker-compose
 4. docker-compose start <service> ; To start a service
 5. docker-compose restart <service> ; To restart a service
 6. docker-compose stop <service> ; To stop a service
 7. docker-compose stop ; To stop all serviecs
