#!/bin/sh
if [[ -z "$DOCKER_HOST_IP" ]]; then
   echo "ERROR! Required environment variable DOCKER_HOST_IP not set"
   exit 1
fi
docker build -t ode-test-harness .
docker run --rm -e DOCKER_HOST_IP=$DOCKER_HOST_IP ode-test-harness:latest
