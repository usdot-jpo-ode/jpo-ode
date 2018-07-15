#!/bin/sh
cd ..
git clone https://usdot-jpo-ode@bitbucket.org/usdot-jpo-ode/jpo-ode-private.git
cd jpo-ode-private
mvn clean
mvn install
cd ../jpo-ode
mvn clean install
mvn --projects jpo-s3-deposit package assembly:single
docker-compose up --build --no-recreate -d
