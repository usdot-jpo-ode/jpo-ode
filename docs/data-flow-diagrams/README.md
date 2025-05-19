# Data Flow Diagrams
The purpose of these diagrams is to show:
- how data flows through the Operation Data Environment (both in an overall sense and for each message type)
- how the ODE interacts with kafka topics as well as its submodules.

## Key Explanation
- The blue rectangles are java classes that belong to this repository.
- The yellow ovals are kafka topics that the ODE and its submodules consume from and produce to.
- The red rectangles are groups of java classes.
- The red ovals are groups of kafka topics.
- The green rectangles are submodules of the ODE.
- The arrows indicate the data flow. The beginning of the arrow is where it flows from and the end of the arrow is where it flows to.

## Data Flow Explanations
### Overview Data Flow 1 (Tim Deposit Controller)
1. Messages come in through the [TimDepositController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/traveler/TimDepositController.java) class and are pushed to the Broadcast Messages and Json Messages groups of topics, as well as the AsnEncoderInput topic.
2. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1EncoderInput and pushes encoded messages to the Asn1EncoderOutput topic.
3. The [AsnEncodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1EncodedDataRouter.java) class pulls from the Asn1EncoderOutput topic and routes the message.
   1. If the message is not signed, it is sent to the [jpo-security-svcs](/jpo-security-svcs/jpo-security-svcs/src/main/java/us/dot/its/jpo/sec/controllers/SignatureController.java) REST API to be signed.
   2. If the message is signed and meant for the RSU, it will be passed to the [RsuDepositor](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/rsu/RsuDepositor.java) class which sends the message to the RSUs.
   3. If the message is signed, is meant for the SDX, and the message has not been double-encoded, it will be sent back to the Asn1EncoderInput topic for encoding.
   4. If the message is signed, is meant for the SDX, and the message has been double-encoded, it will be passed to the SDWDepositorInput, pulled into the [SDWDepositor](https://github.com/usdot-jpo-ode/jpo-sdw-depositor) and sent to the SDX.
4. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the Json Messages group of topics and sends filtered messages to the Filtered Json Messages group of topics.
5. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the Json Messages group of topics, converts the messages and pushes them to the Processed Spat/Map group of topics.
6. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the Processed Map/Spat group of topics and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### Overview Data Flow 2 (Receiver Classes)
1. Messages come in through the [UDP Receivers](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp) and are pushed to the Raw Encoded Messages group of topics.
2. The classes under [jpo/ode/kafka/listeners/asn1](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1) process these raw encoded messages
3. These classes push the message to the Asn1DecoderInput topic.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from that topic and pushes decoded messages to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and deposits messages into the Pojo Messages group of topics and the Json Messages group of topics.
6. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the Json Messages group of topics and pushes filtered messages to the Filtered Json Messages group of topics.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the Json Messages group of topics, converts the messages and pushes them to the Processed Spat/Map group of topics.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the Processed Map/Spat group of topics and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### Overview Data Flow 3 (Offloaded Files)
1. Messages are offloaded onto a directory referenced by the [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java).
2. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
3. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for and processes new log files every one second.
   1. If the message is a DriverAlert, the [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) pushes it to the OdeDriverAlertJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
   2. If the message is a BSM, SPAT, TIM or MAP, the [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) class pushes it to the corresponding OdeRawEncoded JSON topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
   3. If the message is a BSM, SPAT, TIM or MAP, the relevant router under [kafka/listeners/asn1](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1) 
      pulls from the OdeRawEncoded JSON topics and processes the data. This class then writes to the Asn1DecoderInput topic.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from that topic and pushes decoded messages to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and deposits messages into the Pojo Messages group of topics and the Json Messages group of topics.
6. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the Json Messages group of topics and pushes filtered messages to the Filtered Json Messages group of topics.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the Json Messages group of topics, converts the messages and pushes them to the Processed Spat/Map group of topics.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the Processed Map/Spat group of topics and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### BSM Data Flow 1 (Receiver Classes)
1. The BSM comes in through the BsmReceiver class and is pushed to the OdeRawEncodedBSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/RawEncodedBSMJsonRouter.java) processes messages from the OdeRawEncodedBSMJson topic
3. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/RawEncodedBSMJsonRouter.java) pushes the TIM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic and pushes the decoded BSM to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and
   pushes the BSM to the OdeBsmRxPojo, OdeBsmTxPojo, OdeBsmPojo and OdeBsmDuringEventPojo topics.
6. The [BSMPojoToJSONListener](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/BSMPojoToJSONListener.java) class pulls from OdeBsmPojo and pushes the BSM in JSON form to the OdeBsmJson topic.
7. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeBsmJson topic and pushes the filtered BSM to the FilteredOdeBsmJson topic.

### BSM Data Flow 2 (Offloaded Files)
1. The BSM is offloaded onto a directory referenced by the FileUploadController class.
2. The FileUploadController class indirectly invokes the LogFileToAsn1CodecPublisher class, which handles the offloaded message.
3. The LogFileToAsn1CodecPublisher class pushes the BSM to the RawEncodedBSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) processes messages from the OdeRawEncodedBSMJson topic
5. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) class pushes the TIM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
6. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic and pushes the decoded BSM to the Asn1DecoderOutput topic.
7. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and
   pushes the BSM to the OdeBsmRxPojo, OdeBsmTxPojo, OdeBsmPojo and OdeBsmDuringEventPojo topics.
8. The [BSMPojoToJSONListener](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/BSMPojoToJSONListener.java) class pulls from OdeBsmPojo and pushes the BSM in JSON form to the OdeBsmJson topic.
9. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeBsmJson topic and pushes the filtered BSM to the FilteredOdeBsmJson topic.

### TIM Data Flow 1 (Tim Depositor Controller)
see [Overview Data Flow 1 (Tim Depositor Controller)](#overview-data-flow-1-tim-depositor-controller)

### TIM Data Flow 2 (Receiver Classes)
1. The TIM comes in through the TimReceiver class and is pushed to the OdeRawEncodedTIMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedTIMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) processes messages from the OdeRawEncodedTIMJson topic
3. The [RawEncodedTIMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) class pushes the TIM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the TIM, and pushes it to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) pulls from the Asn1DecoderOutput topic and pushes the TIM to the OdeTimJson, OdeTimRxJson and OdeDNMsgJson topics.

### TIM Data Flow 3 (Offloaded Files)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for and processes new log files every one second.
3. The [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) pushes it to the TIM to the RawEncodedTIMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedTIMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) processes messages from the OdeRawEncodedTIMJson topic and pushes the TIM to the Asn1DecoderInput topic. 
5. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the TIM, and pushes it to the Asn1DecoderOutput topic.
6. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) pulls from the Asn1DecoderOutput topic and pushes the TIM to the OdeTimJson, OdeTimRxJson and OdeDNMsgJson topics.

### SPAT Data Flow 1 (Receiver Classes)
1. The SPAT comes in through the SpatReceiver class and is pushed to the OdeRawEncodedSPATJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSPATJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) processes messages from the OdeRawEncodedSPATJson topic.
3. The [RawEncodedSPATJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) class pushes the SPAT to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SPAT, and pushes it to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and 
   pushes the SPAT to the OdeSpatPojo, OdeSpatRxPojo, OdeDNMsgJson, OdeSpatRxJson, OdeSpatTxPojo and OdeSpatJson topics.
6. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeSpatJson topic, converts the SPAT and pushes it to the ProcessedOdeSpatJson topic.
7. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedOdeSpatJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### SPAT Data Flow 2 (Offloaded Files)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the SPAT log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for and processes new log files every one second.
3. The [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) pushes the SPAT to the OdeRawEncodedSPATJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedSPATJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSPATJsonRouter.java) consumes messages from the OdeRawEncodedSPATJson topic and pushes the SPAT message to the Asn1DecoderInput topic.
5. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SPAT, and pushes it to the Asn1DecoderOutput topic.
6. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and pushes the SPAT message to the OdeSpatPojo, OdeSpatRxPojo, OdeDNMsgJson, OdeSpatRxJson, OdeSpatTxPojo and OdeSpatJson topics.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeSpatJson topic, converts the SPAT and pushes it to the ProcessedOdeSpatJson topic.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedOdeSpatJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### MAP Data Flow 1 (Receiver Classes)
1. The MAP comes in through the [MapReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/map/MapReceiver.java) and is pushed to the OdeRawEncodedMAPJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedMAPJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedMAPJsonRouter.java) processes messages from the OdeRawEncodedMAPJson topic.
3. The RawEncodedMAPJsonRouter class pushes the MAP to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the MAP, and pushes it to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the MAP to the OdeMapTxPojo and OdeMapJson topics.
6. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdeMapJson topic, converts the MAP and pushes it to the ProcessedOdeMapJson topic.
7. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) consumes from the ProcessedOdeMapJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### MAP Data Flow 2 (Offloaded Files)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the MAP log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for and processes new log files every one second.
3. [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java)  pushes the MAP to the OdeRawEncodedMAPJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedMAPJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/RawEncodedMAPJsonRouter.java) consumes messages from the OdeRawEncodedMAPJson topic and pushes the MAP to the Asn1DecoderInput topic.
5. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the MAP, and pushes it to the Asn1DecoderOutput topic.
6. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the MAP to the OdeMapTxPojo and OdeMapJson topics.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdeMapJson topic, converts the MAP and pushes it to the ProcessedOdeMapJson topic.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) consumes from the ProcessedOdeMapJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### SRM Data Flow
1. The SRM comes in through the [SrmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/srm/SrmReceiver.java) and is pushed to the OdeRawEncodedSRMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSRMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/RawEncodedSRMJsonRouter.java) processes messages from the OdeRawEncodedSRMJson topic and pushes the SRM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the SRM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the SRM to the OdeSrmTxPojo and OdeSrmJson topics.

### SSM Data Flow
1. The SSM comes in through the [SsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/ssm/SsmReceiver.java) class and is pushed to the OdeRawEncodedSSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/RawEncodedSSMJsonRouter.java) consumes messages from the OdeRawEncodedSSMJson topic and pushes the SSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the SSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the SSM to the OdeSsmPojo and OdeSsmJson topics.

### PSM Data Flow
1. The PSM comes in through the [PsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/psm/PsmReceiver.java) class and is pushed to the OdeRawEncodedPSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedPSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/RawEncodedPSMJsonRouter.java) processes messages from the OdeRawEncodedPSMJson topic and pushes the PSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the PSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the PSM to the OdePsmTxPojo and OdePsmJson topics.

### SDSM Data Flow
1. The SDSM comes in through the [SdsReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/sds/SdsReceiver.java) class and is pushed to the OdeRawEncodedSDSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSDSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/RawEncodedSDSMJsonRouter.java) consumes messages from the OdeRawEncodedSDSMJson topic and pushes the SDSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the SDSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the SDSM to the OdeSdsJson topic.
