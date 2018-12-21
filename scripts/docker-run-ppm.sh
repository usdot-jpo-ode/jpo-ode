#!/bin/bash
docker run -it --env-file=.env -v ${DOCKER_SHARED_VOLUME}:/ppm_data jpoode_ppm:latest $1
