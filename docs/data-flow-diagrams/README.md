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
### Overview Data Flow 1
1. Messages come in through the receiver classes and are pushed to the Raw Encoded Messages group of topics.
1. The AsnCodecMessageServiceController pulls these raw encoded messages and passes them over to the Asn1Decode classes.
1. These classes push the message to the Asn1DecoderInput topic.
1. The ACM pulls from that topic and pushes decoded messages to the Asn1DecoderOutput topic.
1. The Asn1DecodeDataRouter class pulls from the Asn1DecodeOutput topic and deposits messages into the Pojo Messages group of topics and the Json Messages group of topics.
1. The PPM pulls from the Json Messages group of topics and pushes filtered messages to the Filtered Json Messages group of topics.
1. The FileUploadController class pulls from the Json Messages & Filtered Json Messages groups of topics and offloads them.

### Overview Data Flow 2
1. Messages come in through the TimDepositorController class and are pushed to the Broadcast Messages and Json Messages groups of topics, as well as the AsnEncoderInput topic.
1. The ACM pulls from the Asn1EncoderInput and pushes encoded messages to the Asn1EncoderOutput topic.
1. The AsnEncodedDataRouter class pulls from the Asn1EncoderOutput topic and pushes it to the AsnCommandManager class.
1. If the message is not signed, it is sent to the SignatureController class to be signed.
1. If the message is signed and meant for the RSU, it will be passed to the RsuDepositor class which sends the message to the RSUs.
1. If the message is signed and not meant for the RSU, it is meant for the SDX.
1. If the message has not been double-encoded, yet, it will be sent back to the Asn1EncoderInput topic for encoding.
1. If the message has been double-encoded, it will be passed to the SDWDepositorInput, pulled into the SDWD and sent to the SDX.
1. The PPM pulls from the Json Messages group of topics and sends filtered messages to the Filtered Json Messages group of topics.
1. The FileUploadController class pulls from the Json Messages and Filtered Json Messages groups of topics and offloads them.

### BSM Data Flow
1. The BSM comes in through the BsmReceiver class and is pushed to the OdeRawEncodedBSMJson topic.
1. The Asn1CodecMessageServiceController pulls from the OdeRawEncodedBSMJson topic and pushes the BSM to the Asn1DecodeBSMJSON class.
1. The Asn1DecodeBSMJSON class pushes the BSM to the Asn1DecoderInput topic.
1. The ACM pulls from the Asn1DecoderInput topic and pushes the decoded BSM to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController pulls from the Asn1DecoderOutput topic and passes the BSM to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter pushes the BSM to the OdeBsmRxPojo, OdeBsmTxPojo, OdeBsmPojo and OdeBsmDuringEventPojo topics.
1. The ToJsonServiceController class pulls from OdeBsmPojo and pushes the BSM in JSON form to the OdeBsmJson topic.
1. The PPM pulls from the OdeBsmJson topic and pushes the filtered BSM to the FilteredOdeBsmJson topic.
1. The FileUploadController class pulls from the OdeBsmJson and FilteredOdeBsmJson topics and offloads the BSM.

### TIM Data Flow 1
1. The TIM comes in through the TimReceiver class and is pushed to the OdeRawEncodedTIMJson topic.
1. The AsnCodecMessageServiceController pulls from the OdeRawEncodedTIMJson topic and passes the TIM to the Asn1DecodeTIMJSON class.
1. The Asn1DecodeTIMJSON class pushes the TIM to the Asn1DecoderInput topic.
1. The ACM pulls from the Asn1DecoderInput topic, decodes the TIM, and pushes it to the Asn1DecoderOutput topic.
1. The Asn1DecodedDataRouter pulls from the Asn1DecoderOutput topic and pushes the TIM to the OdeTimJson, OdeTimRxJson and OdeDNMsgJson topics.
1. The PPM pulls from the OdeTimJson topic, filters the TIM and pushes it to the FilteredOdeTimJson topic.
1. The FileUploadController pulls from the OdeTimJson and FilteredOdeTimJson topics and offloads the TIM.

### TIM Data Flow 2
1. The TIM comes in through the TimDepositorController class and is pushed to the J2735TimBroadcastJson, OdeTimBroadcastJson, OdeTimBroadcastPojo and Asn1EncoderInput topics.
1. The ACM pulls from the Asn1EncoderInput topic, encodes the TIM, and pushes it to the Asn1EncoderOutput topic.
1. The Asn1EncodedDataRouter class pulls from the Asn1EncoderOutput topic and passes the TIM to the Asn1CommandManager class.
1. If the message is not signed, it is sent to the SignatureController class to be signed.
1. If the message is signed and meant for the RSU, it will be passed to the RsuDepositor class which sends the message to the RSUs.
1. If the message is signed and not meant for the RSU, it is meant for the SDX.
1. If the message has not been double-encoded, yet, it will be sent back to the Asn1EncoderInput topic for encoding.
1. If the message has been double-encoded, it will be passed to the SDWDepositorInput, pulled into the SDWD and sent to the SDX.
1. The PPM pulls from the OdeTimJson topic, filters the TIM and pushes it to the FilteredOdeTimJson topic.
1. The FileUploadController pulls from the OdeTimJson and FilteredOdeTimJson topics and offloads the TIM.

### SPAT Data Flow
1. The SPAT comes in through the SpatReceiver class and is pushed to the OdeRawEncodedSPATJson topic.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedSPATJson topic and passes the SRM to the Asn1DecodeSPATJSON class.
1. The Asn1DecodeSPATJSON class pushes the SPAT to the Asn1DecoderInput topic.
1. The ACM pulls from the Asn1DecoderInput topic, decodes the SPAT, and pushes it to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the SPAT to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter pushes the SPAT to the OdeSpatPojo, OdeSpatRxPojo, OdeDNMsgJson, OdeSpatRxJson, OdeSpatTxPojo and OdeSpatJson topics.
1. The PPM pulls from the OdeSpatJson topic, filters the SPAT, and pushes it to the FilteredOdeSpatJson topic.
1. The FileUploadController pulls from the OdeSpatJson and FilteredOdeSpatJson topics and offloads the SSM.

### MAP Data Flow
1. The MAP comes in through the MapReceiver class and is pushed to the OdeRawEncodedMAPJson topic.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedMAPJson topic and passes the MAP to the Asn1DecodeMAPJSON class.
1. The Asn1DecodeMAPJSON class pushes the MAP to the Asn1DecoderInput topic.
1. The ACM pulls from the Asn1DecoderInput topic, decodes the MAP, and pushes it to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the MAP to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter class pushes the MAP to the OdeMapTxPojo and OdeMapJson topics.
1. The FileUploadController pulls from the OdeMapJson topic and offloads the MAP.

### SRM Data Flow
1. The SRM comes in through the SrmReceiver class and is pushed to the OdeRawEncodedSRMJson topic.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedSRMJson topic and passes the SRM to the Asn1DecodeSRMJSON class.
1. The Asn1DecodeSRMJSON class pushes the SRM to the Asn1DecoderInput topic.
1. The ACM pulls from the Asn1DecoderInput topic, decodes the SRM, and pushes it to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the SRM to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter class pushes the SRM to the OdeSrmTxPojo and OdeSrmJson topics.
1. The FileUploadController pulls from the OdeSrmJson topic and offloads the SRM.

### SSM Data Flow
1. The SSM comes in through the SsmReceiver class and is pushed to the OdeRawEncodedSSMJson topic.
1. The AsnCodecMessageServiceController class pulls from the OdeRawEncodedSSMJson topic and passes the SSM to the Asn1DecodeSSMJSON class.
1. The Asn1DecodeSSMJSON class pushes the SSM to the Asn1DecoderInput topic.
1. The ACM pulls from the Asn1DecoderInput topic, decodes the SSM, and pushes it to the Asn1DecoderOutput topic.
1. The AsnCodecRouterServiceController class pulls from the Asn1DecoderOutput topic and passes the SSM to the Asn1DecodedDataRouter class.
1. The Asn1DecodedDataRouter class pushes the SSM to the OdeSsmTxPojo and OdeSsmJson topics.
1. The FileUploadController pulls from the OdeSsmJson topic and offloads the SSM.