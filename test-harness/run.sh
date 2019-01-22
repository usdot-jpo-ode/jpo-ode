#!/bin/sh
docker build -t ode-test-harness . 
docker run ode-test-harness
