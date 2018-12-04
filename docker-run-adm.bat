docker run -it --env-file=.env -v %DOCKER_SHARED_VOLUME%:/asn1_codec_share -e DOCKER_HOST_IP=%DOCKER_HOST_IP% -e ACM_CONFIG_FILE=adm.properties jpoode_acm:latest %1
