# Importing S3 Archive Files

The ODE can import the files written by the jpo-utils S3 archive connectors, so that archived data can be decoded and analyzed again, for example in a local ODE with MongoDB. See [S3_Archive.md](../jpo-utils/docs/S3_Archive.md) in jpo-utils for the connectors and the archive layout.

**Table of Contents**

- [1. Supported Files](#1-supported-files)
- [2. Importing Files](#2-importing-files)
- [3. Local Analysis Stack](#3-local-analysis-stack)
- [4. Configuration](#4-configuration)
- [5. Caveats](#5-caveats)

## 1. Supported Files

Archive objects are named `<topic>+<partition>+<startOffset>.json.gz` and hold one JSON record per line. The importer reads the topic from the file name and handles the records as follows:

| Archived topic | Record format | Import |
|---|---|---|
| `topic.OdeRawEncoded<TYPE>Json` (`sink.S3Archive`) | Raw encoded record | Published unchanged to the same raw encoded topic. The ODE decodes it. |
| `topic.Ode<Type>Json` under `<environment>/compact/` (`sink.S3RawArchive`) | Compact `{"odeReceivedAt", "originIp", "asn1"}` | Converted into a raw encoded record and published to `topic.OdeRawEncoded<TYPE>Json`. The ODE decodes it. |
| `topic.Ode<Type>Json` (`sink.S3Archive`) | Full decoded record | Published unchanged to the same decoded topic, with no decoding. With `ODE_ARCHIVE_IMPORTER_REDECODE_FULL_RECORDS=true`, decoded again from `metadata.asn1` instead. |
| `topic.OdeDriverAlertJson`, `topic.Filtered*`, `topic.OdeTimJsonTMCFiltered`, `topic.OdeTIMCertExpirationTimeJson` | Full record | Published unchanged to the same topic. |
| Any other topic, such as `topic.Processed*` or `topic.Cm*` | | Not supported. The file is moved to the failed directory. |

`<Type>` is one of the ten message types: BSM, TIM, MAP, SPaT, SRM, SSM, PSM, SDSM, RTCM and RSM.

**What is kept:** in all cases the records keep the archived `odeReceivedAt` and `originIp`.
- **Compact records:** they are converted with the same code as the UDP receivers, so the decoded records have the same `recordType`, `source`, `recordGeneratedBy` and `securityResultCode` as records received over UDP.
- **Re-decoded full records:** they keep their archived `recordType`, `source` and `recordGeneratedBy`.

**Files that were renamed:** a file whose name does not follow the archive naming can only hold compact records. The message type of each record is then detected from the start flags in its `asn1`, as the generic UDP receiver does.

**Compression:** gzip compressed and uncompressed files are both accepted.

[Back to top](#importing-s3-archive-files)

## 2. Importing Files

### Through a mounted volume

The ODE container mounts `${DOCKER_SHARED_VOLUME}/uploads` at `/home/uploads`, so the archive inbox is `${DOCKER_SHARED_VOLUME}/uploads/archive`. Copy archive files there, either as single files or as a downloaded `<topic>/year=/month=/day=/hour=/` tree:

```bash
aws s3 cp s3://<bucket>/<environment>/compact/topic.OdeBsmJson/year=2026/month=10/day=29/ \
  "$DOCKER_SHARED_VOLUME/uploads/archive/compact/topic.OdeBsmJson/" --recursive
```

**Scanning:**
- The inbox is scanned every `ode.file-importer.time-period` seconds (default 5).
- Files are imported in path order, so the objects of a topic partition are published in offset order.
- Files changed in the last 5 seconds are left for the next scan, as are files whose names start with `.` or end with `.part` or `.tmp`. A file that is still being copied in is therefore not read.

**After import:**
- Imported files are moved to `uploads/backup/archive/`.
- Files that could not be imported are moved to `uploads/failed/archive/`.
- Both keep their path relative to the inbox.
- Empty folders in the inbox are removed.

### Through the ODE web page or REST API

On the ODE demo console (`http://<ode-host>:8080`):
1. Select **S3 archive file (.json.gz)** as the file type.
2. Select one or more files.
3. Click **Upload**.

Or use the REST API:

```bash
curl -F "file=@topic.OdeBsmJson+0+0000002500.json.gz" http://<ode-host>:8080/upload/archive
```

**Upload behaviour:**
- Uploaded files are written to `uploads/.incoming/` first, then moved into the archive inbox, so they are imported the same way as files copied into the volume.
- Only the file name of the upload is used. A file with the same name in the inbox is replaced.
- The maximum upload size is `ODE_UPLOAD_MAX_FILE_SIZE` (default `100MB`).

### Results

**Logs:** at `WARN` (the ODE default), the ODE logs:
- files that could not be imported
- up to 10 skipped lines per file, with the reason

Set `LOGGING_LEVEL_US_DOT_ITS_JPO_ODE_ARCHIVE=INFO` on the `ode` service to also log one line per imported file, with its number of published records and skipped lines.

**Metrics:** the counter `ode_archive_import_records_total{topic,format,result}` is exposed on `/actuator/prometheus`.

**When a file fails:** a line that cannot be imported is skipped. The file only fails when:
- its topic is not supported,
- none of its lines can be imported,
- its target topic is disabled (`ode.kafka.disabled-topics`), or
- Kafka does not acknowledge the records within 60 seconds.

[Back to top](#importing-s3-archive-files)

## 3. Local Analysis Stack

To load archived data into a local MongoDB, run the ODE with the jpo-utils Kafka, Kafka Connect and MongoDB services:

1. In the ODE `.env` file, set:

   ```bash
   COMPOSE_PROFILES=ode_base,kafka,kafka_setup,kafka_connect_standalone
   # Create the MongoDB sink connectors for the ODE topics
   CONNECT_CREATE_ODE=true
   # Without the deduplicator, BSM, MAP and TIM are written to MongoDB from the ODE topics
   CONNECT_CREATE_DEDUPLICATOR=false
   # The MongoDB TTL counts from the import time, keep imported data long enough
   MONGO_DATA_RETENTION_SECONDS=31536000
   ```

   `ode_base` includes the ASN.1 decoder (`adm`) that decodes the compact and raw encoded records. It is not needed if only full decoded records are imported. With the FFM codec, keep `ODE_FFM_UDP_ROUTING=raw-topic`, see [Caveats](#5-caveats).

2. Start the stack and import files as described above:

   ```bash
   docker compose up -d
   ```

3. Query the collections by the original receive time, which is indexed on every `Ode*Json` collection:

   ```bash
   docker compose exec mongo mongosh -u "$MONGO_ADMIN_DB_USER" -p "$MONGO_ADMIN_DB_PASS" --authenticationDatabase admin CV \
     --eval 'db.OdeBsmJson.countDocuments({"metadata.odeReceivedAt": {$gte: "2026-10-29T22:00:00", $lt: "2026-10-29T23:00:00"}})'
   ```

The top level `recordGeneratedAt` field that the MongoDB connectors add is the time the record was produced to Kafka. For imported records, that is the import time.

[Back to top](#importing-s3-archive-files)

## 4. Configuration

| Environment variable | Property | Default | Description |
|---|---|---|---|
| `ODE_ARCHIVE_IMPORTER_ENABLED` | `ode.archive-importer.enabled` | `true` | Scan the archive inbox. |
| `ODE_ARCHIVE_IMPORTER_REDECODE_FULL_RECORDS` | `ode.archive-importer.redecode-full-records` | `false` | Decode full decoded records again from `metadata.asn1`, for example to apply a newer decoder, instead of republishing them unchanged. |
| `ODE_UPLOAD_MAX_FILE_SIZE` | `spring.servlet.multipart.max-file-size` | `100MB` | Maximum size of an uploaded file, for archive files and OBU log files. |
| | `ode.archive-importer.inbox-dir` | `archive` | Inbox folder under `ode.file-importer.upload-location-root`. |
| | `ode.archive-importer.min-file-age-seconds` | `5` | Files changed more recently are left for the next scan. |
| | `ode.archive-importer.send-batch-size` | `1000` | Records published before waiting for Kafka acknowledgements. |
| | `ode.archive-importer.send-timeout-seconds` | `60` | Maximum wait for Kafka acknowledgements. |

[Back to top](#importing-s3-archive-files)

## 5. Caveats

- **FFM codec with direct UDP routing.** Compact and raw encoded records are decoded by the ODE consumers of the `topic.OdeRawEncoded<TYPE>Json` topics. Those consumers run with the external codec (`ODE_ASN1_CODEC_MODE=external`, the default) and with the FFM codec's raw-topic routing (`ODE_ASN1_CODEC_MODE=ffm` with `ODE_FFM_UDP_ROUTING=raw-topic`, the default).
  - With `ODE_ASN1_CODEC_MODE=ffm` and `ODE_FFM_UDP_ROUTING=direct`, no consumer reads the raw encoded topics. Imported compact and raw encoded records are then published but never decoded.
  - Full decoded records are imported in every mode, because they are published straight to their decoded topics.
  - Use raw-topic routing, or the external codec, on an ODE used to import compact or raw encoded files.
- **Choose one archive per time range.** The full archive and the compact archive both hold the received messages, so importing more than one of the following for the same time range creates duplicate records:
  - raw encoded files
  - compact files
  - full decoded files
- **Duplicates in the archive.** The connectors deliver at least once, so an archive can contain duplicate records after a connector restart. Remove them when analyzing, for example by `metadata.odeReceivedAt`, `metadata.originIp` and `metadata.asn1`.
- **OBU log upload records.** The compact archive does not keep the record type or location of records imported from OBU log files (`"originIp": ""`). They are imported with the metadata of UDP records of the same message type.
- **Processed and Conflict Monitor topics.** The importer only publishes ODE topics. Records of other archived topics can be produced to Kafka with `kafka-console-producer`, see [Replay archived data into Kafka](../jpo-utils/docs/S3_Archive.md#replay-archived-data-into-kafka), or loaded into MongoDB with `mongoimport`.
- **Kafka record timestamps.** Records are produced with the current time as the Kafka timestamp. Kafka retention and the MongoDB TTL therefore count from the import time, not the original receive time.
- **Import speed.** Files are imported one at a time, by the same scheduler as the OBU log file importer. A large import delays OBU log files placed in `uploads/bsmlog` until it finishes. Decoding speed is limited by the ASN.1 decoder.

[Back to top](#importing-s3-archive-files)
