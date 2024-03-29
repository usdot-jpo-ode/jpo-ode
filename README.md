| Travis Build Status | Sonar Code Quality | Sonar Code Coverage |
|---------------------|---------------------|---------------------|
 [![Build Status](https://travis-ci.org/usdot-jpo-ode/jpo-ode.svg?branch=master)](https://travis-ci.org/usdot-jpo-ode/jpo-ode) | [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=usdot.jpo.ode%3Ajpo-ode&metric=alert_status)](https://sonarcloud.io/dashboard?id=usdot.jpo.ode%3Ajpo-ode) | [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=usdot.jpo.ode%3Ajpo-ode&metric=coverage)](https://sonarcloud.io/dashboard?id=usdot.jpo.ode%3Ajpo-ode) |

# jpo-ode

**US Department of Transportation (USDOT) Intelligent Transportation Systems (ITS) Joint Program Office (JPO) Operational Data Environment (ODE)**

The ITS ODE is a real-time virtual data router that ingests and processes operational data from various connected devices - including vehicles, infrastructure, and traffic management centers - and distributes it to other devices and subscribing transportation management applications. Using the ITS ODE within intelligent transportation deployments increases data fluidity and interoperability while meeting operational needs and protecting user privacy. The software’s microservices architecture makes it easy to add new capabilities to meet local needs. Check the ITS factsheet for more information: <https://www.its.dot.gov/factsheets/pdf/ITSJPO_ODE.pdf>.

![Figure 1: ODE Dataflows](docs/images/readme/figure1.png)

_Figure 1: ODE Dataflows_

**Documentation:**

1. [ODE Architecture](docs/Architecture.md)
2. [ODE User Guide](docs/UserGuide.md)
3. [ODE Output Schema Reference Guide](docs/ODE_Output_Schema_Reference.docx)
4. [ODE REST API Guide](https://usdot-jpo-ode.github.io/)
5. [ODE Smoke Tests](https://github.com/usdot-jpo-ode/jpo-ode/wiki/JPO-ODE-QA-Documents)

All stakeholders are invited to provide input to these documents. To provide feedback, we recommend that you create an "issue" in this repository (<https://github.com/usdot-jpo-ode/jpo-ode/issues>). You will need a GitHub account to create an issue. If you don’t have an account, a dialog will be presented to you to create one at no cost.

---

<a name="toc"/>

## Release Notes
The current version and release history of the JPO-ODE: [ODE Release Notes](<docs/Release_notes.md>)

## Table of Contents

1.  [Usage Example](#usage-example)
2.  [Configuration](#configuration)
3.  [Installation](#installation)
4.  [File Manifest](#file-manifest)
5.  [Development Setup](#development-setup)
6.  [Release History](#release-history)
7.  [Contact Information](#contact-information)
8.  [Contributing](#contributing)
9.  [Known Bugs](#known-bugs)
10. [Credits and Acknowledgement](#credits-and-acknowledgement)
11. [Code.gov Registration Info](#codegov-registration-info)
12. [Kubernetes](#kubernetes)
13. [Sonar Cloud](#sonar-token-configuration) ([Documentation](https://sonarcloud.io/documentation/user-guide/user-token/))
14. [SNMP](#snmp)

<!--
#########################################
############# Usage Example #############
#########################################
 -->

<a name="usage-example"/>

## 1. Usage Example

Once the ODE is deployed and running locally, you may access the ODE's demonstration console by opening your browser and navigating to  `http://localhost:8080`. This portal can only be used to decode a subset of the supported message types: BSM and TIM messages. To decode messages such as MAP, SPaT, SRM and SSM, the ODE UDP ports must be utilized. The following sections will describe how to utilize both methods.

<b>For testing BSM and TIM decoding only:</b>
1.  Press the `Connect` button to connect to the ODE WebSocket service.
2.  Press `Select File` button to select an OBU log file containing BSMs and/or TIM messages as specified by the WYDOT CV Pilot project. See below documents for details:
    - [Wyoming CV Pilot Log File Design](data/Wyoming_CV_Pilot_Log_File_Design.docx)
    - [WYDOT Log Records](data/wydotLogRecords.h)
3.  Press `Upload` button to upload the file to ODE.

Upload records within the files can be embedding BSM, MAP and/or TIM messages wrapped in J2735 MessageFrame and ASN.1 UPER encoded, wrapped in IEEE 1609.2 envelope and ASN.1 COER encoded binary format. Log processing of files that contain messages with WSMP headers within the ASN.1 UPER encoded messages is supported but the header will be removed before processing. Please review the files in the [data](data) folder for samples of each supported type. By uploading a valid data file, you will be able to observe the decoded messages contained within the file appear in the web UI page while connected to the WebSocket interface.

Another way data can be uploaded to the ODE is through copying the file to the location specified by the `ode.uploadLocationRoot/ode.uploadLocationObuLog`property. If not specified,  Default locations would be `uploads/bsmlog`sub-directory off of the location where ODE is launched.

The result of uploading and decoding of messages will be displayed on the UI screen.

![ODE UI](docs/images/readme/figure2.png)

_Figure 2: ODE UI demonstrating message subscription_

Notice that the empty fields in the J2735 message are represented by a `null` value. Also note that ODE output strips the MessageFrame header and returns a pure BSM or TIM in the subscription topic.

With the PPM module running, all filtered BSMs that are uploaded through the web interface will be captured and processed. You will see an output of both submitted BSM and processed data unless the entire record was filtered out.

<b>For testing decoding with all supported ODE message types:</b>

To test decoding all supported ODE messages, the UDP endpoints must be utilized. These endpoints specifically take hex ASN.1 UPER encoded message data. These messages are allowed to have headers but do not need to. The headers are trimmed out currently once the UDP receiver receives the message so this data will not affect the output.

Supported message types:
- BSM
- TIM
- MAP
- SPaT
- SRM
- SSM
- PSM

1. Navigate to the [UDP sender Python scripts](<./scripts/tests/>) in the project.
2. Ensure the environment variable "DOCKER_HOST_IP" has been set in the shell that will be running the script. This must be set to the same IP that the ODE deployments are using.
3. Run the script of your choosing: `python3 ./scripts/tests/udpsender_spat.py`
4. View the output Kafka messages from kafkacat: `kafkacat -b $DOCKER_HOST_IP:9092 -G udp_test_group topic.OdeSpatJson -f '\nTopic: %t, Key: %k, Offset: %o, Timestamp: %T\nValue: %s\n'`
5. The script should continue to send the same message every 5 seconds and kafkacat should continue to output the latest message.
6. You may modify the content of the UDP sender scripts to test different payloads of your own.

<p align="center">
  <img src="./docs/images/readme/figure3.png" width="80%" height="50%">
</p>

[Back to top](#toc)

<!--
#########################################
############# Configuration #############
#########################################
 -->

<a name="configuration"/>

## 2. Configuration

### System Requirements

-  Minimum RAM: 16 GB
-  Minimum storage space: 100 GB
-  Supported operating systems:
   -  Ubuntu 22.04 Linux (Recommended)
   -  Windows 10/11 Professional (Professional version required for Docker virtualization)
   -  OSX 13

The ODE software can run on most standard Window, Mac, or Linux based computers with
Pentium core processors. Performance of the software will be based on the computing power and available RAM in
the system.  Larger data flows can require much larger space requirements depending on the
amount of data being processed by the software. The ODE software application was developed using the open source programming language Java. If running the ODE outside of Docker, the application requires the Java 8 runtime environment.

### Software Prerequisites

The ODE is bundled as a series of submodules running in Docker containers and managed by Docker-Compose. All other required dependencies will automatically be downloaded and installed as part of the Docker build process.

- Docker: <https://docs.docker.com/engine/installation/>
- Docker-Compose: <https://docs.docker.com/compose/install/>

### Tips and Advice

Read the following guides to familiarize yourself with ODE's Docker and Kafka modules.

- [Docker README](docker.md)
- [Kafka README](kafka.md)

The following guide contains information about the data flow diagrams for the ODE.

- [Data Flow Diagrams README](docs/data-flow-diagrams/README.md)

**Installation and Deployment:**

- Docker builds may fail if you are on a corporate network due to DNS resolution errors.
[See here](https://github.com/usdot-jpo-ode/jpo-ode/wiki/Docker-fix-for-SSL-issues-due-to-corporate-network) for instructions to fix this.
- Additionally `git` commands may fail for similar reasons, you can fix this by running `export GIT_SSL_NO_VERIFY=1`.
- Windows users may find more information on installing and using Docker [here](https://github.com/usdot-jpo-ode/jpo-ode/wiki/Docker-management).
- Users interested in Kafka may find more guidance and configuration options [here](docker/kafka/README.md).
- A compatibility guide containing recommendations for suitable versions of submodules for each main module version can be found [here](docs/compatibility.md).

**Configuration:**

If you wish to change the application properties, such as change the location of the upload service via `ode.uploadLocation.*` properties or set the `ode.kafkaBrokers` to something other than the `$DOCKER_HOST_IP:9092`, or wish to change the log file upload folder, etc. instead of setting the environment variables, modify `jpo-ode-svcs\src\main\resources\application.properties` file as desired.

ODE configuration can be customized for every deployment environment using environment variables. These variables can either be set locally or using the [sample.env](sample.env) file. Instructions for how to use this file can be found [here](https://github.com/usdot-jpo-ode/jpo-ode/wiki/Using-the-.env-configuration-file).

**Important!**
You must rename `sample.env` to `.env` for Docker to automatically read the file. This file will contain AWS access keys and other private information. Do not push this file to source control.

[Back to top](#toc)

<!--
########################################
############# Installation #############
########################################
 -->

<a name="installation"/>

## 3. Installation

The following instructions describe the minimal procedure to fetch, build, and run the main ODE application. If you want to use the privacy protection module and/or S3 depositors, see the [User Guide](docs/UserGuide.md) for more detailed information. Additionally, different build processes are covered at the bottom of this section.

#### Step 0 - For Windows Users Only

If running on Windows, please make sure that your global git config is set up to not convert end-of-line characters during checkout.

Disable `git core.autocrlf` (One Time Only)

```bash
git config --global core.autocrlf false
```

#### Step 1 - Download the Source Code

The ODE software system consists of the following modules hosted in separate Github repositories:

|Name|Visibility|Description|
|----|----------|-----------|
|[jpo-ode](https://github.com/usdot-jpo-ode/jpo-ode)|public|Contains the public components of the application code.|
|[jpo-cvdp](https://github.com/usdot-jpo-ode/jpo-cvdp)|public|Privacy Protection Module|
|[jpo-s3-deposit](https://github.com/usdot-jpo-ode/jpo-s3-deposit)|public|S3 depositor service. Optional, comment out of `docker-compose.yml` file if not used.|
|[asn1_codec](https://github.com/usdot-jpo-ode/asn1_codec)|public|ASN.1 Encoder/Decoder module|
|[jpo-security-svcs](https://github.com/usdot-jpo-ode/jpo-security-svcs)|public|Provides cryptographic services.|
|[jpo-sdw-depositor](https://github.com/usdot-jpo-ode/jpo-sdw-depositor)|public|SDW depositor service. Optional, comment out of `docker-compose.yml` file if not used.|

You may download the stable, default branch for ALL of these dependencies by using the following recursive git clone command:

```bash
git clone --recurse-submodules https://github.com/usdot-jpo-ode/jpo-ode.git
```

Once you have these repositories obtained, you are ready to build and deploy the application.

##### Downloading the source code from a non-default branch

<details><summary>(Advanced) Downloading the source code from a non-default branch</summary>
<p>

The above steps to pull the code from GitHub repository pulls it from the default branch which is the stable branch. If you wish to pull the source code from a branch that is still under development or beta testing, you will need to specify the branch to pull from. The following commands aid you in that action.

**Note**: These commands can also be performed using the provided script `update_branch`.

```bash
# Backup user provided source or configuration files used by submodules
cp asn1_codec/asn1c_combined/J2735_201603DA.ASN .

# Run the following commands to reset existing branch
git reset --hard
git submodule foreach --recursive git reset --hard

# Pull from the non-default branch
git checkout <branch_name>
git pull origin <branch_name>

# The next command wipes out all of the submodules and re-initializes them.
git submodule deinit -f . && git submodule update --recursive --init

# Restore user provided source or configuration files used by submodules
cp ./J2735_201603DA.ASN asn1_codec/asn1c_combined/
```

</p>
</details>

#### Step 2 - Build and run the application

**Notes:**
- Docker builds may fail if you are on a corporate network due to DNS resolution errors.
[See here](https://github.com/usdot-jpo-ode/jpo-ode/wiki/Docker-fix-for-SSL-issues-due-to-corporate-network) for instructions to fix this.
- In order for Docker to automatically read the environment variable file, you must rename it from `sample.env` to `.env`. **This file will contain private keys, do not put add it to version control.**

Copy the following files from `jpo-ode` directory into your DOCKER_SHARED_VOLUME directory.
- Copy jpo-ode/ppm.properties to ${DOCKER_SHARED_VOLUME}/config.properties. Open the newly copied `config.properties` file in a text editor and update the `metadata.broker.list=your.docker.host.ip:9092` line with your system's DOCKER_HOST_IP in place of the dummy `your.docker.host.ip` string.
- Copy jpo-ode/adm.properties to ${DOCKER_SHARED_VOLUME}/adm.properties
- Copy jpo-ode/aem.properties to ${DOCKER_SHARED_VOLUME}/aem.properties
- Specifying the variable: ${DOCKER_SHARED_VOLUME_WINDOWS} to "C:" may be required for certain Windows users.

Navigate to the root directory of the jpo-ode project and run the following command:

```bash
docker-compose up --build -d
docker-compose ps
```

To bring down the services and remove the running containers run the following command:

```bash
docker-compose down
```
For a fresh restart, run:

```bash
docker-compose down
docker-compose up --build -d
docker-compose ps
```

To completely rebuild from scratch, run:

```bash
docker-compose down
docker-compose rm -fvs
docker-compose up --build -d
docker-compose ps
```

Check the deployment by running `docker-compose ps`. You can start and stop containers using `docker-compose start` and `docker-compose stop` commands.
If using the multi-broker docker-compose file, you can change the scaling by running `docker-compose scale <container>=n` where container is the container you would like to scale and n is the number of instances. For example, `docker-compose scale kafka=3`.


#### asn1_codec Module (ASN.1 Encoder and Decoder)
ODE requires the deployment of asn1_codec module. ODE's `docker-compose.yml` file is set up to build and deploy the module in a Docker container. If you wish to run `asn1_codec` module outside Docker (i.e. directly on the host machine), please refer to the documentation of `asn1_codec` module.

The only requirement for deploying `asn1_codec` module on Docker is the setup of two environment variables `DOCKER_HOST_IP` and `DOCKER_SHARED_VOLUME`.

#### PPM Module (Geofencing and Filtering)

To run the ODE with PPM module, you must install and start the PPM service. PPM service communicates with other services through Kafka Topics. PPM will read from the specified "Raw BSM" topic and publish the result to the specified "Filtered Bsm" topic. These topic names are specified by the following ODE and PPM properties:

 - ODE properties for communications with PPM (set in application.properties)
	 - ode.kafkaTopicOdeBsmJson  (default = topic.OdeBsmJson)
	 - ode.kafkaTopicFilteredOdeBsmJson (default = topic.FilteredOdeBsmJson)
 - PPM properties for communications with ODE (set in yourconfig.properties)
	 - privacy.topic.consumer (default = j2735BsmRawJson)
	 - privacy.topic.producer (default = j2735BsmFilteredJson)

Follow the instructions [here](https://github.com/usdot-jpo-ode/jpo-cvdp/blob/master/docs/installation.md) to install and build the PPM service.

During the build process, edit the sample config file located in `config/example.properties` and point the property `metadata.broker.list` towards the host of your docker machine or wherever the kafka brokers are hosted. You may use the command `docker-machine ls` to find the kafka service.

After a successful build, use the following commands to configure and run the PPM

```
cd $BASE_PPM_DIR/jpo-cvdp/build
$ ./bsmjson_privacy -c ../config/ppm.properties
```

# Confluent Cloud Integration

Rather than using a local kafka instance, the ODE can utilize an instance of kafka hosted by Confluent Cloud via SASL.



## Environment variables

### Purpose & Usage

- The DOCKER_HOST_IP environment variable is used to communicate with the bootstrap server that the instance of Kafka is running on.

- The KAFKA_TYPE environment variable specifies what type of kafka connection will be attempted and is used to check if Confluent should be utilized. If this environment variable is not set, the ODE will default to normal behavior.

- The CONFLUENT_KEY and CONFLUENT_SECRET environment variables are used to authenticate with the bootstrap server. If the KAFKA_TYPE environment variable is not set, then these are not required.



### Values
In order to utilize Confluent Cloud:

- DOCKER_HOST_IP must be set to the bootstrap server address (excluding the port)

- KAFKA_TYPE must be set to "CONFLUENT"

- CONFLUENT_KEY must be set to the API key being utilized for CC

- CONFLUENT_SECRET must be set to the API secret being utilized for CC



## CC Docker Compose File

There is a provided docker-compose file (docker-compose-confluent-cloud.yml) that passes the above environment variables into the container that gets created. Further, this file doesn't spin up a local kafka instance since it is not required.



## Note

This has only been tested with Confluent Cloud but technically all SASL authenticated Kafka brokers can be reached using this method.	

[Back to top](#toc)
	
<!--
#########################################
############# File Manifest #############
#########################################
 -->

<a name="file-manifest"/>

## 4. File Manifest

This section outlines the software technology stacks of the ODE.

### Containerization and Management

- [Docker](https://www.docker.com/)
- [Docker-Compose](https://docs.docker.com/compose/)

### Messaging

- [Kafka](https://kafka.apache.org/)

### Code Quality

- [SonarCloud](https://sonarcloud.io)

### Continuous Integration

- [TravisCI](https://travis-ci.org/)

### ODE Code

- [Java 8](https://openjdk.java.net/)
- [Maven](https://maven.apache.org/)
- [Spring Boot](http://spring.io/projects/spring-boot)
- [Logback](https://logback.qos.ch/)
- [SNMP4J](https://www.snmp4j.org/)
- [JUnit](https://junit.org)
- [JMockit](http://jmockit.github.io/)
- [Stomp Websocket](http://jmesnil.net/stomp-websocket)

### Web UI

- [MaterializeCSS](https://materializecss.com)
- [jQuery](https://jquery.com/)
- [Stomp Websocket](http://jmesnil.net/stomp-websocket)
- [SockJS](https://github.com/sockjs)

[Back to top](#toc)

<!--
#############################################
############# Development Setup #############
#############################################
 -->

<a name="development-setup"/>

## 5. Development Setup

### Integrated Development Environment (IDE)

Install the IDE of your choice:

* Eclipse: [https://eclipse.org/](https://eclipse.org/)
* STS: [https://spring.io/tools/sts/all](https://spring.io/tools/sts/all)
* IntelliJ: [https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)

### Continuous Integration

* TravisCI: <https://travis-ci.org/usdot-jpo-ode/jpo-ode>

### Dev Container Environment
The project can be reopened inside of a dev container in VSCode. This environment should have all of the necessary dependencies to debug the ODE and its submodules. When attempting to run scripts in this environment, it may be necessary to make them executable with "chmod +x" first.

[Back to top](#toc)

<!--
###########################################
############# Release History #############
###########################################
 -->

<a name="release-history"/>

## 6. Release History

[Release Notes](ReleaseNotes.md)

[Back to top](#toc)

<!--
###############################################
############# Contact Information #############
###############################################
 -->

<a name="contact-information"/>

## 7. Contact Information

Contact the developers of the ODE application by submitting a [Github issue](https://github.com/usdot-jpo-ode/jpo-ode/issues).

Contact the ODE management representative using the information in the [Code.gov Registration Info](#codegov-registration-info) section.

### License information

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
file except in compliance with the License.
You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>
Unless required by applicable law or agreed to in writing, software distributed under
the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied. See the License for the specific language governing
permissions and limitations under the [License](http://www.apache.org/licenses/LICENSE-2.0).

[Back to top](#toc)

<!--
########################################
############# Contributing #############
########################################
 -->

<a name="contributing"/>

## 8. Contributing

Please read our [contributing guide](docs/contributing_guide.md) to learn about our development process, how to propose pull requests and improvements, and how to build and test your changes to this project.

### Source Repositories - GitHub

- Main repository on GitHub (public)
	- <https://github.com/usdot-jpo-ode/jpo-ode>
- Data Privacy Module on Github (public)
	- <https://github.com/usdot-jpo-ode/jpo-cvdp>
- S3 Depositor Module on Github (public)
	- <https://github.com/usdot-jpo-ode/jpo-s3-deposit>
- Security services repository on GitHub (public)
  - <https://github.com/usdot-jpo-ode/jpo-security-svcs>
- ODE Output Validatory Library (public)
  - https://github.com/usdot-jpo-ode/ode-output-validator-library

### Agile Project Management - Jira
<https://usdotjpoode.atlassian.net/secure/RapidBoard.jspa?projectKey=ODE>

### Wiki - Confluence
<https://usdotjpoode.atlassian.net/wiki/>

### Continuous Integration and Delivery
<https://travis-ci.org/usdot-jpo-ode/jpo-ode>

<details><summary>Using Travis for your build</summary>


To allow Travis run your build when you push your changes to your public fork of the jpo-ode repository, you must define the following secure environment variable using Travis CLI (<https://github.com/travis-ci/travis.rb>).

Run:

```
travis login --org
```
Enter personal github account credentials.

In order to allow Sonar to run, personal key must be added with this command:
(Key can be obtained from the JPO-ODE development team)

```
travis env set SONAR_SECURITY_TOKEN <key> -pr <user-account>/<repo-name>
```
</details>
<br>

### Static Code Analysis

<https://sonarcloud.io/organizations/usdot-jpo-ode/projects>

[Back to top](#toc)

<!--
######################################
############# Known Bugs #############
######################################
 -->

### Quality Assurance

Code quality assurance is reported through the [usdot-jpo-ode SonarCloud organization](https://sonarcloud.io/organizations/usdot-jpo-ode/projects). Code quality reports are generated by the [JaCoCo plugin for Maven](https://www.eclemma.org/jacoco/trunk/doc/maven.html) during the ODE's [webhook-triggered TravisCI build](https://github.com/usdot-jpo-ode/jpo-ode/blob/dev/.travis.yml#L16). After a successful build, the [SonarQube scanner plugin for Maven](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Maven) creates and uploads a code quality report to SonarCloud.

For regression and user acceptance testing, ODE provides an automated test harness. The test harness is pprovided in the [qa/test-harness](ga/test-harness) directory under jpo-ode root folder. The test harness uses the ODE [Validator Library](https://github.com/usdot-jpo-ode/ode-output-validator-library) repository as a submodule.

For more information, please see: https://github.com/usdot-jpo-ode/jpo-ode/wiki/Using-the-ODE-test-harness

<a name="known-bugs"/>

## 9. Known Bugs

Date: 07/2017

In its current state, the ODE has been developed to accomplish the goals of data transfer, security, and modularity working with the J2735 and 1609.2 security. The system has been designed to support multiple services orchestrated through the Apache Kafka streaming data pipelines, services built and supported as separate applications and described with each service's repository. As a modular system, each component has been built for functionality first, and additional performance testing is needed to understand the limits of the system with large volumes of data.

### Troubleshooting

Please read our [Wiki](https://github.com/usdot-jpo-ode/jpo-ode/wiki) for more information, or check the [User Guide](docs/UserGuide.md).

Application Support for the ODE currently managed via GitHub's native issue tracker: <https://github.com/usdot-jpo-ode/jpo-ode/issues>.

[Back to top](#toc)

<!--
#######################################################
############# Credits and Acknowledgement #############
#######################################################
 -->

<a name="credits-and-acknowledgement"/>

## 10. Credits and Acknowledgement

[Attribution](ATTRIBUTION.md)

[Back to top](#toc)

<!--
######################################################
############# Code.gov Registration Info #############
######################################################
 -->

<a name="codegov-registration-info"/>

## 11. Code.gov Registration Info

Agency: DOT

Short Description: The ITS ODE is a real-time virtual data router that ingests and processes operational data from various connected devices – including vehicles, infrastructure, and traffic management centers – and distributes it to other devices and subscribing transportation management applications. Using the ITS ODE within intelligent transportation deployments increases data fluidity and interoperability while meeting operational needs and protecting user privacy. The software’s microservices architecture makes it easy to add new capabilities to meet local needs.

Status: Beta

Tags: transportation, connected vehicles, intelligent transportation systems, java

Labor hours: 200

Contact Name: James Lieu

Contact Phone: (202) 366-3000

<a name="kubernetes"/>

## 12. Kubernetes
The ODE can be run in a k8s environment.
See [this document](./docs/Kubernetes.md) for more details about this.

[Back to top](#toc)

<a name="sonar-token-configuration"/>

## 13. Sonar Token Configuration
Generating and Using Tokens
Users can generate tokens that can be used to run analyses or invoke web services without access to the user's actual credentials.

USDOT-JPO-ODE SonarCloud Organization : https://sonarcloud.io/organizations/usdot-jpo-ode-1/

### Generating a token
You can generate new tokens at User > My Account > Security.
The form at the bottom of the page allows you to generate new tokens. Once you click the Generate button, you will see the token value. Copy it immediately; once you dismiss the notification you will not be able to retrieve it.

### Using a token
SonarScanners running in GitHub Actions can automatically detect branches and pull requests being built so you don't need to specifically pass them as parameters to the scanner.

**<ins>To analyze your projects with GitHub Actions, you need to: </ins>**

**<ins> Creating your GitHub secrets </ins>**
You can create repository secrets from your GitHub repository as below:

Sonar Token: Generate a SonarQube token and, in GitHub, create a new repository secret in GitHub with SONAR_TOKEN as the Name and the token you generated as the Value.
Sonar Host URL: In GitHub, create a new repository secret with SONAR_HOST_URL as the Name and your SonarQube server URL as the Value.

Configure your workflow YAML file as below:

	1. Add GitHub Secrets in ci.yml workflow as SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }
 	2. Update the sonar properties in Sonar scan step (- name: Run Sonar) with new sonar project properties.
  
Commit and push your code to start the analysis.

### Revoking a token
You can revoke an existing token at User > My Account > Security by clicking the Revoke button next to the token.

<a name="snmp"/>

## 14. SNMP
The ODE is capable of communicating with RSUs to:
- Query TIMs
- Deposit TIMs
- Delete TIMs

The following SNMP protocols are supported for communication with RSUs:
- DSRC 4.1 (defined in 'Dedicated Short-Range Communications Roadside Unit Specifications')
- NTCIP1218 (defined in 'National Transportation Communications for ITS Protocol')

If no protocol is specified in a request containing RSUs, the ODE will communicate with RSUs via the DSRC 4.1 protocol by default. 
This can be changed by setting the value of the DEFAULT_SNMP_PROTOCOL environment variable. 

Additionally, the ODE supports the execution of PDM operations on RSUs. PDM operations are not defined in NTCIP1218, but are defined DSRC 4.1.