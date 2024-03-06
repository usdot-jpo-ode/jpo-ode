JPO-ODE Release Notes
----------------------------

Version 2.0.1, released March 2024
----------------------------------------
### **Summary**
The updates for the jpo-ode 2.0.1 release includes an update to the commit references for the asn1_codec, jpo-cvdp, jpo-s3-deposit, jpo-sdw-depositor & jpo-security-svcs git submodules.

Enhancements in this release:
- Compatible versions of submodules are now referenced in the jpo-ode project.

Known Issues:
- No known issues at this time.

Version 2.0.0, released February 2024
----------------------------------------
### **Summary**
The updates for the jpo-ode 2.0.0 release includes an update for java, switching over to targeting J2735 2020 and some data flow diagram updates.

Enhancements in this release:
- CDOT PR 50: Updated Java to version 21.
- CDOT PR 51: Updated the ODE to target J2735 2020.
- CDOT PR 52: Updated the data flow diagrams to reflect offloaded file path
- CDOT PR 53: Removed DOCKER_SHARED_VOLUME_WINDOWS environment variable

Known Issues:
- No known issues at this time.

Breaking Changes:
- Users should note that due to the switch to J2735 2020, some fields in outputted TIMs will be different. Any programs relying on data from the ODE must be modified to accommodate these updated outputs. The TIM changes in J2735 2020 include the following field renamings:
    - `sspTimRights` -> `notUsed`
    - `sspLocationRights` -> `notUsed1`
    - `sspMsgRights1` -> `notUsed2`
    - `sspMsgRights2` -> `notUsed3`
    - `duratonTime` -> `durationTime`


Version 1.5.1, released November 2023
----------------------------------------

### **Summary**
The updates for the jpo-ode 1.5.1 release includes a hotfix for nodes getting duplicated during JSON serialization/deserialization.
- Moved JsonProperty declaration to getters to avoid duplicate fetch in serialization/deserialization scenarios

Known Issues:
- No known issues at this time.


Version 1.5.0, released November 2023
----------------------------------------

### **Summary**
The updates for the jpo-ode 1.5.0 release include CI configuration fixes, README updates and updated submodule references.
- Fixed CI configuration command parameters.
- Added README note for Windows users on the shared volume environment variable.
- Updated submodule references
- The supported operating systems in the README have been updated to more recent versions.
- Updated all references to the test package from org.junit.Test to org.junit.jupiter.api.Test. Some tests are also updated to remove deprecated annotations.
- Introduced changes that enable the ODE to establish communication with RSUs using both the NTCIP1218 protocol and the 4.1 DSRC protocol.

Known Issues:
- No known issues at this time.


Version 1.4.0, released July 5th 2023
----------------------------------------

### **Summary**
The updates for the jpo-ode 1.4.0 include unit testing, logging & CI/CD changes.

Enhancements in this release:
- Added a unit test for depositing a tim with extra properties.
- Updated `dockerhub.yml
- CI/CD/Sonar updates
- Updated README with UDP Demo steps.
- Updated data flow diagrams/descriptions to include GeoJSON Converter & Conflict Monitor modules.
- Added a link to the data flow diagrams README in the main README.
- Updated TMC environment diagram.
- Updated README with sonar cloud token configuration.

Fixes in this release:
- Fixed input to invalid syntax test not being invalid.
- Reviewed specified log levels for each log statements and adjusted as needed.
- Bumped json from 20210307 to 20230227.

Known Issues:
- No known issues at this time.

Version 1.3.0, released Mar 30th 2023
----------------------------------------

### **Summary**
The updates for jpo-ode 1.3.0 release includes enhancements such as support for Confluent Cloud, better debugging with the C++ submodules, new JSON output schemas, and improvements for all J2735 message type ser/des. Along with the enhancements below, several bug fixes and CI related enhancements are included in this release.

Enhancements in this release:
-	Issue 466: Removes duplicated J2735 payload data due to Jackson serialization default behavior. Adds annotations to payload classes. Resolves BSM coreData support issues.
-	Issue 468: Resolves TIM ingestion issues created by the previous removal of the gson libraries.
-	Issue 470: Adds unit conversions for MAP and SRM messages for Position3D, heading and speed. This ensures they are compliant with the rest of the jpo-ode conversions.
-	Issue 471: Updates logging calls to reduce logging at the info level.
-	Issue 472: Resolves bug with J2735TransmissionAndSpeedBuilder values being populated incorrectly. Resolves bug for BSM crumbData for unknown values.
-	Issue 474: Provides JSON output schemas for the jpo-ode message types.
-	PR 477: Adds Confluent Cloud support by supporting SASL authenticated Kafka brokers.
-	PR 478: Bumps kafka-clients from 0.10.1.0 to 0.10.2.2
	PR 482: Adds VSCode support for debugging C++ submodules within the jpo-ode project.
-	Issue 485: Updates the jpo-ode SPaT output to better support the J2735 established enum values.
-	Issue 491: Updates schemas to reflect current message output structure of the jpo-ode message types. Removes null values from jpo-ode output messages.
-	Issue 493: Fixes SRM bug for properly deserializing multiple requests in a single SRM message and handling zero requests.
-	PR 494: Bumps logback-core from 1.2.8 to 1.2.9
-	PR 497: Updates base image to eclipse-temurin:11-jre-alpine rather than the deprecated openjdk:8-jre-alpine.
-	PR 498: Adds the GitHub actions workflows for both docker hub build and to run sonar scan analysis for publishing static reports on sonar cloud dashboard.
-	PR 500: Modifies TimDepositController class to allow unknown properties when converting a JSON object to a POJO, resolving an issue where TIMs with additional RSU properties were causing process failures.

Known Issues:
-	One of the tests for the TimDepositController class is failing due to invalid test input. Specifically, the test expects malformed json but is currently being given valid json.
-	The lane type in map messages isn’t handled correctly at this time.


Version 1.2.4, released Dec 17th 2021
----------------------------------------

### **Summary**
The updates for jpo-ode 1.2.4 release includes such as fixes for hex values for CDOT WYDOT projects, Included few unit test in code base and removed Gson & Log4j library in dependencies files. Along with the below enhancements, several bug fixes and CI related enhancements are included in this release.

Enhancements in this release:
- Issue 456: Fixed Hex values to be incorrectly converted to numeric values for both CDOT and WYDOT projects and made few updates to the development environment to accommodate the Java 11 migration.
- Issue 457: Updated few unit tests that are not included in existing code and fixed all failing unit tests in new included tests that should run under mvn test.
- Issue 460: Removed few Gson references and updates the one remaining file OdeTravelerInformationMessage.java which still had Gson references.
- Issue 462: Removed Log4J library within the ODE. The reference to this library has been updated to a patched version.

Version 1.2.3., released Nov 16th, 2021
----------------------------------------

### **Summary**
The updates for jpo-ode 1.2.3 release includes updates to ODE Data router to consume and recognize MAP messages type. Added Kubernetes documentation to run ODE on K8’s. Added UDP receiver class for SPAT messages. Added MAP UDP receiver to receive MAP payload directly from RSU’s. Updated DecodeDataRouter to consume and recognize SRM and SSM messages and Along with the below enhancements, several bug fixes and CI related enhancements are included in this release.

Enhancements in this release:
- Issue 446: Added a new markdown file containing Kubernetes documentation related to running the ODE in a k8s environment. This markdown file has been linked in the README https://github.com/usdot-jpo-ode/jpo-ode#12-kubernetes.
- Issue 442: Updated DecodeDataRouter to consume and recognize MAP message. Added classes and functionalities to build MAP object and populate it with the decoded MAP messages. Serialize the MAP object into JSON and publish the JSON data to Kafka topics within ODE.
- Issue 447: Added UDP receiver class for SPAT messages. This allows to receive SPAT payload directly from RSU and forward to Kafka topics within ODE.
- Issue 449: MAP UDP receiver has been added. This allows to receive MAP payload directly from RSU and forward to Kafka topics within ODE.
- Issue 451: SSM message types are now supported (including UDP receiver). Updated DecodeDataRouter to consume and recognize SSM message. Added classes and functionalities to build SSM object and populate it with the decoded SSM messages. Serialize the SSM object into JSON and publish the JSON data to Kafka topics within ODE.
- Issue 438: SRM message types are now supported (including UDP receiver). Updated DecodeDataRouter to consume and recognize SRM message. Added classes and functionalities to build SRM object and populate it with the decoded SRM messages. Serialize the SRM object into JSON and publish the JSON data to Kafka topics within ODE.
- Issue 454: Fixed the unit test case failure which caused Circle CI build failure.


Version 1.2.2., released Aug 6th, 2021
----------------------------------------

### **Summary**
The updates for jpo-ode 1.2.2 release includes updates to ODE data router to support Spat messages type. Added S3 depositor group name. Added source IP’s to Metadata for BSM and TIM. Updated the Java versions and Sprint Boot Framework Along with the below enhancements, several bug fixes and CI related enhancements are included in this release.

Enhancements in this release:
- Issue 438: Added source IP to metadata in BSM and TIM messages received over UDP to their respective ports.
- Issue 441: Updated Java version 8 to 11 and updated Sprint Boot Framework version.
- Issue 435: Updated new code to Implement ODE data router to support Spat messages type and added additional support for SPaT data forwarding like BSM, TIM.

Fixes in this release:
- Issue 443: Fixed Log File Parser is unable to parse SPaT log files sent from V2XHub
- Issue 432: Fixed Replace single Kafka topic consumer with multiple message type specific consumers by three different topics for receiving encoded BSM, encoded TIM, and encoded SPAT.
- Issue 436: Fixed S3 depositor group name to be configurable rather than a static value which fixes the issues when running multiple containers as Kafka.

Version 1.2.1., released March 9th, 2021
----------------------------------------

### **Summary**
The updates for jpo-ode 1.2.1 release includes updates to ODE to accept BSM and TIM messages over UDP. Added Issue and pull request templates. Added standardized development environment. Added TIM, BSM metadata, Along with the below enhancements, several bug fixes and CI related enhancements are included in this release.

Enhancements in this release:
- Issue 398&399: Updated ODE to accept new BSM data format from V2xHub.
- Issue 402: Created a Pull request template Issue template and added code contribution guidelines.
- Issue 403&401: Added changes to jpo ode svcs which BSM messages are automatically forwarded to it from an RSU over UDP Receiving from port.
- Issue 412&413: Added changes to jpo ode svcs which TIM messages are automatically forwarded to it from an RSU over UDP Receiving from port.
- Issue 407&408: Added standardized development environment. This will include standard development tools such as IDE, as well as standard procedure for running the code and debugging the ODE.
- Issue 415: Updated ODE accept and decode log messages from V2X Hub (Spat) JSON and publish on Kafka topics available for cloud or database consumers.
- Issue 420: Updated ODE accept and decode log messages from V2X Hub (Spat) Binary and publish on Kafka topics available for cloud or database consumers.
- Issue 424: Added a TIM metadata class for the decoding of TIM messages through the Asn1DecodeMessageJSON class.

Fixes in this release:
- Issue 400: Fixed sonar errors: Parameter sonar.pullrequest.branch is mandatory for a pull request analysis.
- Issue 405&406: Fixed Docker.md file to download Docker for mac where Windows Docker download was included twice.
- Issue 397&409&410: Fixed Unrestricted Log File Growth and Docker compose file errors which causing a problem with disk space consumption.
- Issue 416: Fixed variable name change in the 'Asn1DecodeMessageJSON.java' class which causes build error.
- Issue 427: Fixed Jpo-ode upload folder path.


Version 1.2.0., released Oct 30th, 2020
----------------------------------------

### **Summary**
This update for the ODE includes the ability to support the ISS HSM sigValidityOverride parameter to change the default TIM SCMS signature expiration time from the default 4 hours to a programmed time that matches the TIM MAP expiration. This update also provides feedback for the signature expiration (as it could be less than the requested time due to the HSM certificate expiration time). See issue 370 for details. This should also work for MAP messages, but was not tested.
This update also incorporates a fix to support for relative positions for TIM messages. See issue 372 for details.

Version 1.1.0., released Nov 8th, 2019
----------------------------------------

### **Summary**
The New Ode version 1.1.0 includes below updates: 
    1. SNMP Psid P-Encoding 
    2. ODE processing BSM logs and dropping critical data (acceleration)
    3. Fix for SNMP Deposit Response Code #5

Version 1.0.8., released March 24th, 2019
----------------------------------------

### **Summary**
The New Ode version 1.0.8 includes below updates: 
    1. Integrated odevalidator v0.0.6
    2. Updated test-harness
    3. Changed SNMP deposit timestamp format from MM/DD/YYYY to YYYY/MM/DD
   

