# Docker Image
	jpoode_ode:latest
## Kafka Broker Image
	Version 0.10.1.0
	
## Prerequisites

- Install docker-compose [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)
- Set the environment variable ```DOCKER_HOST_IP``` to the IP address of the machine that will be hosting the application containers. Alternatively, create a ```.env``` file in the same directory as ```docker-compose.yml```. 
- if you want to customise any Kafka parameters, simply add them as environment variables in ```docker-compose.yml```, e.g. in order to increase the ```message.max.bytes``` parameter set the environment to ```KAFKA_MESSAGE_MAX_BYTES: 2000000```. To turn off automatic topic creation set ```KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'false'```

## Additional Prerequisites for Windows 7:
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

## Step 2 - Run docker-compose to build and run as a multi-container application
Run Command: ```docker-compose up -d``` to start the containers for the applications. Containers include: one Zookeeper instance bound to port 2181, one Kafka instance bound to port 9092, and jpo-ode-svcs application bound to port *8080*.

## Helpful hints

 ### Obtaining DOCKER_HOST_IP
  1. Run `ifconfig`
  2. Look for "inet addr:" under the desired network interface - generally "eth0" for Linux and "en0" for OSX
  3. Copy that IP address and then run command `export DOCKER_HOST_IP=<addr>`
  
 ### Resolve Git SSL issues by disabling SSL verification
   - `export GIT_SSL_NO_VERIFY=1`
 ### Docker commands
   - Deploy and start all containers 
     - `docker-compose up --no-recreate -d`
   - Lists the running containers under docker-compose
     - `docker-compose ps`
   - Start a container
     - `docker-compose start <container>`
   - Restart a container
     - `docker-compose restart <container>`
   - Stop a container
     - `docker-compose stop <container>`
   - Stop all containers
     - `docker-compose stop`

## Troubleshooting

- Docker builds fail with a `wget: bad address` error
   - [Docker fix for SSL issues due to corporate network](https://github.com/usdot-jpo-ode/jpo-ode/wiki/Docker-fix-for-SSL-issues-due-to-corporate-network)

