docker run -it -v %DOCKER_SHARED_VOLUME%:/asn1_codec -e DOCKER_HOST_IP=%DOCKER_HOST_IP% -e ACM_CONFIG_FILE=aem.properties jpoode_acm:latest %1
