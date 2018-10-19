#!/bin/bash
docker run -it -v /var/run/docker.sock:/var/run/docker.sock -v ${DOCKER_SHARED_VOLUME}:/ppm_data -p '8080:8080' -e DOCKER_HOST_IP=$DOCKER_HOST_IP jpo-ode_ode:latest $1