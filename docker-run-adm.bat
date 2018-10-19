docker run -it -v %DOCKER_SHARED_VOLUME%:/asn1_codec_share -e DOCKER_HOST_IP=%DOCKER_HOST_IP% -e ACM_CONFIG_FILE=adm.properties jpo-ode_acm:latest %1
