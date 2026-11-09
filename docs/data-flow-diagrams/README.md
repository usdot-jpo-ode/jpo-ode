# Data Flow Diagrams
The purpose of these diagrams is to show:
- how data flows through the Operation Data Environment (both in an overall sense and for each message type)
- how the ODE interacts with kafka topics as well as its submodules.

## Diagrams

| Diagram | Shows |
|---|---|
| [ODE Decoding Data Flow](ODE%20Decoding%20Data%20Flow.drawio.png) | UDP ingestion and log import, ASN.1 decoding and JSON processing for every message type. `<T>` stands for BSM, MAP, SPAT, TIM, SRM, SSM, PSM, SDSM, RTCM or RSM. |
| [ODE Encoding Data Flow](ODE%20Encoding%20Data%20Flow.drawio.png) | TIM deposit, ASN.1 encoding, signing and delivery to the RSUs and the SDX. |
| Message type diagrams | The decoding flow for one message type, with its exact topic names. The TIM diagram also includes the encoding flow. See [FFM Data Flows by Message Type](#ffm-data-flows-by-message-type) for the links. |

Every diagram draws the `external` and `ffm` codec modes on the same layout. Colored paths are alternatives selected by configuration. They do not run at the same time.

## Key Explanation

Boxes:
- Blue boxes are ODE Java classes. All Java classes are in `jpo-ode-svcs`.
- Teal boxes are FFM classes, used only in `ffm` mode.
- Gold ovals are kafka topics or groups of topics.
- Red ovals are dead-letter (`dlq.`) topics.
- Green boxes are separate modules or containers, such as the ACM, the PPM, the GeoJSON Converter and the Conflict Monitor.
- Gray boxes are sources and destinations outside the ODE, such as offloaded files, `REST /tim`, the RSUs and the SDX.

The color of a connection shows the mode that uses it:
- Gray: shared by all modes, or by the scope named on the connection.
- Blue: `external` mode (`ODE_ASN1_CODEC_MODE=external`, the default).
- Orange: both FFM routing settings (`ODE_ASN1_CODEC_MODE=ffm`).
- Teal: FFM raw-topic UDP routing (`ODE_FFM_UDP_ROUTING=raw-topic`, the FFM default).
- Purple: FFM direct UDP routing (`ODE_FFM_UDP_ROUTING=direct`).
- Red: an FFM UDP decode failure. The arrow leads to the dead-letter topic.

The line style of a connection shows how the data moves:
- Solid: an in-process call or delivery.
- Dashed: a kafka topic.
- Dotted: an optional publication.

The beginning of an arrow is where the data flows from, and the end of the arrow is where it flows to.

## Codec Modes

The ODE selects its ASN.1 codec with `ODE_ASN1_CODEC_MODE`:

- `external` (default): the [asn1_codec](https://github.com/usdot-jpo-ode/asn1_codec) ACM decodes and encodes messages through the `Asn1Decoder*` and `Asn1Encoder*` topics. The diagrams draw this mode in blue, and the [Data Flow Explanations](#data-flow-explanations) describe it.
- `ffm`: the FFMLib codec runs in the ODE. Decoding happens in the ODE itself, and TIM encoding happens in `FfmlibEncodeService`. The diagrams draw this mode in orange, teal and purple, and the [FFM Data Flow Explanations](#ffm-data-flow-explanations) describe it.

In `ffm` mode, `ODE_FFM_UDP_ROUTING` selects how UDP datagrams are decoded. TIM encoding is the same for both settings.

| Route | Setting | Delivery | Diagram |
|---|---|---|---|
| Raw topic (FFM default) | `ODE_FFM_UDP_ROUTING=raw-topic` | At least once. The raw offset is committed after the decoded JSON is confirmed. | [ODE Decoding Data Flow](ODE%20Decoding%20Data%20Flow.drawio.png) (teal) |
| Direct | `ODE_FFM_UDP_ROUTING=direct` | At most once. The datagram is decoded on its UDP receiver thread. | [ODE Decoding Data Flow](ODE%20Decoding%20Data%20Flow.drawio.png) (purple) |

## Decoding Paths

Every message is decoded by one of the paths below. `ODE_ASN1_CODEC_MODE` chooses ADM External or FFM. In FFM mode, `ODE_FFM_UDP_ROUTING` chooses how UDP datagrams are decoded. The [ODE Decoding Data Flow](ODE%20Decoding%20Data%20Flow.drawio.png) diagram shows each path in its own color.

| Path | Settings | Decoder | Runs in | Delivery |
|---|---|---|---|---|
| ADM External (default) | `ODE_ASN1_CODEC_MODE=external` | asn1_codec ACM (ADM), through the `Asn1DecoderInput` and `Asn1DecoderOutput` topics | External ACM process | Kafka consumer offsets. ODE writes no dead-letter records on this path. |
| FFM Raw-topic | `ODE_ASN1_CODEC_MODE=ffm`, `ODE_FFM_UDP_ROUTING=raw-topic` (default) | `FfmlibDecodeService`, called by `FfmlibRawJsonListener` | ODE process | At least once. The raw offset is committed after the decoded JSON is confirmed. |
| FFM Direct | `ODE_ASN1_CODEC_MODE=ffm`, `ODE_FFM_UDP_ROUTING=direct` | `FfmlibDecodeService`, called by `FfmlibDirectUdpDecoder` | UDP receiver thread | At most once. Nothing waits for Kafka confirmation. |
| FFM log import | `ODE_ASN1_CODEC_MODE=ffm` | `FfmlibDecodeService`, called by `LogFileToAsn1CodecPublisher` | ODE process | A failed record moves the whole file to the failed-file path. |
| ADM External log import | `ODE_ASN1_CODEC_MODE=external` | asn1_codec ACM, through the raw topic | External ACM process | As for the ADM External receiver path. |

All three FFM paths publish the decoded JSON through `FfmlibOutputPublisher`.

### ADM External (default)
Used by the UDP receivers, log import and the TIM deposit in `external` mode.
1. UDP receivers publish each datagram to its `OdeRawEncoded*Json` topic through `UdpIngestPublisher`. The raw record is the same in every mode.
2. The `RawEncoded*JsonRouter` for that type consumes the raw record, removes the transport headers and any remaining signed IEEE 1609.2 header, and sends the record to `Asn1DecoderInput`.
3. The ACM (ADM) decodes the UPER payload and writes the result to `Asn1DecoderOutput`.
4. `Asn1DecodedDataRouter` reads `Asn1DecoderOutput` and publishes the decoded JSON to `Ode*Json`.
5. The PPM and GeoJSON Converter consume `Ode*Json`, and the Conflict Monitor consumes the GeoJSON Converter's processed topics, as in every mode.

Each message type has its own router, so one type's backlog does not hold up another type.

### FFM Raw-topic
Used by the UDP receivers in `ffm` mode with `ODE_FFM_UDP_ROUTING=raw-topic`, which is the default.
1. The receivers publish to the raw topic exactly as in ADM External.
2. One `FfmlibRawJsonListener` per message type consumes that topic, one record at a time. It shares its consumer group with the external router for that type, so switching modes resumes from the committed offsets.
3. The listener parses the raw record and calls `FfmlibDecodeService`, which decodes the UPER payload in process (UPER to JER to JSON).
4. The listener publishes the JSON to `Ode*Json` through `FfmlibOutputPublisher` and waits for Kafka to confirm the send. It allows at most three sends within a 30-second budget and commits the raw offset only after the confirmation.
5. The listener copies malformed, unsupported and serialization failures to `dlq.<raw topic>` (for example, `dlq.OdeRawEncodedBSMJson`) with the original key and value and failure headers. The raw offset is committed once that write is confirmed.
6. Operational native failures (buffer capacity, output encoding, missing codec) are quarantined with `failure-category=infrastructure`. After `ODE_FFM_INFRA_HALT_THRESHOLD` consecutive failures (default 3), that type's listener stops and reports unhealthy.
7. The downstream consumers of `Ode*Json` are unchanged.

### FFM Direct
Used by the UDP receivers in `ffm` mode with `ODE_FFM_UDP_ROUTING=direct`. No raw-topic consumers are started.
1. Each receiver port has its own decode thread. `FfmlibDirectUdpDecoder` calls `FfmlibDecodeService` on the datagram before anything reaches Kafka.
2. `FfmlibDirectUdpDecoder` publishes the decoded JSON to `Ode*Json` through `FfmlibOutputPublisher` without waiting for confirmation. A send that Kafka rejects is dropped and counted in `ode.ffmlib.direct.publish.failures`.
3. With `ODE_FFM_RAW_PUBLISH=true` (the default), the receiver also publishes the raw record to `OdeRawEncoded*Json`. ODE does not consume that topic on this path. Downstream consumers, such as jpo-deduplicator, may.
4. `FfmlibDirectUdpDecoder` writes a decode failure to `dlq.<raw topic>` without waiting, with `ingest-route=direct`. There is no source partition or offset. After the threshold of operational failures, health reports DOWN for that type until it decodes again, but the receivers keep running, because a datagram cannot be replayed.

### FFM log import
Used for offloaded files in `ffm` mode. All ten message types can be imported.
1. `FileUploadController` writes the log file. `ImporterDirectoryWatcher` scans the directory every second and hands each file to `LogFileToAsn1CodecPublisher`.
2. Driver alerts still go to `OdeDriverAlertJson`.
3. Every other record goes straight to `FfmlibDecodeService`, which decodes it and publishes it to `Ode*Json` through `FfmlibOutputPublisher`. It does not pass through a raw topic or the ACM.
4. If a record fails, the file moves to the failed-file path and its remaining records are not published. Records published before the failure stay visible. Reimporting the whole file publishes that prefix again.

### ADM External log import
Used for offloaded files in `external` mode.
1. The same file upload and watcher steps as FFM log import.
2. `LogFileToAsn1CodecPublisher` publishes each record to its raw topic.
3. From there the path is the ADM External receiver path above: the router, then the ACM (ADM), then `Ode*Json`.

## Encoding Paths

TIMs deposited through `POST` and `PUT /tim` are encoded by one of the paths below. Both FFM routing settings use the same FFM path. The [ODE Encoding Data Flow](ODE%20Encoding%20Data%20Flow.drawio.png) diagram shows both paths.

### FFM TIM deposit
Used by `POST` and `PUT /tim` in `ffm` mode.
1. `TimDepositController` prepares the TIM and encodes its MessageFrame in process with `FfmlibEncodeService` (XER to UPER). Nothing is written to `Asn1EncoderInput`.
2. Once the initial encoding completes, `TimDepositController` publishes the TIM JSON to `OdeTimJson` and passes the encoded output and the TIM JSON straight to `Asn1EncodedDataRouter`. Nothing is written to `Asn1EncoderOutput`.
3. The router signs the TIM through jpo-security-svcs when signing is enabled, deposits it to the RSUs, and writes the certificate-expiration record for signed TIMs. For TMC-generated TIMs it writes `OdeTimJsonTMCFiltered` from the TIM JSON it was given.
4. For SDX deposits, the router runs the second (ASD) encoding in process with `FfmlibEncodeService` and then publishes the result to `SDWDepositorInput`.
5. The required TIM, certificate-expiration, filtered-TIM and SDX sends are confirmed within a shared 10-second budget before the request returns success.
6. Native input errors return HTTP 400. Operational and publication failures return HTTP 500.

### ADM External TIM deposit
Used for `POST` and `PUT /tim` in `external` mode.
1. `TimDepositController` publishes the TIM JSON to `OdeTimJson` and writes the encoder input XML to `Asn1EncoderInput`.
2. The ACM (AEM) encodes the input and writes the result to `Asn1EncoderOutput`, which `Asn1EncodedDataRouter` consumes.
3. The router handles signing, RSU deposits and the certificate-expiration record as in FFM mode. For TMC-generated TIMs it reads the TIM JSON from the TIM KTable to write `OdeTimJsonTMCFiltered`.
4. For SDX deposits, the second (ASD) encoding goes back through `Asn1EncoderInput` and the AEM. The router then publishes the result to `SDWDepositorInput`.

### TIM KTable
[OdeTimJsonTopology](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/OdeTimJsonTopology.java) reads `OdeTimJson` and keeps only the TIMs whose `recordGeneratedBy` is `TMC`. It writes them to `OdeTimKTableJson` and stores them in the `timjson-ktable-store` KTable, keyed by stream ID. The filter applies to every TIM on `OdeTimJson`, including decoded and imported TIMs; only those with `recordGeneratedBy` `TMC` are stored.

The store is used only to build the `OdeTimJsonTMCFiltered` record:
- In `external` mode, `Asn1EncodedDataRouter` reads the TIM JSON from the store and removes the entry.
- In `ffm` mode, the router already has the TIM JSON from `TimDepositController`, so it only removes the entry from the store.

## Data Flow Explanations

These flows describe the default `external` codec mode. The `ffm` mode flows are in [FFM Data Flow Explanations](#ffm-data-flow-explanations).

### Encoding Data Flow: TIM deposit (ADM External)
See the [ODE Encoding Data Flow](ODE%20Encoding%20Data%20Flow.drawio.png) diagram.
1. Messages come in through the [TimDepositController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/traveler/TimDepositController.java) class and are pushed to the OdeTimJson and Asn1EncoderInput topics.
2. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1EncoderInput topic and pushes encoded messages to the Asn1EncoderOutput topic.
3. The [Asn1EncodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1EncodedDataRouter.java) class pulls from the Asn1EncoderOutput topic and routes the message.
   1. If the message is not signed and signing is enabled (`DATA_SIGNING_ENABLED_RSU` or `DATA_SIGNING_ENABLED_SDW`), it is sent to the [jpo-security-svcs](/jpo-security-svcs/jpo-security-svcs/src/main/java/us/dot/its/jpo/sec/controllers/SignatureController.java) REST API to be signed. The signed message's expiration is written to the OdeTIMCertExpirationTimeJson topic.
   2. If the message is meant for the RSU, it is passed to the [RsuDepositor](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/rsu/RsuDepositor.java) class, which sends the message to the RSUs.
   3. If the TIM was generated by the TMC, the router reads its TIM JSON from the [TIM KTable](#tim-ktable) and writes it to the OdeTimJsonTMCFiltered topic.
   4. If the message is meant for the SDX and has not been double-encoded, it is packaged into an ASD and sent back to the Asn1EncoderInput topic for its second encoding.
   5. If the message is meant for the SDX and has been double-encoded, it is pushed to the SDWDepositorInput topic, pulled into the [SDWDepositor](https://github.com/usdot-jpo-ode/jpo-sdw-depositor) and sent to the SDX.

### Decoding Data Flow: UDP receivers (ADM External)
See the [ODE Decoding Data Flow](ODE%20Decoding%20Data%20Flow.drawio.png) diagram.
1. Messages come in through the [UDP Receivers](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp) and are pushed to the `OdeRawEncoded<T>Json` topics by the [UdpIngestPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/UdpIngestPublisher.java).
2. The `RawEncoded<T>JsonRouter` classes under [kafka/listeners/json](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json) process these raw encoded messages.
3. These classes push the message to the Asn1DecoderInput topic.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from that topic and pushes decoded messages to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and deposits messages into the `Ode<T>Json` topics.
6. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the `Ode<T>Json` topics and pushes filtered messages to the filtered JSON topics.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the `Ode<T>Json` topics, converts the messages and pushes them to the processed topics, such as ProcessedMap and ProcessedSpat.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the processed topics and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### Decoding Data Flow: log import (ADM External)
See the [ODE Decoding Data Flow](ODE%20Decoding%20Data%20Flow.drawio.png) diagram.
1. Messages are offloaded onto a directory referenced by the [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java).
2. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
3. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for new log files every second and processes them.
   1. If the message is a DriverAlert, the [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) pushes it to the OdeDriverAlertJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
   2. Every other message (BSM, MAP, SPAT, TIM, SRM, SSM, PSM, SDSM, RTCM or RSM) is pushed by the [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) class to the corresponding `OdeRawEncoded<T>Json` topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
   3. The relevant router under [kafka/listeners/json](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json)
      pulls from the `OdeRawEncoded<T>Json` topic and processes the data. This class then writes to the Asn1DecoderInput topic.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from that topic and pushes decoded messages to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and deposits messages into the `Ode<T>Json` topics.
6. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) and [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) process the `Ode<T>Json` topics, and the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) processes the GeoJSON Converter output, as in the UDP receiver flow.

### BSM Data Flow 1: UDP receivers (ADM External)
1. The BSM comes in through the [BsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/bsm/BsmReceiver.java) class and is pushed to the OdeRawEncodedBSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) processes messages from the OdeRawEncodedBSMJson topic
3. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) pushes the BSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic and pushes the decoded BSM to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and
   pushes the BSM to the OdeBsmJson topic in JSON format.
6. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeBsmJson topic and pushes the filtered BSM to the FilteredOdeBsmJson topic.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeBsmJson topic, converts the BSM and pushes it to the ProcessedBsm topic.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedBsm topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### BSM Data Flow 2: log import (ADM External)
1. The BSM is offloaded onto a directory referenced by the FileUploadController class.
2. The FileUploadController class indirectly invokes the LogFileToAsn1CodecPublisher class, which handles the offloaded message.
3. The LogFileToAsn1CodecPublisher class pushes the BSM to the OdeRawEncodedBSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) processes messages from the OdeRawEncodedBSMJson topic
5. The [RawEncodedBSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedBSMJsonRouter.java) class pushes the BSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
6. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic and pushes the decoded BSM to the Asn1DecoderOutput topic.
7. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and
   pushes the BSM to the OdeBsmJson topic in JSON format.
8. The [PPM](https://github.com/usdot-jpo-ode/jpo-cvdp) pulls from the OdeBsmJson topic and pushes the filtered BSM to the FilteredOdeBsmJson topic.
9. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeBsmJson topic, converts the BSM and pushes it to the ProcessedBsm topic.
10. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedBsm topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### TIM Data Flow 1: TIM deposit (ADM External)
See [Encoding Data Flow: TIM deposit (ADM External)](#encoding-data-flow-tim-deposit-adm-external) and [TIM KTable](#tim-ktable).

### TIM Data Flow 2: UDP receivers (ADM External)
1. The TIM comes in through the [TimReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/tim/TimReceiver.java) class and is pushed to the OdeRawEncodedTIMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedTIMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) processes messages from the OdeRawEncodedTIMJson topic
3. The [RawEncodedTIMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) class pushes the TIM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the TIM, and pushes it to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) pulls from the Asn1DecoderOutput topic and pushes the TIM to the OdeTimJson topic.

### TIM Data Flow 3: log import (ADM External)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for new log files every second and processes them.
3. The [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) pushes the TIM to the OdeRawEncodedTIMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedTIMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedTIMJsonRouter.java) processes messages from the OdeRawEncodedTIMJson topic and pushes the TIM to the Asn1DecoderInput topic.
5. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the TIM, and pushes it to the Asn1DecoderOutput topic.
6. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) pulls from the Asn1DecoderOutput topic and pushes the TIM to the OdeTimJson topic.

### SPAT Data Flow 1: UDP receivers (ADM External)
1. The SPAT comes in through the [SpatReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/spat/SpatReceiver.java) class and is pushed to the OdeRawEncodedSPATJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSPATJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSPATJsonRouter.java) processes messages from the OdeRawEncodedSPATJson topic.
3. The [RawEncodedSPATJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSPATJsonRouter.java) class pushes the SPAT to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SPAT, and pushes it to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and
   pushes the SPAT to the OdeSpatJson topic.
6. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeSpatJson topic, converts the SPAT and pushes it to the ProcessedSpat topic.
7. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedSpat topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### SPAT Data Flow 2: log import (ADM External)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the SPAT log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for new log files every second and processes them.
3. The [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) pushes the SPAT to the OdeRawEncodedSPATJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedSPATJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSPATJsonRouter.java) consumes messages from the OdeRawEncodedSPATJson topic and pushes the SPAT message to the Asn1DecoderInput topic.
5. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the SPAT, and pushes it to the Asn1DecoderOutput topic.
6. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) class pulls from the Asn1DecoderOutput topic and pushes the SPAT message to the OdeSpatJson topic.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) pulls from the OdeSpatJson topic, converts the SPAT and pushes it to the ProcessedSpat topic.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedSpat topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### MAP Data Flow 1: UDP receivers (ADM External)
1. The MAP comes in through the [MapReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/map/MapReceiver.java) and is pushed to the OdeRawEncodedMAPJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedMAPJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedMAPJsonRouter.java) processes messages from the OdeRawEncodedMAPJson topic.
3. The RawEncodedMAPJsonRouter class pushes the MAP to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
4. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) pulls from the Asn1DecoderInput topic, decodes the MAP, and pushes it to the Asn1DecoderOutput topic.
5. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the MAP to the OdeMapJson topic.
6. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdeMapJson topic, converts the MAP and pushes it to the ProcessedMap topic.
7. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) consumes from the ProcessedMap topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### MAP Data Flow 2: log import (ADM External)
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the MAP log file to the configured uploads directory defined in [FileImporterProperties](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/FileImporterProperties.java) (ex: /uploads/bsmlog)
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the upload directory for new log files every second and processes them.
3. [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java) pushes the MAP to the OdeRawEncodedMAPJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
4. The [RawEncodedMAPJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedMAPJsonRouter.java) consumes messages from the OdeRawEncodedMAPJson topic and pushes the MAP to the Asn1DecoderInput topic.
5. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the MAP, and pushes it to the Asn1DecoderOutput topic.
6. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the MAP to the OdeMapJson topic.
7. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdeMapJson topic, converts the MAP and pushes it to the ProcessedMap topic.
8. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) consumes from the ProcessedMap topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

### SRM Data Flow
1. The SRM comes in through the [SrmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/srm/SrmReceiver.java) and is pushed to the OdeRawEncodedSRMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSRMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSRMJsonRouter.java) processes messages from the OdeRawEncodedSRMJson topic and pushes the SRM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the SRM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the SRM to the OdeSrmJson topic.
5. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdeSrmJson topic, converts the SRM and pushes it to the ProcessedSrm topic.
6. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedSrm topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

SRM log files are imported as described in [Decoding Data Flow: log import (ADM External)](#decoding-data-flow-log-import-adm-external). The record goes to the OdeRawEncodedSRMJson topic and follows steps 2 to 6 above.

### SSM Data Flow
1. The SSM comes in through the [SsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/ssm/SsmReceiver.java) class and is pushed to the OdeRawEncodedSSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSSMJsonRouter.java) consumes messages from the OdeRawEncodedSSMJson topic and pushes the SSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the SSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the SSM to the OdeSsmJson topic.
5. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdeSsmJson topic, converts the SSM and pushes it to the ProcessedSsm topic.
6. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedSsm topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

SSM log files are imported as described in [Decoding Data Flow: log import (ADM External)](#decoding-data-flow-log-import-adm-external). The record goes to the OdeRawEncodedSSMJson topic and follows steps 2 to 6 above.

### PSM Data Flow
1. The PSM comes in through the [PsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/psm/PsmReceiver.java) class and is pushed to the OdeRawEncodedPSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedPSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedPSMJsonRouter.java) processes messages from the OdeRawEncodedPSMJson topic and pushes the PSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the PSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the PSM to the OdePsmJson topic.
5. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdePsmJson topic, converts the PSM and pushes it to the ProcessedPsm topic.

PSM log files are imported as described in [Decoding Data Flow: log import (ADM External)](#decoding-data-flow-log-import-adm-external). The record goes to the OdeRawEncodedPSMJson topic and follows steps 2 to 5 above.

### SDSM Data Flow
1. The SDSM comes in through the [SdsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/sdsm/SdsmReceiver.java) class and is pushed to the OdeRawEncodedSDSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedSDSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedSDSMJsonRouter.java) consumes messages from the OdeRawEncodedSDSMJson topic and pushes the SDSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the SDSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the SDSM to the OdeSdsmJson topic.

SDSM log files are imported as described in [Decoding Data Flow: log import (ADM External)](#decoding-data-flow-log-import-adm-external). The record goes to the OdeRawEncodedSDSMJson topic and follows steps 2 to 4 above.

### RTCM Data Flow
1. The RTCM comes in through the [RtcmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/rtcm/RtcmReceiver.java) class and is pushed to the OdeRawEncodedRTCMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedRTCMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedRTCMJsonRouter.java) consumes messages from the OdeRawEncodedRTCMJson topic and pushes the RTCM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the RTCM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the RTCM to the OdeRtcmJson topic.
5. The [GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter) consumes from the OdeRtcmJson topic, converts the RTCM and pushes it to the ProcessedRtcm topic.
6. The [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) pulls from the ProcessedRtcm topic and pushes to the [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor) Output Topics group.

RTCM log files are imported as described in [Decoding Data Flow: log import (ADM External)](#decoding-data-flow-log-import-adm-external). The record goes to the OdeRawEncodedRTCMJson topic and follows steps 2 to 6 above.

### RSM Data Flow
1. The RSM comes in through the [RsmReceiver](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/rsm/RsmReceiver.java) class and is pushed to the OdeRawEncodedRSMJson topic. Any IEEE 1609.3 or unsigned IEEE 1609.2 headers are stripped at this point.
2. The [RawEncodedRSMJsonRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/json/RawEncodedRSMJsonRouter.java) consumes messages from the OdeRawEncodedRSMJson topic and pushes the RSM to the Asn1DecoderInput topic. Any remaining signed IEEE 1609.2 headers are removed at this point.
3. The [ACM](https://github.com/usdot-jpo-ode/asn1_codec) consumes from the Asn1DecoderInput topic, decodes the RSM, and pushes it to the Asn1DecoderOutput topic.
4. The [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java) consumes from the Asn1DecoderOutput topic and pushes the RSM to the OdeRsmJson topic.

RSM log files are imported as described in [Decoding Data Flow: log import (ADM External)](#decoding-data-flow-log-import-adm-external). The record goes to the OdeRawEncodedRSMJson topic and follows steps 2 to 4 above.

## FFM Data Flow Explanations

In `ffm` mode, the ODE decodes and encodes ASN.1 in process with FFMLib. The ACM steps are not used, so the `Asn1DecoderInput`, `Asn1DecoderOutput`, `Asn1EncoderInput`, and `Asn1EncoderOutput` topics are not involved in these flows. The `RawEncoded*JsonRouter` classes, [Asn1DecodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1DecodedDataRouter.java), and the `Asn1EncodedDataRouter` Kafka listener are inactive for these flows. `Asn1EncodedDataRouter` still routes deposited TIMs, called directly by `TimDepositController`. The downstream consumers of the `Ode*Json` topics, such as the PPM, the GeoJSON Converter, and the Conflict Monitor, are the same as in `external` mode.

### FFM Raw-topic decoding (default FFM route)
Drawn in teal in the [ODE Decoding Data Flow](ODE%20Decoding%20Data%20Flow.drawio.png) diagram.
1. The UDP receiver classes publish each datagram to its `OdeRawEncoded*Json` topic through the [UdpIngestPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/udp/UdpIngestPublisher.java). The raw record is the same as in `external` mode.
2. One [FfmlibRawJsonListener](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibRawJsonListener.java) per message type consumes that topic one record at a time. The listener shares its consumer group with the external `RawEncoded*JsonRouter` class for that type, so switching modes resumes from the committed offsets.
3. The listener calls the [FfmlibDecodeService](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibDecodeService.java), which decodes the UPER payload in process (UPER to JER to JSON).
4. The listener publishes the decoded JSON to the `Ode*Json` topic through the [FfmlibOutputPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibOutputPublisher.java). It waits for Kafka to confirm the send before it commits the raw offset, so delivery is at least once.
5. If a record fails to decode, the listener copies it to the `dlq.` topic for its raw topic (for example, `dlq.OdeRawEncodedBSMJson`) with its original key and value and failure headers. The raw offset is committed after that write is confirmed. Operational native failures are quarantined the same way with `failure-category=infrastructure`.
6. The PPM and GeoJSON Converter consume the `Ode*Json` topics, and the Conflict Monitor consumes the processed topics, as in `external` mode.

### FFM Direct decoding
Used when `ODE_FFM_UDP_ROUTING=direct`. No raw-topic consumers are started. Drawn in purple in the [ODE Decoding Data Flow](ODE%20Decoding%20Data%20Flow.drawio.png) diagram.
1. Each UDP receiver hands its datagram to the [FfmlibDirectUdpDecoder](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibDirectUdpDecoder.java), which runs on the receiver thread and calls the [FfmlibDecodeService](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibDecodeService.java) to decode it.
2. The direct decoder publishes the decoded JSON to the `Ode*Json` topic through the [FfmlibOutputPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibOutputPublisher.java) without waiting for Kafka confirmation. A datagram whose send Kafka rejects is dropped and counted in `ode.ffmlib.direct.publish.failures`, so delivery is at most once.
3. When `ODE_FFM_RAW_PUBLISH=true` (the default), the receiver also publishes the raw record to the `OdeRawEncoded*Json` topic for downstream consumers such as jpo-deduplicator. This is the dotted purple branch in the diagrams. ODE does not consume that topic in this route.
4. The direct decoder writes a decode failure without waiting to the `dlq.` topic, with the raw encoded JSON as the value and `ingest-route=direct` in its headers. The record has no source partition or offset.

### FFM log import decoding
Drawn in orange in the [ODE Decoding Data Flow](ODE%20Decoding%20Data%20Flow.drawio.png) diagram.
1. The [FileUploadController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/upload/FileUploadController.java) writes the log file to the uploads directory, as in `external` mode.
2. The [ImporterDirectoryWatcher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/importer/ImporterDirectoryWatcher.java) scans the directory every second and passes each file to the [LogFileToAsn1CodecPublisher](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/coder/stream/LogFileToAsn1CodecPublisher.java).
3. Driver alerts are published to `OdeDriverAlertJson` as before. Every other record is decoded by the FfmlibDecodeService and published directly to its `Ode*Json` topic through the FfmlibOutputPublisher. It does not pass through a raw topic or the ACM.
4. If a record fails, the file moves to the failed-file path and its remaining records are not published. The records published before the failure remain visible.

### FFM TIM deposit encoding
Drawn in orange in the [ODE Encoding Data Flow](ODE%20Encoding%20Data%20Flow.drawio.png) diagram. Both FFM routing settings use this path.
1. The [TimDepositController](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/traveler/TimDepositController.java) encodes the TIM MessageFrame in process with the [FfmlibEncodeService](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/codec/ffmlib/FfmlibEncodeService.java) (XER to UPER). Encoder-input XML is not written to `Asn1EncoderInput`.
2. After the initial encoding completes, the controller publishes the TIM JSON to `OdeTimJson` and calls the [Asn1EncodedDataRouter](/jpo-ode-svcs/src/main/java/us/dot/its/jpo/ode/kafka/listeners/asn1/Asn1EncodedDataRouter.java) directly with the encoded output and the TIM JSON. Nothing goes through `Asn1EncoderOutput`.
3. The router signs the TIM through jpo-security-svcs when signing is enabled and deposits it to the RSUs. It writes the certificate-expiration record for signed TIMs. For TMC-generated TIMs, it writes the `OdeTimJsonTMCFiltered` record from the TIM JSON it was given and removes the entry from the [TIM KTable](#tim-ktable).
4. For SDX deposits, the router packages the TIM into an ASD, runs the second encoding in process with the FfmlibEncodeService, and publishes the result to `SDWDepositorInput` as in `external` mode.
5. The required TIM, certificate-expiration, filtered-TIM, and SDX Kafka sends are confirmed within a shared 10-second budget before the request returns success.
6. Native input errors return HTTP 400. Operational and publication failures return HTTP 500.

### FFM Data Flows by Message Type
Each type has its own listener, direct decoder path, raw topic, decoded topic, and dead-letter topic. Only the names differ between types. Every message type diagram draws the UDP receiver paths and the offloaded-file (log import) path.

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
