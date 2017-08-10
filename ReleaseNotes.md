## ODE Release Notes ##
----------
### Sprint 18
ODE-201	Validate/authenticate Basic Safety Message (BSM) and MessageFrames received from the OBU using the 1609.2 Implementation
ODE-436	Provide header information in JSON stream of processed BSM	Story	Medium

### Sprint 17
ODE-314 Build VSDs from received BSMs and deposit them to SDC (Phase 2)
ODE-413 Generalize S3 Depositor with configuration features

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
