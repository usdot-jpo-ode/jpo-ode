
# Getting Started with Docker
The following sections summarize installation and setup of Docker on your machine.
## Windows
- Enable virtualization in your BIOS
- Install [Docker for Windows](https://store.docker.com/editions/community/docker-ce-desktop-windows)
## Mac OSX
- Enable virtualization in your BIOS
- Install [Docker for Mac](https://hub.docker.com/editions/community/docker-ce-desktop-mac/)
## Linux
- Install [Docker Engine](https://store.docker.com/search?type=edition&offering=community&operating_system=linux) on Docker host machine
- Install [docker-compose](https://docs.docker.com/compose/install/) on docker host and/or local machine
- Create a ```.env``` file in the same directory as ```docker-compose.yml```. 

## Step 1 - Build and deploy the application
Run ```docker-compose up --build -d```

This starts the configured containers.

## Helpful hints

 ### Obtaining DOCKER_HOST_IP
1. **Windows**: Run `ifconfig`
2. **OSX**: Look for "inet addr:" under the "en0" network interface
3. **Linux**: Look for "inet addr:" under the "eth0" network interface
  
 ### Resolve Git SSL issues by disabling SSL verification
   - `export GIT_SSL_NO_VERIFY=1`
 ### Docker commands
 - Bring UP (deploy and start) all containers 
   - `docker-compose up -d`
 - Bring DOWN all containers 
   - `docker-compose down`
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
