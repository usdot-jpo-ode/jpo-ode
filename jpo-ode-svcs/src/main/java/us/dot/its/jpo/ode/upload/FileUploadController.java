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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.exporter.StompStringExporter;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

@RestController
public class FileUploadController {
   private static final String FILTERED_OUTPUT_TOPIC = "/topic/filtered_messages";
   private static final String UNFILTERED_OUTPUT_TOPIC = "/topic/unfiltered_messages";

   private static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

   private final StorageService storageService;

   @Autowired
   public FileUploadController(
           StorageService storageService, OdeProperties odeProperties,
           OdeKafkaProperties odeKafkaProperties, SimpMessagingTemplate template) {
      super();
      this.storageService = storageService;

      ExecutorService threadPool = Executors.newCachedThreadPool();

      Path logPath = Paths.get(odeProperties.getUploadLocationRoot(),
         odeProperties.getUploadLocationObuLog());
      logger.debug("UPLOADER - BSM log file upload directory: {}", logPath);
      Path failurePath = Paths.get(odeProperties.getUploadLocationRoot(), "failed");
      logger.debug("UPLOADER - Failure directory: {}", failurePath);
      Path backupPath = Paths.get(odeProperties.getUploadLocationRoot(), "backup");
      logger.debug("UPLOADER - Backup directory: {}", backupPath);

      // Create the importers that watch folders for new/modified files
      threadPool.submit(new ImporterDirectoryWatcher(odeProperties, odeKafkaProperties, logPath, backupPath, failurePath, ImporterFileType.LOG_FILE, odeProperties.getFileWatcherPeriod()));

      // Create unfiltered exporters
      threadPool.submit(new StompStringExporter(odeKafkaProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeBsmJson()));
      threadPool.submit(new StompStringExporter(odeKafkaProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeTimJson()));
      threadPool.submit(new StompStringExporter(odeKafkaProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeSpatJson()));
      threadPool.submit(new StompStringExporter(odeKafkaProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeMapJson()));
      threadPool.submit(new StompStringExporter(odeKafkaProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeSsmJson()));
      threadPool.submit(new StompStringExporter(odeKafkaProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeSrmJson()));
      threadPool.submit(new StompStringExporter(odeKafkaProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicDriverAlertJson()));
      threadPool.submit(new StompStringExporter(odeKafkaProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeTimBroadcastJson()));

      // Create filtered exporters
      threadPool.submit(new StompStringExporter(odeKafkaProperties, FILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicFilteredOdeBsmJson()));
      threadPool.submit(new StompStringExporter(odeKafkaProperties, FILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicFilteredOdeTimJson()));
   }

   @PostMapping("/upload/{type}")
   public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable("type") String type) {

      logger.debug("File received at endpoint: /upload/{}, name={}", type, file.getOriginalFilename());
      try {
         storageService.store(file, type);
      } catch (Exception e) {
         logger.error("File storage error", e);
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
