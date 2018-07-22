#!/bin/bash
docker-compose stop
docker-compose rm -f -v
mvn clean install
mvn --projects jpo-s3-deposit package assembly:single
docker-compose up --build -d
docker-compose ps
