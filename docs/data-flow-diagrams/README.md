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
### Overview Data Flow 1 (Tim Depositor Controller)
1. Messages come in through the TimDepositorController class and are pushed to the Broadcast Messages and Json Messages groups of topics, as well as the AsnEncoderInput topic.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1EncoderInput and pushes encoded messages to the Asn1EncoderOutput topic.
1. The AsnEncodedDataRouter class pulls from the Asn1EncoderOutput topic and pushes it to the AsnCommandManager class.
1. If the message is not signed, it is sent to the SignatureController class to be signed.
1. If the message is signed and meant for the RSU, it will be passed to the RsuDepositor class which sends the message to the RSUs.
1. If the message is signed, is meant for the SDX and the message has not been double-encoded, yet, it will be sent back to the Asn1EncoderInput topic for encoding.
1. If the message is signed, is meant for the SDX and the message has been double-encoded, it will be passed to the SDWDepositorInput, pulled into the [SDWD](https://github.com/usdot-jpo-ode/jpo-sdw-depositor) and sent to the SDX.
1. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the Json Messages group of topics and sends filtered messages to the Filtered Json Messages group of topics.
1. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the Json Messages group of topics, converts the messages and pushes them to the Processed Spat/Map group of topics.
1. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the Processed Map/Spat group of topics and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### Overview Data Flow 2 (Receiver Classes)
1. Messages come in through the receiver classes and are pushed to the Raw Encoded Messages group of topics.
1. The AsnCodecMessageServiceController pulls these raw encoded messages and passes them over to the Asn1Decode classes.
1. These classes push the message to the Asn1DecoderInput topic.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from that topic and pushes decoded messages to the Asn1DecoderOutput topic.
1. The Asn1DecodeDataRouter class pulls from the Asn1DecodeOutput topic and deposits messages into the Pojo Messages group of topics and the Json Messages group of topics.
1. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the Json Messages group of topics and pushes filtered messages to the Filtered Json Messages group of topics.
1. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the Json Messages group of topics, converts the messages and pushes them to the Processed Spat/Map group of topics.
1. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the Processed Map/Spat group of topics and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### Overview Data Flow 3 (Offloaded Files)
1. Messages are offloaded onto a directory referenced by the FileUploadController class.
1. The FileUploadController class indirectly invokes the LogFileToAsn1CodecPublisher class, which handles the offloaded messages.
1. If the message is a DriverAlert, the LogFileToAsn1CodecPublisher class pushes it to the OdeDriverAlertJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. If the message is a BSM, SPAT, TIM or MAP, the LogFileToAsn1CodecPublisher class pushes it to the corresponding OdeRawEncoded JSON topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. If the message is a BSM, SPAT, TIM or MAP, the Asn1CodecMessageServiceController pulls from the OdeRawEncoded JSON topics and processes the data with the corresponding Asn1Decode JSON class. This class then writes to the Asn1DecoderInput topic.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from that topic and pushes decoded messages to the Asn1DecoderOutput topic.
4. The Asn1DecodeDataRouter class pulls from the Asn1DecodeOutput topic and deposits messages into the Pojo Messages group of topics and the Json Messages group of topics.
5. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the Json Messages group of topics and pushes filtered messages to the Filtered Json Messages group of topics.
6. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the Json Messages group of topics, converts the messages and pushes them to the Processed Spat/Map group of topics.
7. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the Processed Map/Spat group of topics and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### BSM Data Flow 1 (Receiver Classes)
1. The BSM comes in through the BsmReceiver class and is pushed to the OdeRawEncodedBSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The Asn1CodecMessageServiceController pulls from the OdeRawEncodedBSMJson topic and pushes the BSM to the Asn1DecodeBSMJSON class.
1. The Asn1DecodeBSMJSON class pushes the BSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic and pushes the decoded BSM to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController pulls from the Asn1DecoderOutput topic and passes the BSM to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter pushes the BSM to the OdeBsmRxPojo, OdeBsmTxPojo, OdeBsmPojo and OdeBsmDuringEventPojo topics.
1. The ToJsonServiceController class pulls from OdeBsmPojo and pushes the BSM in JSON form to the OdeBsmJson topic.
1. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeBsmJson topic and pushes the filtered BSM to the FilteredOdeBsmJson topic.

### BSM Data Flow 2 (Offloaded Files)
1. The BSM is offloaded onto a directory referenced by the FileUploadController class.
1. The FileUploadController class indirectly invokes the LogFileToAsn1CodecPublisher class, which handles the offloaded message.
1. The LogFileToAsn1CodecPublisher class pushes the BSM to the RawEncodedBSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The Asn1CodecMessageServiceController pulls from the OdeRawEncodedBSMJson topic and pushes the BSM to the Asn1DecodeBSMJSON class.
1. The Asn1DecodeBSMJSON class pushes the BSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic and pushes the decoded BSM to the Asn1DecoderOutput topic.
4. The AsnCodecRouterServiceController pulls from the Asn1DecoderOutput topic and passes the BSM to the Asn1DecodedDataRouter class.
5. The Asn1DecodedDataRouter pushes the BSM to the OdeBsmRxPojo, OdeBsmTxPojo, OdeBsmPojo and OdeBsmDuringEventPojo topics.
6. The ToJsonServiceController class pulls from OdeBsmPojo and pushes the BSM in JSON form to the OdeBsmJson topic.
7. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeBsmJson topic and pushes the filtered BSM to the FilteredOdeBsmJson topic.

### TIM Data Flow 1 (Tim Depositor Controller)
1. The TIM comes in through the TimDepositorController class and is pushed to the J2735TimBroadcastJson, OdeTimBroadcastJson, OdeTimBroadcastPojo and Asn1EncoderInput topics.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1EncoderInput topic, encodes the TIM, and pushes it to the Asn1EncoderOutput topic.
1. The Asn1EncodedDataRouter class pulls from the Asn1EncoderOutput topic and passes the TIM to the Asn1CommandManager class.
1. If the message is not signed, it is sent to the SignatureController class to be signed.
1. If the message is signed and meant for the RSU, it will be passed to the RsuDepositor class which sends the message to the RSUs.
1. If the message is signed, is meant for the SDX and the message has not been double-encoded, yet, it will be sent back to the Asn1EncoderInput topic for encoding.
1. If the message is signed, is meant for the SDX and the message has been double-encoded, it will be passed to the SDWDepositorInput, pulled into the [SDWD](https://github.com/usdot-jpo-ode/jpo-sdw-depositor) and sent to the SDX.
1. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeTimJson topic, filters the TIM and pushes it to the FilteredOdeTimJson topic.

### TIM Data Flow 2 (Receiver Classes)
1. The TIM comes in through the TimReceiver class and is pushed to the OdeRawEncodedTIMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The AsnCodecMessageServiceController pulls from the OdeRawEncodedTIMJson topic and passes the TIM to the Asn1DecodeTIMJSON class.
1. The Asn1DecodeTIMJSON class pushes the TIM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the TIM, and pushes it to the Asn1DecoderOutput topic.
1. The Asn1DecodedDataRouter pulls from the Asn1DecoderOutput topic and pushes the TIM to the OdeTimJson, OdeTimRxJson and OdeDNMsgJson topics.
1. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeTimJson topic, filters the TIM and pushes it to the FilteredOdeTimJson topic.

### TIM Data Flow 3 (Offloaded Files)
1. The TIM is offloaded onto a directory referenced by the FileUploadController class.
1. The FileUploadController class indirectly invokes the LogFileToAsn1CodecPublisher class, which handles the offloaded message.
1. The LogFileToAsn1CodecPublisher class pushes the TIM to the RawEncodedTIMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The AsnCodecMessageServiceController pulls from the OdeRawEncodedTIMJson topic and passes the TIM to the Asn1DecodeTIMJSON class.
1. The Asn1DecodeTIMJSON class pushes the TIM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
2. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the TIM, and pushes it to the Asn1DecoderOutput topic.
3. The Asn1DecodedDataRouter pulls from the Asn1DecoderOutput topic and pushes the TIM to the OdeTimJson, OdeTimRxJson and OdeDNMsgJson topics.
4. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeTimJson topic, filters the TIM and pushes it to the FilteredOdeTimJson topic.

### SPAT Data Flow 1 (Receiver Classes)
1. The SPAT comes in through the SpatReceiver class and is pushed to the OdeRawEncodedSPATJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedSPATJson topic and passes the SRM to the Asn1DecodeSPATJSON class.
1. The Asn1DecodeSPATJSON class pushes the SPAT to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SPAT, and pushes it to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the SPAT to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter pushes the SPAT to the OdeSpatPojo, OdeSpatRxPojo, OdeDNMsgJson, OdeSpatRxJson, OdeSpatTxPojo and OdeSpatJson topics.
1. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeSpatJson topic, filters the SPAT, and pushes it to the FilteredOdeSpatJson topic.
1. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeSpatJson topic, converts the SPAT and pushes it to the ProcessedOdeSpatJson topic.
1. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedOdeSpatJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### SPAT Data Flow 2 (Offloaded Files)
1. The SPAT is offloaded onto a directory referenced by the FileUploadController class.
1. The FileUploadController class indirectly invokes the LogFileToAsn1CodecPublisher class, which handles the offloaded message.
1. The LogFileToAsn1CodecPublisher class pushes the SPAT to the OdeRawEncodedSPATJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedSPATJson topic and passes the SRM to the Asn1DecodeSPATJSON class.
1. The Asn1DecodeSPATJSON class pushes the SPAT to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
2. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SPAT, and pushes it to the Asn1DecoderOutput topic.
3. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the SPAT to the Asn1DecodedDataRouter class.
4. The Asn1DecodedDataRouter pushes the SPAT to the OdeSpatPojo, OdeSpatRxPojo, OdeDNMsgJson, OdeSpatRxJson, OdeSpatTxPojo and OdeSpatJson topics.
5. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeSpatJson topic, filters the SPAT, and pushes it to the FilteredOdeSpatJson topic.
6. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeSpatJson topic, converts the SPAT and pushes it to the ProcessedOdeSpatJson topic.
7. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedOdeSpatJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### MAP Data Flow 1 (Receiver Classes)
1. The MAP comes in through the MapReceiver class and is pushed to the OdeRawEncodedMAPJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedMAPJson topic and passes the MAP to the Asn1DecodeMAPJSON class.
1. The Asn1DecodeMAPJSON class pushes the MAP to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the MAP, and pushes it to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the MAP to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter class pushes the MAP to the OdeMapTxPojo and OdeMapJson topics.
1. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeMapJson topic, converts the MAP and pushes it to the ProcessedOdeMapJson topic.
1. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedOdeMapJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### MAP Data Flow 2 (Offloaded Files)
1. The MAP is offloaded onto a directory referenced by the FileUploadController class.
1. The FileUploadController class indirectly invokes the LogFileToAsn1CodecPublisher class, which handles the offloaded message.
1. The LogFileToAsn1CodecPublisher class pushes the MAP to the OdeRawEncodedMAPJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedMAPJson topic and passes the MAP to the Asn1DecodeMAPJSON class.
1. The Asn1DecodeMAPJSON class pushes the MAP to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
2. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the MAP, and pushes it to the Asn1DecoderOutput topic.
3. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the MAP to the Asn1DecodedDataRouter class.
4. The Asn1DecodedDataRouter class pushes the MAP to the OdeMapTxPojo and OdeMapJson topics.
5. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeMapJson topic, converts the MAP and pushes it to the ProcessedOdeMapJson topic.
6. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedOdeMapJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### SRM Data Flow
1. The SRM comes in through the SrmReceiver class and is pushed to the OdeRawEncodedSRMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedSRMJson topic and passes the SRM to the Asn1DecodeSRMJSON class.
1. The Asn1DecodeSRMJSON class pushes the SRM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SRM, and pushes it to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the SRM to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter class pushes the SRM to the OdeSrmTxPojo and OdeSrmJson topics.

### SSM Data Flow
1. The SSM comes in through the SsmReceiver class and is pushed to the OdeRawEncodedSSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedSSMJson topic and passes the SSM to the Asn1DecodeSSMJSON class.
1. The Asn1DecodeSSMJSON class pushes the SSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SSM, and pushes it to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the SSM to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter class pushes the SSM to the OdeSsmTxPojo and OdeSsmJson topics.

### PSM Data Flow
1. The PSM comes in through the PsmReceiver class and is pushed to the OdeRawEncodedPSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedPSMJson topic and passes the PSM to the Asn1DecodePSMJSON class. Any remaining signed IEEE 1609.2 headers are removed at this point.
1. The Asn1DecodePSMJSON class pushes the PSM to the Asn1DecoderInput topic.
1. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the PSM, and pushes it to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the PSM to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter class pushes the PSM to the OdePsmTxPojo and OdePsmJson topics.