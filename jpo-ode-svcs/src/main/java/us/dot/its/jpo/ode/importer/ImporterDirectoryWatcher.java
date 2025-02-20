/*============================================================================
 * Copyright 2018 572682
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

/**
 * The ImporterDirectoryWatcher uses a scheduled task to periodically scan the designated inbox directory for
 * files offloaded from the Roadside Units (RSUs) for offline bulk processing.
 */
@Component
@EnableScheduling
@Slf4j
public class ImporterDirectoryWatcher {

  private final ImporterProcessor importerProcessor;
  private final FileImporterProperties props;
  private final Path inboxPath;
  private final Path backupPath;
  private final Path failuresPath;

  /**
   * Constructs an instance of ImporterDirectoryWatcher responsible for managing the
   * inbox, failure, and backup directories for processing files offloaded from RSUs.
   * Initializes the ImporterProcessor for handling log files and their associated encoding.
   *
   * @param fileImporterProperties Configuration properties for file importer.
   * @param jsonTopics             Configuration for Kafka topics that handle JSON payloads.
   * @param rawEncodedJsonTopics   Configuration for Kafka topics that handle raw encoded
   *                               JSON payloads.
   * @param kafkaTemplate          Kafka template for producing messages to Kafka topics.
   */
  public ImporterDirectoryWatcher(FileImporterProperties fileImporterProperties,
                                  JsonTopics jsonTopics,
                                  RawEncodedJsonTopics rawEncodedJsonTopics,
                                  KafkaTemplate<String, String> kafkaTemplate) {
    this.props = fileImporterProperties;

    this.inboxPath = Paths.get(fileImporterProperties.getUploadLocationRoot(), fileImporterProperties.getObuLogUploadLocation());
    log.debug("UPLOADER - BSM log file upload directory: {}", inboxPath);

    this.failuresPath = Paths.get(fileImporterProperties.getUploadLocationRoot(), fileImporterProperties.getFailedDir());
    log.debug("UPLOADER - Failure directory: {}", failuresPath);

    this.backupPath = Paths.get(fileImporterProperties.getUploadLocationRoot(), fileImporterProperties.getBackupDir());
    log.debug("UPLOADER - Backup directory: {}", backupPath);

    try {
      OdeFileUtils.createDirectoryRecursively(inboxPath);
      log.debug("Created inbox directory at: {}", inboxPath);

      OdeFileUtils.createDirectoryRecursively(failuresPath);
      log.debug("Created failures directory at: {}", failuresPath);

      OdeFileUtils.createDirectoryRecursively(backupPath);
      log.debug("Created backup directory at: {}", backupPath);
    } catch (IOException e) {
      log.error("Error creating directory", e);
    }

    this.importerProcessor = new ImporterProcessor(
        new LogFileToAsn1CodecPublisher(kafkaTemplate, jsonTopics, rawEncodedJsonTopics),
        ImporterFileType.LOG_FILE,
        fileImporterProperties.getBufferSize());
  }

  /**
   * Executes the scheduled task to process the inbox directory at a fixed interval.
   * The method logs the processing information, processes files in the inbox, and logs
   * the number of files processed successfully.
   *
   * <p>This task runs at a fixed rate defined in the application configuration using
   * the property {@code ode.file-importer.time-period}, measured in seconds.</p>
   *
   * <p>During execution, the method utilizes the file processing logic to handle files
   * located in the inbox directory, moving them to either the backup or failures directory
   * based on the processing outcome.</p>
   */
  @Scheduled(fixedRateString = "${ode.file-importer.time-period}", timeUnit = TimeUnit.SECONDS)
  public void run() {
    log.info("Processing inbox directory {} every {} seconds.", inboxPath, props.getTimePeriod());
    var filesProcessedSuccessfully = importerProcessor.processDirectory(inboxPath, backupPath, failuresPath);
    log.info("Successfully processed {} files.", filesProcessedSuccessfully);
  }
}
