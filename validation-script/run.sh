#!/bin/sh
if [[ -z "$DOCKER_HOST_IP" ]]; then
   echo "ERROR! Required environment variable DOCKER_HOST_IP not set"
   exit 1
fi
mkdir data | true
cp ../data/bsmLogDuringEvent.gz ./data
docker build -t ode-metadata-validation .
docker run --rm -e DOCKER_HOST_IP=$DOCKER_HOST_IP ode-metadata-validation:latest
rm -rf ./data
