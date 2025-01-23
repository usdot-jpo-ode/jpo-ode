/*******************************************************************************
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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.FileAsn1CodecPublisher;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ImporterDirectoryWatcher implements Runnable {

    public enum ImporterFileType {
        LOG_FILE, JSON_FILE
    }

    @Setter
    @Getter
    private boolean watching;

    private final ImporterProcessor importerProcessor;
    private final FileImporterProperties props;
    private final ScheduledExecutorService executor;
    private final Path inboxPath;
    private final Path backupPath;
    private final Path failuresPath;

    public ImporterDirectoryWatcher(FileImporterProperties fileImporterProperties,
                                    OdeKafkaProperties odeKafkaProperties,
                                    JsonTopics jsonTopics,
                                    ImporterFileType fileType,
                                    RawEncodedJsonTopics rawEncodedJsonTopics) {
        this.props = fileImporterProperties;
        this.watching = true;

        this.inboxPath = Paths.get(fileImporterProperties.getUploadLocationRoot(), fileImporterProperties.getObuLogUploadLocation());
        log.debug("UPLOADER - BSM log file upload directory: {}", inboxPath);

        this.failuresPath = Paths.get(fileImporterProperties.getUploadLocationRoot(), fileImporterProperties.getFailedDir());
        log.debug("UPLOADER - Failure directory: {}", failuresPath);

        this.backupPath = Paths.get(fileImporterProperties.getUploadLocationRoot(), fileImporterProperties.getBackupDir());
        log.debug("UPLOADER - Backup directory: {}", backupPath);

        try {
            String msg = "Created directory {}";

            OdeFileUtils.createDirectoryRecursively(inboxPath);
            log.debug(msg, inboxPath);

            OdeFileUtils.createDirectoryRecursively(failuresPath);
            log.debug(msg, failuresPath);

            OdeFileUtils.createDirectoryRecursively(backupPath);
            log.debug(msg, backupPath);
        } catch (IOException e) {
            log.error("Error creating directory", e);
        }

        this.importerProcessor = new ImporterProcessor(new FileAsn1CodecPublisher(odeKafkaProperties, jsonTopics, rawEncodedJsonTopics),
                fileType,
                fileImporterProperties.getBufferSize());

        executor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void run() {

        log.info("Processing inbox directory {} every {} seconds.", inboxPath, props.getTimePeriod());

        // ODE-646: the old method of watching the directory used file
        // event notifications and was unreliable for large quantities of files
        // Watch directory for file events
        executor.scheduleWithFixedDelay(() -> importerProcessor.processDirectory(inboxPath, backupPath, failuresPath),
                0, props.getTimePeriod(), TimeUnit.SECONDS);

        try {
            // This line will only execute in the event that .scheduleWithFixedDelay() throws an error
            executor.awaitTermination(props.getTimePeriod(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Directory watcher polling loop interrupted!", e);
        }
    }
}
