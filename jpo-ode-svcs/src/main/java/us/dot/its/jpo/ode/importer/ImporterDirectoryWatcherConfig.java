package us.dot.its.jpo.ode.importer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

/**
 * Configuration class for setting up the Directory Watcher Bean required for importing and
 * processing directory files.
 */
@Configuration
public class ImporterDirectoryWatcherConfig {

  /**
   * Creates and configures an ImporterDirectoryWatcher Bean to monitor and process incoming files
   * in the specified directories. The ImporterDirectoryWatcher is configured with file handling
   * settings, Kafka properties, and topic configurations to process directory files of the type
   * LOG_FILE.
   *
   * @param fileImporterProps    the file importer properties containing directory paths, buffer size,
   *                             and other configuration details for file processing.
   * @param odeKafkaProperties   the Kafka configuration properties for producer and topic details.
   * @param jsonTopics           configuration for JSON Kafka topics used during the file processing.
   * @param rawEncodedJsonTopics configuration for raw-encoded JSON Kafka topics used during the
   *                             file processing.
   *
   * @return an initialized and configured ImporterDirectoryWatcher instance for monitoring
   *     directories.
   */
  @Bean
  public ImporterDirectoryWatcher importerDirectoryWatcher(
      FileImporterProperties fileImporterProps,
      OdeKafkaProperties odeKafkaProperties,
      JsonTopics jsonTopics,
      RawEncodedJsonTopics rawEncodedJsonTopics
  ) {
    return new ImporterDirectoryWatcher(fileImporterProps,
        odeKafkaProperties,
        jsonTopics,
        ImporterDirectoryWatcher.ImporterFileType.LOG_FILE,
        rawEncodedJsonTopics);
  }
}
