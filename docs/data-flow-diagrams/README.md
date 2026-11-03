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
- Dashed outlines and dashed arrows show the `ffm` codec mode (`ODE_ASN1_CODEC_MODE=ffm`) elements. Solid elements are the `external` mode path or are shared by both modes.

## Codec Modes

The ODE selects its ASN.1 codec with `ODE_ASN1_CODEC_MODE`:

- `external` (default): the [asn1_codec](https://github.com/usdot-jpo-ode/asn1_codec) ACM decodes and encodes messages through the `Asn1Decoder*` and `Asn1Encoder*` topics. The **Data Flow** diagrams and the [Data Flow Explanations](#data-flow-explanations) describe this mode.
- `ffm`: the FFMLib codec runs in the ODE. Decoding happens in the ODE itself, and TIM encoding happens in `FfmlibEncodeService`. The **Data Flow** diagrams show its paths as dashed elements, and the [FFM Data Flow Explanations](#ffm-data-flow-explanations) describe them.

In `ffm` mode, `ODE_FFM_UDP_ROUTING` selects how UDP datagrams are decoded:

| Route | Setting | Delivery | Diagram |
|---|---|---|---|
| Raw topic (FFM default) | `ODE_FFM_UDP_ROUTING=raw-topic` | At least once. The raw offset is committed after the decoded JSON is confirmed. | [ODE Data Flow Overview](ODE%20Data%20Flow%20Overview.drawio.png) |
| Direct | `ODE_FFM_UDP_ROUTING=direct` | At most once. The datagram is decoded on its UDP receiver thread. | [ODE Data Flow Overview](ODE%20Data%20Flow%20Overview.drawio.png) |

## Decoding Paths

Every message is decoded by one of the paths below. `ODE_ASN1_CODEC_MODE` chooses ADM External or FFM. In FFM mode, `ODE_FFM_UDP_ROUTING` chooses how UDP datagrams are decoded. Each diagram's Key marks FFM elements with a dashed outline and dashed arrows.

| Path | Settings | Decoder | Runs in | Delivery |
|---|---|---|---|---|
| ADM External (default) | `ODE_ASN1_CODEC_MODE=external` | asn1_codec ACM (ADM), through the `Asn1DecoderInput` and `Asn1DecoderOutput` topics | External ACM process | Kafka consumer offsets. ODE writes no dead-letter records on this path. |
| FFM Raw-topic | `ODE_ASN1_CODEC_MODE=ffm`, `ODE_FFM_UDP_ROUTING=raw-topic` (default) | `FfmlibDecodeService`, called by `FfmlibRawJsonListener` | ODE process | At least once. The raw offset is committed after the decoded JSON is confirmed. |
| FFM Direct | `ODE_ASN1_CODEC_MODE=ffm`, `ODE_FFM_UDP_ROUTING=direct` | `FfmlibDecodeService`, called by `FfmlibDirectUdpDecoder` | UDP receiver thread | At most once. Nothing waits for Kafka confirmation. |
| FFM log import | `ODE_ASN1_CODEC_MODE=ffm` | `FfmlibDecodeService`, called by `LogFileToAsn1CodecPublisher` | ODE process | A failed record moves the whole file to the failed-file path. |
| ADM External log import | `ODE_ASN1_CODEC_MODE=external` | asn1_codec ACM, through the raw topic | External ACM process | As for the ADM External receiver path. |

### ADM External (default)
Used by the UDP receivers, log import and the TIM deposit in `external` mode.
1. UDP receivers publish each datagram to its `OdeRawEncoded*Json` topic. The raw record is the same in every mode.
2. The `RawEncoded*JsonRouter` for that type consumes the raw record, removes the transport headers and any remaining signed IEEE 1609.2 header, and sends the record to `Asn1DecoderInput`.
3. The ACM (ADM) decodes the UPER payload and writes the result to `Asn1DecoderOutput`.
4. `Asn1DecodedDataRouter` reads `Asn1DecoderOutput` and publishes the decoded JSON to `Ode*Json`.
5. The PPM, GeoJSON Converter and Conflict Monitor consume `Ode*Json`, as in every mode.

Each message type has its own router, so one type's backlog does not hold up another type.

### FFM Raw-topic
Used by the UDP receivers in `ffm` mode with `ODE_FFM_UDP_ROUTING=raw-topic`, which is the default.
1. The receivers publish to the raw topic exactly as in ADM External.
2. One `FfmlibRawJsonListener` per message type consumes that topic, one record at a time. It shares its consumer group with the external router for that type, so switching modes resumes from the committed offsets.
3. The listener parses the raw record and calls `FfmlibDecodeService`, which decodes the UPER payload in process (UPER to JER to JSON).
4. The listener publishes the JSON to `Ode*Json` and waits for Kafka to confirm the send. It allows at most three sends within a 30-second budget and commits the raw offset only after the confirmation.
5. Malformed, unsupported and serialization failures are copied to `dlq.<raw topic>` (for example, `dlq.OdeRawEncodedBSMJson`) with the original key and value and failure headers. The raw offset is committed once that write is confirmed.
6. Operational native failures (buffer capacity, output encoding, missing codec) are quarantined with `failure-category=infrastructure`. After `ODE_FFM_INFRA_HALT_THRESHOLD` consecutive failures (default 3), that type's listener stops and reports unhealthy.
7. The downstream consumers of `Ode*Json` are unchanged.

### FFM Direct
Used by the UDP receivers in `ffm` mode with `ODE_FFM_UDP_ROUTING=direct`. No raw-topic consumers are started.
1. Each receiver port has its own decode thread. `FfmlibDirectUdpDecoder` calls `FfmlibDecodeService` on the datagram before anything reaches Kafka.
2. The decoded JSON goes to `Ode*Json` without waiting for confirmation. A send that Kafka rejects is dropped and counted in `ode.ffmlib.direct.publish.failures`.
3. With `ODE_FFM_RAW_PUBLISH=true` (the default), the receiver also publishes the raw record to `OdeRawEncoded*Json`. ODE does not consume that topic on this path. Downstream consumers, such as jpo-deduplicator, may.
4. A decode failure is written without waiting to `dlq.<raw topic>` with `ingest-route=direct`. There is no source partition or offset. After the threshold of operational failures, health reports DOWN for that type until it decodes again, but the receivers keep running, because a datagram cannot be replayed.

### FFM log import
Used for offloaded files in `ffm` mode.
1. `FileUploadController` writes the log file. `ImporterDirectoryWatcher` scans the directory every second and hands each file to `LogFileToAsn1CodecPublisher`.
2. Driver alerts still go to `OdeDriverAlertJson`.
3. Every other record goes straight to `FfmlibDecodeService` and then to `Ode*Json`. It does not pass through a raw topic or the ACM.
4. If a record fails, the file moves to the failed-file path and its remaining records are not published. Records published before the failure stay visible. Reimporting the whole file publishes that prefix again.

### ADM External log import
Used for offloaded files in `external` mode.
1. The same file upload and watcher steps as FFM log import.
2. `LogFileToAsn1CodecPublisher` publishes each record to its raw topic.
3. From there the path is the ADM External receiver path above: the router, then the ACM (ADM), then `Ode*Json`.

### FFM TIM deposit
Used by `POST` and `PUT /tim` in `ffm` mode.
1. `TimDepositController` prepares the TIM and encodes its MessageFrame in process with `FfmlibEncodeService` (XER to UPER). Nothing is written to `Asn1EncoderInput`.
2. The TIM JSON is published to `OdeTimJson` once the initial encoding completes. The required TIM, certificate-expiration, filtered-TIM and SDX sends are confirmed within a shared 10-second budget before the request returns success.
3. `Asn1EncodedDataRouter` processes the encoded output in process, without `Asn1EncoderOutput`. It signs the TIM when signing is required, deposits it to the RSUs, and writes the certificate-expiration record and, for TMC-generated TIMs, `OdeTimJsonTMCFiltered`.
4. For SDX deposits, the second (ASD) encoding also runs in process with `FfmlibEncodeService`.
5. Native input errors return HTTP 400. Operational and publication failures return HTTP 500.

### ADM External TIM deposit
Used for `POST` and `PUT /tim` in `external` mode.
1. `TimDepositController` publishes the TIM JSON to `OdeTimJson` and writes the encoder input XML to `Asn1EncoderInput`.
2. The ACM (AEM) encodes the input and writes the result to `Asn1EncoderOutput`, which `Asn1EncodedDataRouter` consumes.
3. The router handles signing, RSU and SDX deposits as in FFM mode. The SDX second encoding is sent back through `Asn1EncoderInput`.

## Data Flow Explanations

These flows describe the default `external` codec mode. The `ffm` mode flows are in [FFM Data Flow Explanations](#ffm-data-flow-explanations).
### Overview Data Flow 1: path #1, TIM deposit (ADM External)
1. Messages come in through the [TimDepositController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/traveler/TimDepositController.java) class and are pushed to the OdeTimJson and AsnEncoderInput topics.
2. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1EncoderInput and pushes encoded messages to the Asn1EncoderOutput topic.
3. The [AsnEncodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1EncodedDataRouter.java) class pulls from the Asn1EncoderOutput topic and routes the message.
   1. If the message is not signed, it is sent to the [jpo-security-svcs](/jpo-security-svcs/jpo-security-svcs/src/main/java/us/dot/its/jpo/sec/controllers/SignatureController.java) REST API to be signed.
   2. If the message is signed and meant for the RSU, it will be passed to the [RsuDepositor](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/rsu/RsuDepositor.java) class which sends the message to the RSUs.
   3. If the message is signed, is meant for the SDX, and the message has not been double-encoded, it will be sent back to the Asn1EncoderInput topic for encoding.
   4. If the message is signed, is meant for the SDX, and the message has been double-encoded, it will be passed to the SDWDepositorInput, pulled into the [SDWDepositor](https://github.com/usdot-jpo-ode/jpo-sdw-depositor) and sent to the SDX.
4. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the Json Messages group of topics and sends filtered messages to the Filtered Json Messages group of topics.
5. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the Json Messages group of topics, converts the messages and pushes them to the Processed Spat/Map group of topics.
6. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the Processed Map/Spat group of topics and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### Overview Data Flow 2: path #2, UDP receivers (ADM External)
1. Messages come in through the [UDP Receivers](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp) and are pushed to the Raw Encoded Messages group of topics.
2. The classes under [jpo/ode/kafka/listeners/asn1](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1) process these raw encoded messages
3. These classes push the message to the Asn1DecoderInput topic.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from that topic and pushes decoded messages to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and deposits messages into the Pojo Messages group of topics and the Json Messages group of topics.
6. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the Json Messages group of topics and pushes filtered messages to the Filtered Json Messages group of topics.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the Json Messages group of topics, converts the messages and pushes them to the Processed Spat/Map group of topics.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the Processed Map/Spat group of topics and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### Overview Data Flow 3: path #3, log import (ADM External)
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

### BSM Data Flow 1: UDP receivers (ADM External)
1. The BSM comes in through the BsmReceiver class and is pushed to the OdeRawEncodedBSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) processes messages from the OdeRawEncodedBSMJson topic
3. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) pushes the BSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic and pushes the decoded BSM to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and
   pushes the BSM to the OdeBsmJson topic in JSON format.
6. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeBsmJson topic and pushes the filtered BSM to the FilteredOdeBsmJson topic.

### BSM Data Flow 2: log import (ADM External)
1. The BSM is offloaded onto a directory referenced by the FileUploadController class.
2. The FileUploadController class indirectly invokes the LogFileToAsn1CodecPublisher class, which handles the offloaded message.
3. The LogFileToAsn1CodecPublisher class pushes the BSM to the RawEncodedBSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) processes messages from the OdeRawEncodedBSMJson topic
5. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) class pushes the BSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
6. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic and pushes the decoded BSM to the Asn1DecoderOutput topic.
7. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and
   pushes the BSM to the OdeBsmJson topic in JSON format.
8. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeBsmJson topic and pushes the filtered BSM to the FilteredOdeBsmJson topic.

### TIM Data Flow 1: TIM deposit (ADM External)
see [Overview Data Flow 1: path #1, TIM deposit (ADM External)](#overview-data-flow-1-path-1-tim-deposit-adm-external)

### TIM Data Flow 2: UDP receivers (ADM External)
1. The TIM comes in through the TimReceiver class and is pushed to the OdeRawEncodedTIMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedTIMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) processes messages from the OdeRawEncodedTIMJson topic
3. The [RawEncodedTIMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) class pushes the TIM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the TIM, and pushes it to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) pulls from the Asn1DecoderOutput topic and pushes the TIM to the OdeTimJson topic.

### TIM Data Flow 3: log import (ADM External)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for and processes new log files every one second.
3. The [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) pushes it to the TIM to the RawEncodedTIMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedTIMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) processes messages from the OdeRawEncodedTIMJson topic and pushes the TIM to the Asn1DecoderInput topic. 
5. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the TIM, and pushes it to the Asn1DecoderOutput topic.
6. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) pulls from the Asn1DecoderOutput topic and pushes the TIM to the OdeTimJson topic.

### SPAT Data Flow 1: UDP receivers (ADM External)
1. The SPAT comes in through the SpatReceiver class and is pushed to the OdeRawEncodedSPATJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSPATJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) processes messages from the OdeRawEncodedSPATJson topic.
3. The [RawEncodedSPATJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) class pushes the SPAT to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SPAT, and pushes it to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and 
   pushes the SPAT to the OdeSpatJson topic.
6. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeSpatJson topic, converts the SPAT and pushes it to the ProcessedOdeSpatJson topic.
7. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedOdeSpatJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### SPAT Data Flow 2: log import (ADM External)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the SPAT log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for and processes new log files every one second.
3. The [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) pushes the SPAT to the OdeRawEncodedSPATJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedSPATJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSPATJsonRouter.java) consumes messages from the OdeRawEncodedSPATJson topic and pushes the SPAT message to the Asn1DecoderInput topic.
5. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SPAT, and pushes it to the Asn1DecoderOutput topic.
6. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and pushes the SPAT message to the OdeSpatJson topic.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeSpatJson topic, converts the SPAT and pushes it to the ProcessedOdeSpatJson topic.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedOdeSpatJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### MAP Data Flow 1: UDP receivers (ADM External)
1. The MAP comes in through the [MapReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/map/MapReceiver.java) and is pushed to the OdeRawEncodedMAPJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedMAPJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedMAPJsonRouter.java) processes messages from the OdeRawEncodedMAPJson topic.
3. The RawEncodedMAPJsonRouter class pushes the MAP to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the MAP, and pushes it to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the MAP to the OdeMapJson topic.
6. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdeMapJson topic, converts the MAP and pushes it to the ProcessedOdeMapJson topic.
7. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) consumes from the ProcessedOdeMapJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### MAP Data Flow 2: log import (ADM External)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the MAP log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for and processes new log files every one second.
3. [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java)  pushes the MAP to the OdeRawEncodedMAPJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedMAPJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedMAPJsonRouter.java) consumes messages from the OdeRawEncodedMAPJson topic and pushes the MAP to the Asn1DecoderInput topic.
5. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the MAP, and pushes it to the Asn1DecoderOutput topic.
6. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the MAP to the OdeMapJson topic.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdeMapJson topic, converts the MAP and pushes it to the ProcessedOdeMapJson topic.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) consumes from the ProcessedOdeMapJson topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### SRM Data Flow
1. The SRM comes in through the [SrmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/srm/SrmReceiver.java) and is pushed to the OdeRawEncodedSRMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSRMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSRMJsonRouter.java) processes messages from the OdeRawEncodedSRMJson topic and pushes the SRM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the SRM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the SRM to the OdeSrmJson topic.

### SSM Data Flow
1. The SSM comes in through the [SsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/ssm/SsmReceiver.java) class and is pushed to the OdeRawEncodedSSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSSMJsonRouter.java) consumes messages from the OdeRawEncodedSSMJson topic and pushes the SSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the SSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the SSM to the OdeSsmJson topic.

### PSM Data Flow
1. The PSM comes in through the [PsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/psm/PsmReceiver.java) class and is pushed to the OdeRawEncodedPSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedPSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedPSMJsonRouter.java) processes messages from the OdeRawEncodedPSMJson topic and pushes the PSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the PSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the PSM to the OdePsmJson topic.

### SDSM Data Flow
1. The SDSM comes in through the [SdsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/sdsm/SdsmReceiver.java) class and is pushed to the OdeRawEncodedSDSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSDSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSDSMJsonRouter.java) consumes messages from the OdeRawEncodedSDSMJson topic and pushes the SDSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the SDSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the SDSM to the OdeSdsmJson topic.

### RTCM Data Flow
1. The RTCM comes in through the [RtcmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/rtcm/RtcmReceiver.java) class and is pushed to the OdeRawEncodedRTCMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedRTCMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedRTCMJsonRouter.java) consumes messages from the OdeRawEncodedRTCMJson topic and pushes the RTCM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the RTCM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the RTCM to the OdeRtcmJson topic.

### RSM Data Flow
1. The RSM comes in through the [RsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/rsm/RsmReceiver.java) class and is pushed to the OdeRawEncodedRSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedRSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedRSMJsonRouter.java) consumes messages from the OdeRawEncodedRSMJson topic and pushes the RSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the RSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the RSM to the OdeRsmJson topic.

## FFM Data Flow Explanations

In `ffm` mode, the ODE decodes and encodes ASN.1 in process with FFMLib. The ACM steps are not used, so the `Asn1DecoderInput`, `Asn1DecoderOutput`, `Asn1EncoderInput`, and `Asn1EncoderOutput` topics are not involved in these flows. The `RawEncoded*JsonRouter` classes, [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java), and the `Asn1EncodedDataRouter` output listener are inactive for these flows. The downstream consumers of the `Ode*Json` topics, such as the PPM, the GeoJSON Converter, and the Conflict Monitor, are the same as in `external` mode.

### FFM Raw-topic path (path #2, default FFM route)
1. The UDP receiver classes publish each datagram to its `OdeRawEncoded*Json` topic through the [UdpIngestPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/UdpIngestPublisher.java). The raw record is the same as in `external` mode.
2. One [FfmlibRawJsonListener](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibRawJsonListener.java) per message type consumes that topic one record at a time. The listener shares its consumer group with the external `RawEncoded*JsonRouter` class for that type, so switching modes resumes from the committed offsets.
3. The [FfmlibDecodeService](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibDecodeService.java) decodes the UPER payload in process (UPER to JER to JSON).
4. The decoded JSON is published to the `Ode*Json` topic. The listener waits for Kafka to confirm the send before it commits the raw offset, so delivery is at least once.
5. A record that fails to decode is copied to the `dlq.` topic for its raw topic (for example, `dlq.OdeRawEncodedBSMJson`) with its original key and value and failure headers. The raw offset is committed after that write is confirmed. Operational native failures are quarantined the same way with `failure-category=infrastructure`.
6. The PPM, GeoJSON Converter, and Conflict Monitor consume the `Ode*Json` topics as in `external` mode.

### FFM Direct path (path #2)
Used when `ODE_FFM_UDP_ROUTING=direct`. No raw-topic consumers are started.
1. Each UDP receiver decodes its datagram on the receiver thread with [FfmlibDirectUdpDecoder](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibDirectUdpDecoder.java).
2. The decoded JSON is published to the `Ode*Json` topic without waiting for Kafka confirmation. A datagram whose send Kafka rejects is dropped and counted in `ode.ffmlib.direct.publish.failures`, so delivery is at most once.
3. When `ODE_FFM_RAW_PUBLISH=true` (the default), the receiver also publishes the raw record to the `OdeRawEncoded*Json` topic for downstream consumers such as jpo-deduplicator. ODE does not consume that topic in this route.
4. A decode failure is written without waiting to the `dlq.` topic with the raw encoded JSON as the value and `ingest-route=direct` in its headers. The record has no source partition or offset.

### FFM log import path (path #3)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the log file to the uploads directory, as in `external` mode.
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the directory every second and passes each file to the [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java).
3. Driver alerts are published to `OdeDriverAlertJson` as before. Every other record is decoded by the FfmlibDecodeService and published directly to its `Ode*Json` topic. It does not pass through a raw topic or the ACM.
4. If a record fails, the file moves to the failed-file path and its remaining records are not published. The records published before the failure remain visible.

### FFM TIM deposit path (path #1)
1. The [TimDepositController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/traveler/TimDepositController.java) encodes the TIM MessageFrame in process with the [FfmlibEncodeService](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibEncodeService.java) (XER to UPER). Encoder-input XML is not written to `Asn1EncoderInput`.
2. The TIM JSON is published to `OdeTimJson` after the initial encoding completes. The required TIM, certificate-expiration, filtered-TIM, and SDX Kafka sends are confirmed within a shared 10-second budget before the request returns success.
3. The [Asn1EncodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1EncodedDataRouter.java) processes the encoded output in process. It signs the TIM when signing is required and deposits it to the RSUs. It also writes the certificate-expiration record and, for TMC-generated TIMs, the `OdeTimJsonTMCFiltered` record.
4. For SDX deposits, the second (ASD) encoding also runs in process with the FfmlibEncodeService, and the SDX deposit goes to `SDWDepositorInput` as in `external` mode.
5. Native input errors return HTTP 400. Operational and publication failures return HTTP 500.

### FFM Data Flows by Message Type
Each type has its own listener, direct decoder path, raw topic, decoded topic, and dead-letter topic. Only the names differ between types. The offloaded-file (log import) path is drawn in the overview and in the BSM, MAP, SPAT and TIM diagrams. The PSM, SRM, SSM, SDSM, RTCM and RSM diagrams do not draw it, as in their `external` diagrams.

| Message type | Receiver | Raw topic | Decoded topic | Dead-letter topic | Diagram |
|---|---|---|---|---|---|
| BSM | BsmReceiver | OdeRawEncodedBSMJson | OdeBsmJson | dlq.OdeRawEncodedBSMJson | [BSM Data Flow](bsm/BSM%20Data%20Flow.drawio.png) |
| TIM | TimReceiver | OdeRawEncodedTIMJson | OdeTimJson | dlq.OdeRawEncodedTIMJson | [TIM Data Flow](tim/TIM%20Data%20Flow.drawio.png) |
| SPAT | SpatReceiver | OdeRawEncodedSPATJson | OdeSpatJson | dlq.OdeRawEncodedSPATJson | [SPAT Data Flow](spat/SPAT%20Data%20Flow.drawio.png) |
| MAP | MapReceiver | OdeRawEncodedMAPJson | OdeMapJson | dlq.OdeRawEncodedMAPJson | [MAP Data Flow](map/MAP%20Data%20Flow.drawio.png) |
| SRM | SrmReceiver | OdeRawEncodedSRMJson | OdeSrmJson | dlq.OdeRawEncodedSRMJson | [SRM Data Flow](srm/SRM%20Data%20Flow.drawio.png) |
| SSM | SsmReceiver | OdeRawEncodedSSMJson | OdeSsmJson | dlq.OdeRawEncodedSSMJson | [SSM Data Flow](ssm/SSM%20Data%20Flow.drawio.png) |
| PSM | PsmReceiver | OdeRawEncodedPSMJson | OdePsmJson | dlq.OdeRawEncodedPSMJson | [PSM Data Flow](psm/PSM%20Data%20Flow.drawio.png) |
| SDSM | SdsmReceiver | OdeRawEncodedSDSMJson | OdeSdsmJson | dlq.OdeRawEncodedSDSMJson | [SDSM Data Flow](sdsm/SDSM%20Data%20Flow.drawio.png) |
| RTCM | RtcmReceiver | OdeRawEncodedRTCMJson | OdeRtcmJson | dlq.OdeRawEncodedRTCMJson | [RTCM Data Flow](rtcm/RTCM%20Data%20Flow.drawio.png) |
| RSM | RsmReceiver | OdeRawEncodedRSMJson | OdeRsmJson | dlq.OdeRawEncodedRSMJson | [RSM Data Flow](rsm/RSM%20Data%20Flow.drawio.png) |

### FFM Delivery and Failures
- The raw-topic route delivers at least once, and the direct route delivers at most once. If the ODE stops after the decoded output is confirmed but before the raw offset is committed, the record is decoded and published again on restart.
- The `dlq.` topics keep records for seven days by default. ODE does not replay them. Republish a record to its raw topic deliberately after you investigate it.
- A native failure that is not caused by the input (for example, a buffer that is too small or a missing codec) is quarantined with `failure-category=infrastructure`. After `ODE_FFM_INFRA_HALT_THRESHOLD` consecutive failures (default 3), that type's listener stops and reports unhealthy. The direct route reports DOWN for that type but keeps receiving datagrams.
- To roll back, set `ODE_ASN1_CODEC_MODE=external`. The external routers resume from the same raw-topic consumer groups. When you switch from direct routing with raw publishing back to raw-topic or external routing, reset those groups to the latest offsets, or wait for the raw topic retention to expire, to avoid decoding the retained raw records a second time.

For the full FFM settings, see the [User Guide](../UserGuide.md).
