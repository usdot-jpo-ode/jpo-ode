## ODE Release Notes ##
-----------------------
NOTE: For release details for latest releases, please see [release notes](https://github.com/usdot-jpo-ode/jpo-ode/releases)

### Release 1.0.8 (May 24, 2019)
- Integrated odevalidator v0.0.6
- Updated test-harness (see [release notes](./qa/test-harness/README.md#release-history))
- Changed SNMP deposit timestamp format from MM/DD/YYYY to YYYY/MM/DD
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
-
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
-- As part of this new feature, the build process was updated in the [ODE README file](README.md) with additional steps for obtaining the source code and building the application. Please review the [Getting Started](https://github.com/usdot-jpo-ode/jpo-ode#getting-started) section for details.

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
