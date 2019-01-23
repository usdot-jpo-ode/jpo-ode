#!/bin/bash
docker run -it --env-file=.env -v /var/run/docker.sock:/var/run/docker.sock -v ${DOCKER_SHARED_VOLUME}:/ppm_data -p '8080:8080' jpoode_ode:latest $1