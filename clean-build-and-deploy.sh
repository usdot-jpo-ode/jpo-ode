#!/bin/bash
docker-compose stop
docker-compose rm -f -v
mvn -DskipTests clean install
docker-compose up --build -d
docker-compose ps
