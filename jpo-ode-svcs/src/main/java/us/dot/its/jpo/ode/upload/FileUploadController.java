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
package us.dot.its.jpo.ode.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;
import us.dot.its.jpo.ode.exporter.StompStringExporter;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher;
import us.dot.its.jpo.ode.kafka.topics.FileTopics;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
public class FileUploadController {
    private final StorageService storageService;

    @Autowired
    public FileUploadController(
            StorageService storageService,
            SimpMessagingTemplate template,
            FileImporterProperties fileImporterProps,
            FileTopics fileTopics,
            JsonTopics jsonTopics,
            RawEncodedJsonTopics rawEncodedJsonTopics,
            OdeKafkaProperties odeKafkaProperties) {
        super();
        this.storageService = storageService;

        ExecutorService threadPool = Executors.newCachedThreadPool();

        // Create the importers that watch folders for new/modified files
        threadPool.submit(
                new ImporterDirectoryWatcher(fileImporterProps,
                        odeKafkaProperties,
                        jsonTopics,
                        ImporterDirectoryWatcher.ImporterFileType.LOG_FILE,
                        rawEncodedJsonTopics)
        );

        // Create unfiltered exporters
        threadPool.submit(new StompStringExporter(fileTopics.getUnfilteredOutput(), template, jsonTopics.getBsm(), odeKafkaProperties.getBrokers()));
        threadPool.submit(new StompStringExporter(fileTopics.getUnfilteredOutput(), template, jsonTopics.getTim(), odeKafkaProperties.getBrokers()));
        threadPool.submit(new StompStringExporter(fileTopics.getUnfilteredOutput(), template, jsonTopics.getSpat(), odeKafkaProperties.getBrokers()));
        threadPool.submit(new StompStringExporter(fileTopics.getUnfilteredOutput(), template, jsonTopics.getMap(), odeKafkaProperties.getBrokers()));
        threadPool.submit(new StompStringExporter(fileTopics.getUnfilteredOutput(), template, jsonTopics.getSsm(), odeKafkaProperties.getBrokers()));
        threadPool.submit(new StompStringExporter(fileTopics.getUnfilteredOutput(), template, jsonTopics.getSrm(), odeKafkaProperties.getBrokers()));
        threadPool.submit(new StompStringExporter(fileTopics.getUnfilteredOutput(), template, jsonTopics.getDriverAlert(), odeKafkaProperties.getBrokers()));
        threadPool.submit(new StompStringExporter(fileTopics.getUnfilteredOutput(), template, jsonTopics.getTimBroadcast(), odeKafkaProperties.getBrokers()));

        // Create filtered exporters
        threadPool.submit(new StompStringExporter(fileTopics.getFilteredOutput(), template, jsonTopics.getBsmFiltered(), odeKafkaProperties.getBrokers()));
        threadPool.submit(new StompStringExporter(fileTopics.getFilteredOutput(), template, jsonTopics.getTimFiltered(), odeKafkaProperties.getBrokers()));
    }

    @PostMapping("/upload/{type}")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable("type") String type) {

        log.debug("File received at endpoint: /upload/{}, name={}", type, file.getOriginalFilename());
        try {
            storageService.store(file, type);
        } catch (Exception e) {
            log.error("File storage error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"Error\": \"File storage error.\"}");
            // do not return exception, XSS vulnerable
        }

        return ResponseEntity.status(HttpStatus.OK).body("{\"Success\": \"True\"}");
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<Void> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
