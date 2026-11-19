package us.dot.its.jpo.ode.archive;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of the S3 archive file importer, which publishes the records of archive files
 * written by the jpo-utils S3 archive connectors back to the ODE Kafka topics.
 */
@Configuration
@ConfigurationProperties(prefix = "ode.archive-importer")
@Data
public class ArchiveImporterProperties {

  /**
   * Whether the archive inbox directory is scanned.
   */
  private boolean enabled = true;

  /**
   * Inbox directory, relative to {@code ode.file-importer.upload-location-root}.
   */
  private String inboxDir = "archive";

  /**
   * When true, full decoded records are decoded again from their {@code metadata.asn1}, instead
   * of being republished unchanged to their decoded topic.
   */
  private boolean redecodeFullRecords = false;

  /**
   * Files modified more recently than this are left for the next scan, so that files that are
   * still being copied into the inbox are not read.
   */
  private int minFileAgeSeconds = 5;

  /**
   * Maximum time to wait for Kafka to acknowledge a batch of published records.
   */
  private int sendTimeoutSeconds = 60;

  /**
   * Number of records published before waiting for Kafka to acknowledge them.
   */
  private int sendBatchSize = 1000;
}
