#!/bin/sh
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock --env-file ./.env -it jpoode_kafka:latest /bin/bash
