Master: [![Build Status](https://travis-ci.org/usdot-jpo-ode/jpo-ode.svg?branch=master)](https://travis-ci.org/usdot-jpo-ode/jpo-ode) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=usdot.jpo.ode:jpo-ode)](https://sonarqube.com/dashboard?id=usdot.jpo.ode%3Ajpo-ode)

Develop: [![Build Status](https://travis-ci.org/usdot-jpo-ode/jpo-ode.svg?branch=develop)](https://travis-ci.org/usdot-jpo-ode/jpo-ode) [![Quality Gate](https://sonarqube.com/api/badges/gate?key=usdot.jpo.ode:jpo-ode:develop)](https://sonarqube.com/dashboard?id=usdot.jpo.ode%3Ajpo-ode%3Adevelop)

# jpo-ode
US Department of Transportation Joint Program office (JPO) Operational Data Environment (ODE)

In the context of ITS, an Operational Data Environment is a real-time data acquisition and distribution software system that processes and routes data from Connected-X devices –including connected vehicles (CV), personal mobile devices, and infrastructure components and sensors –to subscribing applications to support the operation, maintenance, and use of the transportation system, as well as related research and development efforts.

![ODE Dataflows](images/data_flow_v1.png)

<a name="toc"/>

## Table of Contents 

[I. Release Notes](#release-notes) 

[II. Documentation](#documentation) 

[III. Collaboration Tools](#collaboration-tools) 

[IV. Getting Started](#getting-started) 

[V. Testing the Application](#testing) 

[VI. ODE Limitation](#dev-tools) 

[VII. Development Tools](#dev-tools) 

[VIII.  Contribution Information](#contribution-info) 

--- 

<a name="release-notes"/>

 
## [I. Release Notes](ReleaseNotes.md)


<a name="documentation"/>

## II. Documentation
ODE provides the following living documents to keep ODE users and stakeholders informed of the latest developments:

1. [ODE Architecture](docs/JPO%20ODE%20Architecture.docx)
2. [ODE User Guide](docs/JPO_ODE_UserGuide.docx)
3. [ODE REST API Guide](https://usdot-jpo-ode.github.io/)
4. [ODE Smoke Tests](https://github.com/usdot-jpo-ode/jpo-ode/wiki/JPO-ODE-QA-Documents)

All stakeholders are invited to provide input to these documents. Stakeholders should direct all input on this document to the JPO Product Owner at DOT, FHWA, and JPO. To provide feedback, we recommend that you create an "issue" in this repository (https://github.com/usdot-jpo-ode/jpo-ode/issues). You will need a GitHub account to create an issue. If you don’t have an account, a dialog will be presented to you to create one at no cost.

<a name="collaboration-tools"/>

## III. Collaboration Tools

### Source Repositories - GitHub
- Main repository on GitHub (public)
	- https://github.com/usdot-jpo-ode/jpo-ode
	- git@github.com:usdot-jpo-ode/jpo-ode.git
- Security repository on GitHub (public)
        - https://github.com/usdot-jpo-ode/jpo-security.git
	- git@github.com:usdot-jpo-ode/jpo-security.git
- Private repository on BitBucket
	- https://usdot-jpo-ode@bitbucket.org/usdot-jpo-ode/jpo-ode-private.git
	- git@bitbucket.org:usdot-jpo-ode/jpo-ode-private.git
- Data Privacy Module on Github (public)
	- https://github.com/usdot-jpo-ode/jpo-cvdp
	- git@github.com/usdot-jpo-ode/jpo-cvdp
- S3 Depositor Module on Github (public)
	- https://github.com/usdot-jpo-ode/jpo-s3-deposit
	- gith@github.com/usdot-jpo-ode/jpo-s3-deposit

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
travis env set BITBUCKET_UN_APP_PW 'yourbitbucketusername:yourbitbucketpassword' -r yourtravisusername/jpo-ode
```

The login information will be saved and this needs to be done only once.

In order to allow Sonar to run, personal key must be added with this command:
(Key can be obtained from the JPO-ODE development team)

```
travis env set SONAR_SECURITY_TOKEN <key> -pr <user-account>/<repo-name>
```

### Static Code Analysis
https://sonarqube.com/organizations/usdot-jpo-ode/projects

[Back to top](#toc)

<a name="getting-started"/>

## IV. Getting Started

The following instructions describe the procedure to fetch, build, and run the application. 

### Prerequisites
* JDK 1.8: http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html
* Maven: https://maven.apache.org/install.html
* Git: https://git-scm.com/

Additionally, read the following guides to familiarize yourself with Docker and Kafka.

**Docker** 

[README.md](docker/README.md)

**Kafka** 

[README.md](docker/kafka/README.md)

---
### Obtain the Source Code

**NOTE**: The ODE consists of four repositories: 

|Name|Visibility|Description|
|----|----------|-----------|
|[jpo-ode](https://github.com/usdot-jpo-ode/jpo-ode)|public|Contains the public components of the application code.|
|[jpo-s3-deposit](https://github.com/usdot-jpo-ode/jpo-s3-deposit)|public|S3 depositor service. Optional, comment out of `docker-compose.yml` file if not used.|
|[jpo-security](https://github.com/usdot-jpo-ode/jpo-security)|public|Security dependencies.|
|jpo-ode-private|private|Proprietary dependencies.|

Building this application requires all repositories. If you need access to the private repositories, please reach out to a member of the development team. 


#### Step 1 - Clone public repository

Disable Git core.autocrlf (Only the First Time)
**NOTE**: If running on Windows, please make sure that your global git config is set up to not convert End-of-Line characters during checkout. This is important for building docker images correctly.

```bash
git config --global core.autocrlf false
```

Clone the source code from the GitHub repository using Git command:

```bash
git clone https://github.com/usdot-jpo-ode/jpo-ode.git
```

#### Step 2 - Clone S3 Bucket Depositor repository

Clone the source code from the GitHub repository:

```bash
git clone https://github.com/usdot-jpo-ode/jpo-s3-deposit.git
```

#### Step 3 - Clone the security repository:

Clone the source code from the GitHub repository:
```bash
git clone https://github.com/usdot-jpo-ode/jpo-security.git
```

#### Step 4 - Clone private repository

Clone the source code from the BitBucket repository:

```bash
git clone https://yourbitbucketusername:yourbitbucketpassword@bitbucket.org/usdot-jpo-ode/jpo-ode-private.git
```

---
### Build and Deploy the Application

#### Environment Variables
ODE configuration can be customized for every deployment environment using the OS's environment variables. The following table list the environment variables used in Docker files and shell scripts to automate the deployment process and customize it for each deployment environment. Setting these environment variables will allow the deployment to take place without any modification to the default configuration files.

|Environment Variable|Description|
|--------------------|-----------|
|[DOCKER_HOST_IP](docker/README.md#obtaining-docker_host_ip)      |The IP address of Docker host machine|
|DOCKER_SHARED_VOLUME|The full path of a directory on the host machine to be shared with docker containers.|
|CVPEP_AWS_ACCESS_KEY_ID|The data deposit S3 bucket access ID|
|CVPEP_AWS_SECRET_ACCESS_KEY|The data deposit S3 bucket secret key|
|CVPEP_DEPOSIT_BUCKET_NAME|The name of the S3 data deposit bucket|
|CVPEP_DEPOSIT_KEY_NAME|The key used for S3 file names.|
|CVPEP_DEPOSIT_TOPIC|The Kafka topic to which the S3 depositor subscribes|
|RDE_AWS_ACCESS_KEY_ID|The data deposit S3 bucket access ID|
|RDE_AWS_SECRET_ACCESS_KEY|The data deposit S3 bucket secret key|
|RDE_DEPOSIT_BUCKET_NAME|The name of the S3 data deposit bucket|
|RDE_DEPOSIT_KEY_NAME|The key used for S3 file names.|
|RDE_DEPOSIT_TOPIC|The Kafka topic to which the S3 depositor subscribes|
|ODE_DDS_CAS_USERNAME|The username for authenticating the USDOT Situation Data Warehouse WebSocket server |
|ODE_DDS_CAS_PASSWORD|The password for authenticating the USDOT Situation Data Warehouse WebSocket server |
|ODE_EXTERNAL_IPV4|The IPv4 address of the server running ODE |
|ODE_EXTERNAL_IPV6|The IPv6 address of the server running ODE |

To be able to change the configuration of the application during runtime, you may store the configuration files in the location specified by the DOCKER_SHARED_VOLUME/config environment variable.

#### Build Process

The ODE application uses Maven to manage builds.

**Step 1**: Build the private repository artifacts consisting of J2735 ASN.1 Java API and IEEE1609.2 ASN.1 Java API

Navigate to the root directory of the `jpo-ode-private` project:

```bash
 cd jpo-ode-private/
 mvn clean install
```

**Step 2**: Build the public 1609.2 Security Library
```bash
cd jpo-security
mvn clean install -DskipTests
```

**Step 3**: Build the S3 Bucket Depositor Service

Note - if you do not intend on using this feature, edit the docker-compose.yml file and comment out (add a `#` to) the lines including and below `s3dep:`.

Navigate to the root directory of the `jpo-s3-depositor` project:

```bash
mvn clean compile assembly:single install
```

**Step 4** (Optional)
Familiarize yourself with Docker and follow the instructions in the [README.md](docker/README.md).

If you wish to change the application properties, such as change the location of the upload service via `ode.uploadLocation.*` properties or set the `ode.kafkaBrokers` to something other than the $DOCKER_HOST_IP:9092, or wish to set the CAS username/password, `ODE_EXTERNAL_IPVs`, etc. instead of setting the environment variables, modify `jpo-ode-svcs\src\main\resources\application.properties` file as desired.

**Step 5**: Navigate to the root directory of the jpo-ode project.

**Step 6**: Build and deploy the application. 

The easiest way to do this is to run the ```clean-build-and-deploy``` script. 
This script executes the following commands:

```
#!/bin/bash
docker-compose stop
docker-compose rm -f -v
mvn clean install
docker-compose up --build -d
docker-compose ps
```

For other build options, see the next section. Otherwise, move on to section [V. Testing ODE Application](#testing)

[Back to top](#toc)

---
### Other Build/Deploy Options

#### Building ODE without Deploying
To build the ODE docker container images but not deploy it, run the following commands:

```
 cd jpo-ode (or cd ../jpo-ode if you are in any sub-directory)
 mvn clean install
 docker-compose rm -f -v
 docker-compose build
```

Alternatively, you may run the ```clean-build``` script.

#### Deploying ODE Application on a Docker Host
To deploy the the application on the docker host configured in your DOCKER_HOST_IP machine, run the following:

```bash
docker-compose up --no-recreate -d
```

**NOTE**: It's important to run ```docker-compose up``` with ```no-recreate``` option. Otherwise you may run into [this issue] (https://github.com/wurstmeister/kafka-docker/issues/100).

Alternatively, run ```deploy``` script.

Check the deployment by running ```docker-compose ps```. You can start and stop containers using ```docker-compose start``` and ```docker-compose stop``` commands.
If using the multi-broker docker-compose file, you can change the scaling by running ```docker-compose scale <container>=n``` where container is the container you would like to scale and n is the number of instances. For example, ```docker-compose scale kafka=3```.

#### Running ODE Application on localhost
You can run the application on your local machine while other services are deployed on a host environment. To do so, run the following:
```bash
 docker-compose start zookeeper kafka
 java -jar jpo-ode-svcs/target/jpo-ode-svcs-0.0.1-SNAPSHOT.jar
```

[Back to top](#toc)

<a name="testing"/>

## V. Testing ODE Application
Once the ODE is running, you should be able to access the jpo-ode web UI at `localhost:8080`.

1. Press the `Connect` button to connect to the ODE WebSocket service.
2. Press `Choose File` button to select a file with J2735 BSM or MessageFrame records in ASN.1 UPER encoding
3. Press `Upload` button to upload the file to ODE.

Upload a file containing BSM messages or J2735 MessageFrame in ASN.1 UPER encoded binary format. For example, try the file [data/bsm.uper](data/bsm.uper) or [data/messageFrame.uper](data/messageFrame.uper) and observe the decoded messages returned to the web UI page while connected tot he WebSocket interface.

Alternatively, you may upload a file containing BSM messages in ASN.1 UPER encoded hexadecimal format. For example, a file containing the following pure BSM record and a file extension of `.hex` or  `.txt` would be processed and decoded by the ODE and results returned to the web UI page:
```text
401480CA4000000000000000000000000000000000000000000000000000000000000000F800D9EFFFB7FFF00000000000000000000000000000000000000000000000000000001FE07000000000000000000000000000000000001FF0
```
*Note: Hexadecimal file format is for test purposes only. ODE is not expected to receive ASN.1 data records in hexadecimal format from the field devices.*

Another way data can be uploaded to the ODE is through copying the file to the location specified by the `ode.uploadLocationRoot/ode.uploadLocationBsm` or `ode.uploadLocationRoot/ode.uploadLocationMessageFrame` property. If not specified,  Default locations would be `uploads/bsm` and `uploads/messageframe` sub-directories off of the location where ODE is launched.

The result of uploading and decoding of the message will be displayed on the UI screen.

![ODE UI](images/ode-ui.png)

*Notice that the empty fields in the J2735 message are represented by a `null` value. Also note that ODE output strips the MessageFrame header and returns a pure BSM in the J2735 BSM subscription topic.*

### PPM Module (Geofencing and Filtering)

To run the ODE with PPM module, you must install and start the PPM service. PPM service communicates with other services through Kafka Topics. PPM will read from the specified "Raw BSM" topic and publish the result to the specified "Filtered Bsm" topic. These topic names are specified by the following ODE and PPM properties:

 - ODE properties for communications with PPM (set in application.properties)
	 - ode.kafkaTopicBsmRawJson  (default = j2735BsmRawJson)
	 - ode.kafkaTopicBsmFilteredJson (default = j2735BsmFilteredJson)
 - PPM properties for communications with ODE (set in yourconfig.properties)
	 - privacy.topic.consumer (default = j2735BsmRawJson)
	 - privacy.topic.producer (default = j2735BsmFilteredJson)
 
Follow the instructions [here](https://github.com/usdot-jpo-ode/jpo-cvdp/blob/master/docs/installation.md) (https://github.com/usdot-jpo-ode/jpo-cvdp/blob/master/docs/installation.md) to install and build the PPM service. 

During the build process, edit the sample config file located in `config/example.properties` and point the property `metadata.broker.list` towards the host of your docker machine or wherever the kafka brokers are hosted. You may use the command `docker-machine ls` to find the kafka service.

After a successful build, use the following commands to configure and run the PPM

```
cd $BASE_PPM_DIR/jpo-cvdp/build
$ ./bsmjson_privacy -c ../config/ppm.properties
```
With the PPM module running, all filtered BSMs that are uploaded through the web interface will be captured and processed. You will see an output of both submitted BSM and processed data unless the entire record was filtered out.

![PPM](images/PPM.png)


[Back to top](#toc)

<a name="ode-limitation"/>

## VI. ODE Limitations

Date: 07/2017

In its current state, the ODE has been developed to accomplish the goals of data transfer, security, and modularity working with the J2735 and 1609.2 security. The system has been designed to support multiple services orchestrated through the Apache Kafka streaming data pipelines, services built and supported as separate applications and described with each service's repository. As a modular system, each component has been built for functionality first, and additional performance testing is needed to understand the limits of the system with large volumes of data.

<a name="dev-tools"/>

## VII. Development Tools

### Integrated Development Environment (IDE)

Install the IDE of your choice:

* Eclipse: [https://eclipse.org/](https://eclipse.org/)
* STS: [https://spring.io/tools/sts/all](https://spring.io/tools/sts/all)
* IntelliJ: [https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)

### Continuous Integration and Delivery

To be added.

### Continous Deployment

To be added.

<a name="contribution-info"/>

## VII. Contribution Information

Please read our [contributing guide](docs/contributing_guide.md) to learn about our development process, how to propose pull requests and improvements, and how to build and test your changes to this project. 


[Back to top](#toc)
