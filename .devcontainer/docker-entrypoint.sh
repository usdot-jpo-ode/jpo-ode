#!/usr/bin/env bash

trap stop SIGTERM SIGINT SIGQUIT SIGHUP ERR

start() {
    echo "Container started, starting kafka..." >>/root/startup.txt
    sudo service kafka start
}

stop() {
    echo "Container stopped, performing cleanup..." >>/root/shutdown.txt
    sudo service kafka stop
    sleep 2
    exit 0
}

start

while
    sleep 1 &
    wait $!
do :; done
