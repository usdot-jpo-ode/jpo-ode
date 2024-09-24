#!/bin/bash

# NOTE: This is a bash script intended to be used by the Dockerfile and not intended to be used on its own

# Create the necessary openrc directories on boot of the new container
sh -c rc-status
# Create the necessary file to run a service on openrc. This is necessary when using an alpine image
touch /run/openrc/softlevel
# Start the SSH/SCP server on boot of the new container
# This will utilize the /root/.ssh/authorized_keys file for determining authorized users - this is intended to be Onboard Units
rc-service sshd start

# Start the jpo-ode application
java -Djava.rmi.server.hostname=$DOCKER_HOST_IP -Dcom.sun.management.jmxremote.port=9090 -Dcom.sun.management.jmxremote.rmi.port=9090 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dlogback.configurationFile=/home/logging/logback.xml -jar /home/jpo-ode-svcs.jar
