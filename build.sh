#!/bin/sh
git clone https://usdot-jpo-ode@bitbucket.org/usdot-jpo-ode/jpo-ode-private.git
cd jpo-ode-private
mvn clean
mvn install
cd ..
mvn install
