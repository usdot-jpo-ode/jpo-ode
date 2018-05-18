FROM ubuntu:18.04
MAINTAINER 583114@bah.com

#Install necessary software
RUN apt-get update && \
    apt-get install -y software-properties-common git
RUN apt-get update && \
    apt-get install -y apt-utils
RUN apt-get update && \
    apt-get install -y wget supervisor dnsutils curl jq net-tools
RUN apt-get update && \
    apt-get install -y openjdk-8-jdk
RUN apt-get update && \
    apt-get install -y vim
RUN apt-get update && \
    apt-get install -y nano
#RUN apt-get update && \
#   apt-cache search maven && \
#    apt-get install -y maven
	
RUN apt-get clean

##install docker
#RUN apt-get install -y apt-transport-https ca-certificates
#RUN apt-key adv \
#  --keyserver hkp://ha.pool.sks-keyservers.net:80 \
#  --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
#RUN echo "deb https://apt.dockerproject.org/repo ubuntu-xenial main" | tee /etc/apt/sources.list.d/docker.list
#RUN apt-get update && apt-get install -y docker docker-engine
#RUN apt-cache policy docker-engine
##RUN apt-get install -y linux-image-extra-$(uname -r) linux-image-extra-virtual
#RUN service docker start

#Add ODE binary files
ADD jpo-ode-svcs/target/jpo-ode-svcs-0.0.1-SNAPSHOT.jar /home

ADD docker/start-ode.sh /usr/bin/start-ode.sh
ADD jpo-ode-svcs/src/main/resources/application.properties /home
ADD jpo-ode-svcs/src/main/resources/logback.xml /home

# Build ODE inside the image
#RUN cd /home/jpo-ode/jpo-ode-private && mvn clean && mvn install
#RUN cd /home/jpo-ode && mvn clean && mvn install
#RUN cp /home/jpo-ode/jpo-ode-svcs/target/jpo-ode-svcs-0.0.1-SNAPSHOT.jar /home

#Change permissions and run scripts
RUN chmod a+x /usr/bin/start-ode.sh

# Use "exec" form so that it runs as PID 1 (useful for graceful shutdown)
#CMD bash -c 'start-kafka.sh & ; start-ode.sh'
CMD bash -c 'start-ode.sh'
