#!/bin/bash

# NOTE: This is a bash script intended to be used by the Dockerfile and not intended to be used on its own

# Start the SSH/SCP server used by Onboard Units. Ubuntu Noble does not use OpenRC.
mkdir -p /run/sshd
/usr/sbin/sshd

# Start the jpo-ode application
exec java -Djava.rmi.server.hostname=$DOCKER_HOST_IP -Dcom.sun.management.jmxremote.port=9090 -Dcom.sun.management.jmxremote.rmi.port=9090 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dlogback.configurationFile=/home/logging/logback.xml -Djava.library.path=/home/libs --enable-native-access=ALL-UNNAMED -jar /home/jpo-ode-svcs.jar
