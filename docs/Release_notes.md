JPO-ODE Release Notes
----------------------------

Version 4.0.0, released January 2025
----------------------------------------
### **Summary**

The jpo-ode 4.0.0 release brings a variety of updates aimed at enhancing functionality, maintainability, and overall development experience.

Key highlights include:
- The introduction of a generated TMC TIM Topic
- The migration from a custom Kafka client to Spring Kafka for improved Spring integration
- Updates to TIM schemas and JSON annotations for more J2735-accurate ODE processing

Significant strides have been made in optimizing configurations through enhancements like the extraction of Kafka properties,
environment variables, and Kafka topics into more streamlined Spring Configuration properties. Additionally, schema
updates have been made to increment the version for output messages and ensure compatibility with J2735 2024.
Developer-focused changes include revisions to the README, Makefile, and devcontainer to support smoother onboarding and
ease of use. Other vital updates encompass new entries in the submodule compatibility guide, version upgrades, and
bug fixes related to Kafka configurations and message handling.

Enhancements in this release:
- [CDOT PR 100](https://github.com/CDOT-CV/jpo-ode/pull/100): Add Generated TMC TIM Topic
- [CDOT PR 102](https://github.com/CDOT-CV/jpo-ode/pull/102): Remove Sping Dependencies from Maven Jars
- [CDOT PR 105](https://github.com/CDOT-CV/jpo-ode/pull/105): Updates to README, Makefile, and devcontainer to improve onboarding and developer experience
- [CDOT PR 106](https://github.com/CDOT-CV/jpo-ode/pull/106): Add configurable alert to print log statement warning of missing TIMs deposits
- [CDOT PR 107](https://github.com/CDOT-CV/jpo-ode/pull/107): Submodule Compatibility Guide Entry for ODE 3.0.0
- [CDOT PR 108](https://github.com/CDOT-CV/jpo-ode/pull/108): Update Release Documentation to Include jpo-utils Submodule
- [CDOT PR 109](https://github.com/CDOT-CV/jpo-ode/pull/109): Update TIM schema
- [CDOT PR 110](https://github.com/CDOT-CV/jpo-ode/pull/110): Extract Kafka properties from OdeProperties as a first step to breaking apart the monstrosity
- [CDOT PR 111](https://github.com/CDOT-CV/jpo-ode/pull/111): Adding Compression to Ode
- [CDOT PR 112](https://github.com/CDOT-CV/jpo-ode/pull/112): Extract Kafka Topics and related configurations into Spring Configuration properties
- [CDOT PR 113](https://github.com/CDOT-CV/jpo-ode/pull/113): Extract environment variables from OdeProperties into Spring Configuration objects
- [CDOT PR 114](https://github.com/CDOT-CV/jpo-ode/pull/114): Fixed JSON annotations to enable ODE processing of circle geometries
- [CDOT PR 115](https://github.com/CDOT-CV/jpo-ode/pull/115): Update the TIM schema test
- [CDOT PR 116](https://github.com/CDOT-CV/jpo-ode/pull/116): Add Approval Tests for MAP Data Flows
- [CDOT PR 117](https://github.com/CDOT-CV/jpo-ode/pull/117): SSM Processing Fix
- [CDOT PR 118](https://github.com/CDOT-CV/jpo-ode/pull/118): Spring Kafka Proof-of-Concept - Implemented in MAP Data Flow
- [CDOT PR 119](https://github.com/CDOT-CV/jpo-ode/pull/119): OdeTimJson POJO Processing Rework
- [CDOT PR 120](https://github.com/CDOT-CV/jpo-ode/pull/120): Introduce Checkstyle Linter to GH pipeline and provide steps for local configuration
- [CDOT PR 121](https://github.com/CDOT-CV/jpo-ode/pull/121): Switching adopt builder to temurin
- [CDOT PR 122](https://github.com/CDOT-CV/jpo-ode/pull/122): J2735 2024 Compatibility and Enhanced TIM Format Handling
- [CDOT PR 123](https://github.com/CDOT-CV/jpo-ode/pull/123): add missing confluent.password and confluent.username to application.yaml
- [CDOT PR 129](https://github.com/CDOT-CV/jpo-ode/pull/129): Migrate UDPRecievers to use Spring Kafka
- [CDOT PR 130](https://github.com/CDOT-CV/jpo-ode/pull/130): BugFix: use correct config value for setting up producers with Confluent Properties
- [CDOT PR 131](https://github.com/CDOT-CV/jpo-ode/pull/131): Migrate Asn1DecodedDataRouter to use Spring Kafka
- [CDOT PR 132](https://github.com/CDOT-CV/jpo-ode/pull/132): Update POM Version 4.0.0
- [CDOT PR 134](https://github.com/CDOT-CV/jpo-ode/pull/134): Migrate AsnCodecMessageServiceController to Spring Kafka
- [CDOT PR 136](https://github.com/CDOT-CV/jpo-ode/pull/136): Update the schema version to 8 for the output messages
- [CDOT PR 137](https://github.com/CDOT-CV/jpo-ode/pull/137): GitHub Actions Caching
- [CDOT PR 140](https://github.com/CDOT-CV/jpo-ode/pull/140): Data flow diagram updates - Spring Kafka related
- [CDOT PR 141](https://github.com/CDOT-CV/jpo-ode/pull/141): CDOT ASN1 Codec Submodule Update
- [CDOT PR 142](https://github.com/CDOT-CV/jpo-ode/pull/142): Set scope of annotation processors to 'provided'
- [CDOT PR 143](https://github.com/CDOT-CV/jpo-ode/pull/143): Updated Submodule Compatibility Guide for 2025 Q1 Release
- [CDOT PR 145](https://github.com/CDOT-CV/jpo-ode/pull/145): Resolve TIM schema inconsistency
- [CDOT PR 146](https://github.com/CDOT-CV/jpo-ode/pull/146): Update jpo-utils Submodule Reference to Fix Linux Startup Issues
- [CDOT PR 147](https://github.com/CDOT-CV/jpo-ode/pull/147): Fixed Data Loss in UDP Payload Handling by Updating UdpHexDecoder Logic
- [CDOT PR 148](https://github.com/CDOT-CV/jpo-ode/pull/148): Update jpo-utils Submodule Reference to Fix Required ENV Variables
- [CDOT PR 149](https://github.com/CDOT-CV/jpo-ode/pull/149): JPO Utils Commit Reference Change to Create "topic.OdeTimRxJson" Topic
- [CDOT PR 151](https://github.com/CDOT-CV/jpo-ode/pull/151): Kafka and logging updates
- [CDOT PR 152](https://github.com/CDOT-CV/jpo-ode/pull/152): TIM and Map Schema Fixes
- [CDOT PR 153](https://github.com/CDOT-CV/jpo-ode/pull/153): Fix: producer failures
- [CDOT PR 154](https://github.com/CDOT-CV/jpo-ode/pull/154): Fix: Use odeKafkaProperties env vars to drive producer retries
- [USDOT PR 559](https://github.com/usdot-jpo-ode/jpo-ode/pull/559): Update GitHub Actions Third-Party Action Versions
- [USDOT PR 561](https://github.com/usdot-jpo-ode/jpo-ode/pull/561): Bump ch.qos.logback:logback-core from 1.4.14 to 1.5.13 in /jpo-ode-plugins

Breaking changes:
- The major version was incremented due to breaking changes in the 2024 revision of J2735.

Known Issues:
- No known issues at this time.

Version 3.0.0, released September 2024
----------------------------------------
### **Summary**
The updates for the jpo-ode 3.0.0 release include several key improvements and cleanups. Outdated 'deposit over WebSocket to SDX' code was removed and the ppm_tim service was eliminated from Docker compose files. Additionally, the jpo-s3-deposit submodule was replaced with the jpo-utils submodule. Error handling was enhanced, particularly in interpreting "SNMP Error Code 10" from RSUs and stack traces for bad encoded data from ACM are now printed only when debug logging is enabled. Documentation updates provide more granular project references and mapfile references in ppm*.properties files were updated. Build and deployment improvements include resolving a UID conflict for container builds and adding Maven JAR publishing to GitHub Maven Central via GitHub Actions. Lastly, a Docker startup script was introduced for log offloading via SSH/SCP and source ASN1 bytes payload support was added for IMP depositors.

Enhancements in this release:
- CDOT PR 83: Removed stale 'deposit over WebSocket to SDX' code
- CDOT PR 86: Improved interpretation of the "SNMP Error Code 10" error message returned by RSUs
- CDOT PR 89: Expanded project reference update documentation to be more granular
- CDOT PR 90: Updated mapfile reference in `ppm*.properties` files
- CDOT PR 91: Modified code to print stack trace upon receiving bad encoded data from ACM only if debug logging is enabled
- CDOT PR 92: Added a startup script to Dockerfile to allow for log offloading support via SSH/SCP
- CDOT PR 93: Removed `ppm_tim` service from the docker compose files
- CDOT PR 94: Added maven JAR publishing to GitHub Maven Central via Github Actions
- CDOT PR 95: Fixed dev container build failure due to specified uid being taken
- CDOT PR 96: Added the source ASN1 bytes payload to message topics to allow for IMP depositor support
- CDOT PR 97: Removed jpo-s3-deposit submodule and added jpo-utils repository as a submodule
- CDOT PR 99: Remove Spring Dependencies from Maven Jars
- CDOT PR 101: ODE JSON Schema Updates


Version 2.1.0, released June 2024
----------------------------------------
### **Summary**
The updates for the jpo-ode 2.1.0 release include several key improvements and fixes. These updates address issues with PSM and log offloading and enhance the continuous integration processes. The Kafka version has been upgraded and a bug related to log processing has been resolved. Nanoseconds are now trimmed from timestamps and 1609.2 headers are now stripped from unsigned messages. A submodule compatibility guide has been added, along with making the default SNMP protocol configurable. Configurable signing is now possible independently for Road-Side Units (RSUs) and the SDX. The Dockerhub documentation now includes a link to the submodule compatibility guide. Maven plugin versions have been updated and the Kafka topic creation process has been improved. A timestamp precision bug has been fixed and the documentation has been revised for accuracy. Additionally, the NTCIP1218 msgRepeatOptions value is now set based on context and SnmpSession now reports failures to retrieve authoritative engine IDs only if a response is received. Finally, the TimDeleteController has been updated to log message deletion failures as errors.

Enhancements in this release:
CDOT PR 57: Fixes for PSM & Log Offloading
CDOT PR 58: Updated CI
CDOT PR 59: Updated Kafka version
CDOT PR 61: Fixed bug with log processing
CDOT PR 62: Trimmed nanoseconds from snmpTimeStampFromIso
CDOT PR 63: Stripped 1609.2 headers from unsigned messages
CDOT PR 64: Added submodule compatibility guide
CDOT PR 65: Added support for retaining IEEE 1609.2 security headers
CDOT PR 66: Made default SNMP protocol configurable
CDOT PR 67: Added configurable signing independently for RSUs and the SDX
CDOT PR 69: Added link to submodule compatibility guide to Docker Hub documentation
CDOT PR 70: Updated maven plugin versions
CDOT PR 71: Updated Kafka topic creation
CDOT PR 72: Fixed timestamp precision bug
CDOT PR 73: Revised documentation for accuracy
CDOT PR 74: Kafka Connect & MongoDB Database
CDOT PR 75: Set NTCIP1218 msgRepeatOptions value based on context
CDOT PR 76: Updated SnmpSession to report failures to retrieve authoritative engine IDs only if a response is received
CDOT PR 78: UDP/Log Ingestion Updates
CDOT PR 79: Updated TimDeleteController to log failures to delete messages as errors

Version 2.0.2, released April 2024
----------------------------------------
### **Summary**
The updates for the jpo-ode 2.0.2 release includes an update to the dataflow for the UDP and log ingestion endpoints. This update allows for the UDP and log ingestion endpoints to handle and strip unsigned IEEE 1609.2 and 1609.3 headers. Signed IEEE 1609.2 security headers are maintained in the encoded Kafka topic prior to being decoded.

Enhancements in this release:
- Support IEEE 1609.2 and 1609.3 header ingestion
- Strips unsigned IEEE 1609.2 headers
- Strips IEEE 1609.2 headers
- Maintains signed IEEE 1609.3 headers before eventually stripping them before decoding to the J2735 payload

Known Issues:
- No known issues at this time.

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
The updates for jpo-ode 1.3.0 release includes enhancements such as support for Confluent Cloud, better debugging with the C++ submodules, new JSON output schemas and improvements for all J2735 message type ser/des. Along with the enhancements below, several bug fixes and CI related enhancements are included in this release.

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
- Issue 432: Fixed Replace single Kafka topic consumer with multiple message type specific consumers by three different topics for receiving encoded BSM, encoded TIM and encoded SPAT.
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

### Release 1.0.7 (Apr 15, 2019)
- Integrated odevalidator v0.0.3

### Release 1.0.6 (Apr 8, 2019)
- Integrated odevalidator v0.0.1
    - ODE-1234 Updated the odevalidator library with conditional configurability.

### Release 1.0.5 (Mar 20, 2019)
- ODE-1209 Fixed missing bsmSource metadata field
- ODE-944 Creates SDW Depositor microservice using the new SDW REST API
- ODE-1056 Modifying ODE to support both WebSocket interface or SDW Depositor microservice by configurable properties

### Release 1.0.4 (Feb 12, 2019)
- ODE-1136 Fixed metadata bug causing duplicate or invalid metadata elements (locationData, odeReceivedAt, recordGeneratedAt)
- ODE-844 Fixed GitHub issue 264: New Content Type for TIMs desired by WYDOT
- ODE-994 Add the ODE version number to the Demo UI

### Release 1.0.3 (Feb 7, 2019)
- ODE-944 Added Situation Data Warehouse depositor submodule to the ODE

### Release 1.0.2 (Jan 22, 2019)
- ODE-1076 New values added for metadata.recordGeneratedBy: RSU, TMC_VIA_SAT, TMC_VIA_SNMP

### Release 1.0.1 (Dec 20, 2019)
- ODE-466 serialId element of metadata will now provide a true serial number to help identify missing records.
- ODE-964 Added ode.rsuUsername/ode.rsuPassword application properties or alternatively ODE_RSU_USERNAME and ODE_RSU_PASSWORD environment variables to authenticate communications with RSU instead of using the REST API parameters.
- ODE-992 Added `/version` endpoint to get the version information of a running ODE.

### Release 1.0.0
- ODE-787 Publish J2735 version of Broadcast TIM in addition to the REST request version (see GitHub Issue #262). Please see https://github.com/usdot-jpo-ode/jpo-ode/wiki/Schema-Version-6-Change-Notice for details.

### Sprint 38
- ODE-769 Initiated output schema change SOP. See https://github.com/usdot-jpo-ode/jpo-ode/wiki/SchemaVersion-5-Change-Notice for details
- ODE-771 Fixed PPM crash bug

### Sprint 37
- ODE-745	Updated ODE documentation to reflect actual implementation of the ODE output interface schema
- ODE-763	Added support for TIMs with ITIS custom text
- ODE-764	Fixed how Trailer Mass was calculated
- ODE-768 Created a repository of ASN.1 schema files on usdot-jpo-ode GitHub organization based on scms-asn to eliminate dependency on CAMP SCMS and avoid build failures due to scms-asn site outages. All references to CAMP SCMS repository was changed to point to usdot-jpo-ode/scms-asn1 repository.

### Sprint 36
- ODE-741 Added capability to load Explicit Enrollment Certificates

### Sprint 35
- ODE-736 Added capability to configure Record ID of SDW TIMs

### Sprint 34
- ODE-733 Fixed a bug where single-byte OCTET STRINGs were being encoded incorrectly

### Sprint 33
- ODE-560 Added capability to Receive Compressed Log Files
- ODE-707 Fixed a bug resulting in first TIM deposit after startup not propagating
- ODE-725 Registered ODE in Code.gov

### Sprint 32


### Sprint 31
- ODE-685 Added metadata field `bsmSource` to identify the source of the BSM as host (EV) or remote (RV). See https://github.com/usdot-jpo-ode/jpo-ode/wiki/Log-File-Changes-(schemaVersion=4) for details.
- ODE-688 Deploed firehose into production for CVPEP and RDE
- ODE-689 and ODE-690: Replaced boolean metadata field `validSignature` with integer `securityResultCode` to better convey the status of security validation. See https://github.com/usdot-jpo-ode/jpo-ode/wiki/Log-File-Changes-(schemaVersion=4) for details.
- ODE-692 Improved AEM/ADM error handling for when connection with Kafka broker is interrupted

### Sprint 30
- ODE-680 Migrated SDW/SDC calls to new production endpoints

### Sprint 29
- ODE-675 CHanged jpo-S3-deposit module to send CVPEP data through AWS Firehose

### Sprint 28
- ODE-670 J2735TravelerInformationMessage.packetID needs to be BigInteger
  see https://github.com/usdot-jpo-ode/jpo-ode/wiki/TIM-REST-Endpoint-Changes for interface changes.

### Sprint 27
- ODE-661 CVPEP Data Inconsistency for TIM Files
- ODE-657 Continue Supporting WyDOT issues
- ODE-591 ORNL - Further generalize encoding and decoding capability of asn1_codec module

### Sprint 26
- ODE-646 Sirius XM Requires ASD messages to be wrapped in IEEE 1698.2 Data
- ODE-645 Update Documentation for Metadata
- ODE-642 Address SDW and RSU flexibility
- ODE-632 ODE Error out on log files

### Sprint 25
- ODE-588 Free and Open Source ODE Minimum Viable Product
- ODE-615 ORNL Implement SDW TIM encoding
- ODE-587 Implement TIM data encoding through ASN.1 Encoder Module (AEM)
- ODE-596 Support receiving and publishing of Driver Alert Messages
- ODE-613 SDW Sending of TIM Messages using asn1_codec
- ODE-631 Add capability to disable output topics

### Sprint 24
- ODE-537 ASN1 CODEC Module development
- ODE-543 Publish a defined v3 for metadata	Story	Medule
- ODE-581 Integrate ODE with asn1 decoder module (adm) for BSM decoding
- ODE-584 Decode Inbound TIM through asn1 decoder module (adm)
- ODE-585 Implement asn1 decoding capability
- ODE-586 Implement asn1 encoding capability
- ODE-592 Added Message Frame to Outbound TIM messages
- ODE-593 Support the receiving and publishing of Distress Notifications
- ODE-604 Support multiple types of payloads for receivedMsgRecord

### Sprint 23
- ODE-559	ASN1 CODEC Decoder Module Connection

### Sprint 22
- ODE-528	Support publishing of TIM data received from WYDOT log files

### Sprint 21
- ODE-483 Implementing generatedAt field in the metadata for TIM
- ODE-512	Support receiving WYDOT BSM Log files

### Sprint 20
- ODE-476 Change TIM schema to use real unit values
- ODE-485 TIM S3 depositor service

### Sprint 18
- ODE-201	Validate/authenticate Basic Safety Message (BSM) and MessageFrames received from the OBU using the 1609.2 Implementation
- ODE-436	Provide header information in JSON stream of processed BSM	Story	Medium

### Sprint 17
- ODE-314 Build VSDs from received BSMs and deposit them to SDC (Phase 2)
- ODE-413 Generalize S3 Depositor with configuration features

### Sprint 16
- ODE-400 Updated TIM REST Calls to enable querying and deleting messages
- ODE-401 Created S3 deposit service to watch Kafka topic and deposit BSM files
- ODE-381 1609.2 Security Libary is located within the ODE library

### Sprint 15
- ODE-381 Leidos Security Library Integration implementing 1609.2
  -- As part of this new feature, the build process was updated in the [ODE README file](../README.md)) with additional steps for obtaining the source code and building the application. Please review the [Getting Started](https://github.com/usdot-jpo-ode/jpo-ode#getting-started) section for details.

### Sprint 14
- ODE-312 Receiving Raw BSMs over UDP/IP (Phase 2)
- ODE-310 Receiving Raw ISD (Phase 1)
- ODE-311 Submiting ISD to SDC (Phase 1)

### Sprint 13
- ODE-290 Integrate with ORNL Privacy Protection Module (PPM)

### Sprint 12
- ODE-339 Deposit Raw VSD to SDC (Phase 1)

### Sprint 11
- (ODE-77 Subtask) ODE-274 Publish to J2735BsmRawJson

### Sprint 10
- ODE-259 Interface Control Document (ICD) - Updated
- ODE-268 Fixed Message CRC field in TIM messages causing error

### Sprint 9
- ODE-227 Probe Data Management (PDM) - Outbound
- ODE-230 Interface Control Document (ICD) - Created
- ODE-202 Evaluate Current 1609.2 Leidos Code

### Sprint 8
- ODE-143 Outbound TIM Message Parameters - Phase 2
- ODE-146 Provide generic SDW Deposit Capability
- ODE-147 Deposit TIM message to SDW.

### Sprint 7
- ODE-125 Expose empty field ODE output records when presented in JSON format
- ODE-142 Outbound TIM Message Parameters - Phase 1
- ODE-169 Encode TIM Message to ASN.1 - Outbound
- ODE-171 Research 1609.2 Standard Implementation

### Sprint 6
- ODE-138 Add Capability for Raw BSM Data (bin format only) with Header Information
- ODE-150 Encode TIM Message to ASN.1 (Inbound messages only)
- ODE-148 Develop More Robust User Facing Documentation

### Sprint 5
- ODE-126 ADD to ODE 58 - Log ODE Data Flows On/off without restarting ODE
- ODE-74 RESTful SNMP Wrapper Service to pass SNMP messages to an RSU
- ODE-127 Defined future story and tasks for inbound/outbound TIM messages

### Sprint 4
- ODE-123 Developed a sample client application to interface directly with Kafka service to subscribe to ODE data
- ODE-118 Validate BSM data decoding, inclusing Part II, with real binary data from OBU
- ODE-54 Authored first draft of ODE User Guide
- ODE-58 Developed ODE Event Logger
- ODE-41 Importer improvements

### Sprint 3
- ODE-42 Clean up the kafka adapter and make it work with Kafka broker. Integrated kafka. Kept Stomp as the high level WebSocket API protocol.
- ODE-36 - Docker, docker-compose, Kafka and ode Integration
