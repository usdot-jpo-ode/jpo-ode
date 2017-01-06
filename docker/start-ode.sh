#!/bin/bash
#java -jar /home/jpo-ode-svcs-0.0.1-SNAPSHOT.jar &
#java -jar /home/jpo-ode-svcs-0.0.1-SNAPSHOT.jar --ode.kafkaBrokers=$(broker-list.sh)
#echo starting docker serivce
#service docker start
#echo waiting for docker service to start
#sleep 10
#docker ps
#BROKER_LIST=`broker-list.sh`
#export BROKER_LIST
#echo BROKER_LIST=$BROKER_LIST
#echo java -jar /home/jpo-ode-svcs-0.0.1-SNAPSHOT.jar --ode.kafkaBrokers=$BROKER_LIST
#java -jar /home/jpo-ode-svcs-0.0.1-SNAPSHOT.jar --ode.kafkaBrokers=$BROKER_LIST
echo starting ODE
cd /home
#mkdir -p /home/uploads
#java -Xdebug -Xrunjdwp:transport=dt_socket,address=8001,server=y,suspend=y -jar /home/jpo-ode-svcs-0.0.1-SNAPSHOT.jar
java -jar /home/jpo-ode-svcs-0.0.1-SNAPSHOT.jar
