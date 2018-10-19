docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -e DOCKER_HOST_IP=%DOCKER_HOST_IP% -e ZK=%DOCKER_HOST_IP%:2181 -it jpo-ode_kafka:latest /bin/bash
