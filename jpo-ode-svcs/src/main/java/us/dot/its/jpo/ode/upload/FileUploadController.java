package us.dot.its.jpo.ode.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.exporter.StompStringExporter;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class FileUploadController {
   private static final String FILTERED_OUTPUT_TOPIC = "/topic/filtered_messages";
   private static final String UNFILTERED_OUTPUT_TOPIC = "/topic/unfiltered_messages";

   private static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

   private final StorageService storageService;

   @Autowired
   public FileUploadController(
   		StorageService storageService, OdeProperties odeProperties,
         SimpMessagingTemplate template) {
      super();
      this.storageService = storageService;

      ExecutorService threadPool = Executors.newCachedThreadPool();

      Path logPath = Paths.get(odeProperties.getUploadLocationRoot(),
          odeProperties.getUploadLocationObuLog());
      logger.debug("UPLOADER - BSM log file upload directory: {}", logPath);
      Path backupPath = Paths.get(odeProperties.getUploadLocationRoot(), "backup");
      logger.debug("UPLOADER - Backup directory: {}", backupPath);

      // Create the importers that watch folders for new/modified files
      threadPool.submit(new ImporterDirectoryWatcher(odeProperties, logPath, backupPath, ImporterFileType.OBU_LOG_FILE));

      // Create unfiltered exporters
      threadPool.submit(new StompStringExporter(odeProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeBsmJson()));
      threadPool.submit(new StompStringExporter(odeProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeDNMsgJson()));
      threadPool.submit(new StompStringExporter(odeProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeTimJson()));
      threadPool.submit(new StompStringExporter(odeProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicDriverAlertJson()));

      // Create filtered exporters
      threadPool.submit(new StompStringExporter(odeProperties, FILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicFilteredOdeBsmJson()));
   }

   @PostMapping("/upload/{type}")
   @ResponseBody
   public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable("type") String type) {

      logger.debug("File received at endpoint: /upload/{}, name={}", type, file.getOriginalFilename());
      try {
         storageService.store(file, type);
      } catch (Exception e) {
         logger.error("File storage error: " + e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"Error\": \"File storage error.\"}");
         // do not return exception, XSS vulnerable
      }

      return ResponseEntity.status(HttpStatus.OK).body("{\"Success\": \"True\"}");
   }

   @ExceptionHandler(StorageFileNotFoundException.class)
   public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
      return ResponseEntity.notFound().build();
   }

}
