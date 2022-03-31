#-------------------------------------------------------------------------------------------------------------
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License. See https://go.microsoft.com/fwlink/?linkid=2090316 for license information.
#-------------------------------------------------------------------------------------------------------------

FROM openjdk:11-jdk

# This Dockerfile adds a non-root user with sudo access. Use the "remoteUser"
# property in devcontainer.json to use it. On Linux, the container user's GID/UIDs
# will be updated to match your local UID/GID (when using the dockerFile property).
# See https://aka.ms/vscode-remote/containers/non-root-user for details.
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

# Configure apt
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
    && apt-get upgrade -y \
    && apt-get -y install --no-install-recommends apt-utils dialog 2>&1 \
    #
    # Create a non-root user to use if preferred - see https://aka.ms/vscode-remote/containers/non-root-user.
    && groupadd --gid $USER_GID $USERNAME \
    && useradd -s /bin/bash --uid $USER_UID --gid $USER_GID -m $USERNAME \
    # [Optional] Add sudo support for the non-root user
    && apt-get install -y sudo \
    && echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME\
    && chmod 0440 /etc/sudoers.d/$USERNAME \
    #
    # Verify git, needed tools installed
    && apt-get -y install git openssh-client less iproute2 procps curl lsb-release zip unzip sed kafkacat telnet

#-------------------Install SDKMan----------------------------------
RUN curl -s https://get.sdkman.io | bash
RUN chmod a+x "$HOME/.sdkman/bin/sdkman-init.sh"
#-------------------------------------------------------------------------------------------------------------

#-------------------Install Maven CLI Tools----------------------------------
ARG MAVEN_VERSION=3.6.3
RUN /bin/bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk install maven ${MAVEN_VERSION}"
#-------------------------------------------------------------------------------------------------------------

# Install snmp (rsu-data-controller dependency)
RUN apt-get -y install snmp

#-------------------Install Kafka----------------------------------
RUN mkdir ~/Downloads
RUN curl "https://archive.apache.org/dist/kafka/2.1.1/kafka_2.11-2.1.1.tgz" -o ~/Downloads/kafka.tgz
RUN mkdir ~/kafka \
    && cd ~/kafka \
    && tar -xvzf ~/Downloads/kafka.tgz --strip 1
RUN echo "\nadvertised.listeners=PLAINTEXT://localhost:9092" >> ~/kafka/config/server.properties    
#-------------------------------------------------------------------------------------------------------------

# Clean up
RUN apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*
ENV DEBIAN_FRONTEND=dialog

# Allow for a consistant java home location for settings - image is changing over time
RUN if [ ! -d "/docker-java-home" ]; then ln -s "${JAVA_HOME}" /docker-java-home; fi

COPY kafka /etc/init.d/kafka
RUN chmod 775 /etc/init.d/kafka && update-rc.d kafka defaults

COPY ./docker-entrypoint.sh ./docker-entrypoint.sh
RUN chmod +x ./docker-entrypoint.sh
ENTRYPOINT ["./docker-entrypoint.sh"]