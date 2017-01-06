Master: [![Build Status](https://travis-ci.org/usdot-jpo-ode/jpo-ode.svg?branch=master)](https://travis-ci.org/usdot-jpo-ode/jpo-ode) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=us.dot.its.jpo.ode:jpo-ode)](https://sonarqube.com/dashboard/index?id=us.dot.its.jpo.ode%3Ajpo-ode)

Develop: [![Build Status](https://travis-ci.org/usdot-jpo-ode/jpo-ode.svg?branch=develop)](https://travis-ci.org/usdot-jpo-ode/jpo-ode) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=us.dot.its.jpo.ode:jpo-ode:develop)](https://sonarqube.com/dashboard/index?id=us.dot.its.jpo.ode%3Ajpo-ode%3Adevelop)

# jpo-ode
US Department of Transportation Joint Program office (JPO) Operational Data Environment (ODE)

In the context of ITS, an Operational Data Environment is a real-time data acquisition and distribution software system that processes and routes data from Connected-X devices –including connected vehicles (CV), personal mobile devices, and infrastructure components and sensors –to subscribing applications to support the operation, maintenance, and use of the transportation system, as well as related research and development efforts.
 
## Release Notes
### Release 1.0

### 2017-01-06
ODE_41 Importer improvements

### 2017-01-02
- ODE-42 Clean up the kafka adapter and make it work with Kafka broker
Integrated kafka. Kept Stomp as the high level WebSocket API protocol.
- ODE-36 - Docker, docker-compose, Kafka and ode Integration

## Collaboration Tools

### Source Repositories - GitHub
- Main repository on GitHub (public)
	- https://github.com/usdot-jpo-ode/jpo-ode
	- git@github.com:usdot-jpo-ode/jpo-ode.git
- Private repository on BitBucket
	- https://usdot-jpo-ode@bitbucket.org/usdot-jpo-ode/jpo-ode-private.git
	- git@bitbucket.org:usdot-jpo-ode/jpo-ode-private.git

### Agile Project Management - Jira
https://usdotjpoode.atlassian.net/secure/Dashboard.jspa

### Wiki - Confluence
https://usdotjpoode.atlassian.net/wiki/

### Continuous Integration and Delivery
https://travis-ci.org/usdot-jpo-ode/jpo-ode

To allow Travis run your build when you push your changes to your public fork of the jpo-ode repository, you must define the following secure environment variable using Travis CLI (https://github.com/travis-ci/travis.rb). 

Run:

```
travis login --org
```
Enter personal github account credentials and then run this:

```
travis env set PRIVATE_REPO_URL_UN_PW 'https://<bitbucketusername>:<password>@bitbucket.org/usdot-jpo-ode/jpo-ode-private.git' -r <travis username>/jpo-ode
```

The login information will be saved and this needs to be done only once.

In order to allow Sonar to run, personal key must be added with this command:
(Key can be obtained from the JPO-ODE development team)

```
travis env set SONAR_SECURITY_TOKEN <key> -pr <user-account>/<repo-name>
```

### Static Code Analysis
https://sonarqube.com/dashboard/index?id=us.dot.its.jpo.ode%3Ajpo-ode%3Adevelop

## Getting Started

### Local Build

#### Prerequisites
* Maven: https://maven.apache.org/install.html
* Git: https://git-scm.com/

#### Instructions

The following instructions describe the procedure to fetch, build and run the application.

##### Getting the source Code
Clone the source code from GitHub and BitBucket repositories using Git commands:

```bash
git clone https://github.com/usdot-jpo-ode/jpo-ode.git
git clone https://usdot-jpo-ode@bitbucket.org/usdot-jpo-ode/jpo-ode-private.git
```

**NOTE**: If running on Windows, please make sure that your global git config is set up to not convert End-of-Line characters during checkout. This is important for building docker images correctly.
```bash
git config --global core.autocrlf false
``` 

##### Building ODE Application

To build the application use maven command line. 

**Step 1**. Navigate to the root directory of the jpo-ode-private project:

```bash
 cd jpo-ode-private/
 mvn clean
 mvn install
```
It is important you run mvn clean first and then mvn install because mvn clean installs the required OSS jar file in your maven local repository. 

**Step 2**. Navigate to the root directory of the jpo-ode project:
If you wish to change the application properties, such as change the location of the upload service via ode.uploadLocation property or set the ode.kafkaBrokers to something other than the $DOCKER_HOST_IP:9092, modify ```jpo-ode-svcs\src\main\resources\application.properties``` file as desired.

Run the following commands to build the application containers.
```bash
 cd jpo-ode (or cd ../jpo-ode if you are in the jpo-ode-private directory) 
 mvn clean install
 docker-compose rm -f -v
 docker-compose build
```

Alternatively, run the script ```clean-build``` script.

##### Deploying ODE Application on a Docker Host
To run the application, from jpo-ode directory: 

```bash
docker-compose up --no-recreate -d
```

**NOTE**: It's important to run ```docker-compose up``` with ```no-recreate``` option. Otherwise you may run into [this issue] (https://github.com/wurstmeister/kafka-docker/issues/100).

Alternatively, run ```deploy``` script.

Check the deployment by running ```docker-compose ps```. You can start and stop service using ```docker-compose start``` and ```docker-compose stop``` commands. 
If using the multi-broker docker-compose file, you can change the scaling by running ```docker-compose scale <service>=n``` where service is the service you would like to scale and n is the number of instances. For example, ```docker-compose scale kafka=3```.

##### Build and deploy in one step
To build and run the application in one step, run the ```clean-build-and-deploy``` script.
 
##### Running ODE Application Locally
You can run the application on your local machine while other services are deployed on a host environment. To do so, run the following:
```bash
 docker-compose start zookeeper kafka
 java -jar jpo-ode-svcs/target/jpo-ode-svcs-0.0.1-SNAPSHOT.jar
```

##### Testing ODE Application
You should be able to access the jpo-ode UI at `localhost:8080`.

Upload a file containing BSM messages in ASN.1 Hexadecimal encoded format. For example, a file containing the following record:
```text
401480CA4000000000000000000000000000000000000000000000000000000000000000F800D9EFFFB7FFF00000000000000000000000000000000000000000000000000000001FE07000000000000000000000000000000000001FF0
```

1. Press the ```Connect``` button to connect to the ODE WebSocket service.
2. Press ```Choose File``` button to select the file with the ASN.1 Hex BSM record in it.
3. Press ```Upload``` button to upload the file to ODE.

Another way data can be uploaded to the ODE is through copying the file to the location specified by the ode.uploadLocation property. Default location is the ```uploads``` directory directly off of the directory where ODE is launched.
The result of uploading and decoding of the message will be displayed on the UI screen. 

![ODE UI](images/ode-ui.png)

### Integrated Development Environment (IDE)

Install the IDE of your choice:

* Eclipse: [https://eclipse.org/](https://eclipse.org/)
* STS: [https://spring.io/tools/sts/all](https://spring.io/tools/sts/all)
* IntelliJ: [https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)


### Continuous Integration and Delivery

### Deployment

## Docker
![README.md](docker/README.md)

## Kafka
![README.md](docker/kafka/README.md)

