#!/bin/bash
docker-compose stop
docker-compose rm -f -v
mvn clean install
docker-compose up --build -d
docker-compose ps
