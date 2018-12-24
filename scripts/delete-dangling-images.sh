# Removes all old/dangling Docker images
# without stopping currently running containers.
if [[ $(docker ps -q -f status=exited | wc -c) -ne 0 ]]; then
    docker rm $(docker ps -q -f status=exited)
else
    echo "No dead containers found."
fi

if [[ $(docker volume ls -qf dangling=true | wc -c) -ne 0 ]]; then
    docker volume rm $(docker volume ls -qf dangling=true)
else
    echo "No dangling volumes found."
fi

if [[ $(docker images --filter "dangling=true" -q --no-trunc | wc -c) -ne 0 ]]; then
    docker rmi --force $(docker images --filter "dangling=true" -q --no-trunc)
else
    echo "No dangling images found."
fi
