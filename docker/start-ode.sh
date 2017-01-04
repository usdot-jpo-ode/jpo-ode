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
java -jar /home/jpo-ode-svcs-0.0.1-SNAPSHOT.jar --ode.kafkaBrokers=$DOCKER_HOST_IP:9092
