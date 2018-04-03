package us.dot.its.jpo.ode.upload;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.exporter.StompStringExporter;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher;
import us.dot.its.jpo.ode.importer.ImporterProcessor.ImporterFileType;
import us.dot.its.jpo.ode.importer.ObuLogFileImportProcessor;
import us.dot.its.jpo.ode.importer.SecurityFileImportProcessor;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

@Controller
public class FileUploadController {
   private static final String FILTERED_OUTPUT_TOPIC = "/topic/filtered_messages";
   private static final String UNFILTERED_OUTPUT_TOPIC = "/topic/unfiltered_messages";

   private static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

   private final StorageService storageService;
   private final OdeProperties odeProperties; 
   private final SimpMessagingTemplate template;
   private final String pubKeyHexBytes;

   @Autowired
   public FileUploadController(
   		StorageService storageService, 
   		OdeProperties odeProperties,
         SimpMessagingTemplate template,
         @Qualifier("pubKeyHexBytes")String pubKeyHexBytes) {
      super();
      this.storageService = storageService;
      this.odeProperties = odeProperties;
      this.template = template;
      this.pubKeyHexBytes = pubKeyHexBytes;
   }

   @PostConstruct
   private void initialize() {

      ExecutorService threadPool = Executors.newCachedThreadPool();

      Path logPath = Paths.get(odeProperties.getUploadLocationRoot(),
          odeProperties.getUploadLocationObuLogDir());
      Path securityEnrollmentPath = Paths.get(odeProperties.getUploadLocationRoot(),
         odeProperties.getUploadLocationSecurityEnrollmentDir());

      logger.debug("UPLOADER - BSM log file upload directory: {}", logPath);
      Path failurePath = Paths.get(odeProperties.getUploadLocationRoot(), "failed");
      logger.debug("UPLOADER - Failure directory: {}", failurePath);
      Path backupPath = Paths.get(odeProperties.getUploadLocationRoot(), "backup");
      logger.debug("UPLOADER - Backup directory: {}", backupPath);

      // Create the importers that watch folders for new/modified files
      threadPool.submit(new ImporterDirectoryWatcher(odeProperties, logPath, 
         backupPath.resolve(logPath.getFileName()), failurePath.resolve(logPath.getFileName()), odeProperties.getFileWatcherPeriod(), 
         new ObuLogFileImportProcessor(odeProperties, ImporterFileType.OBU_LOG_FILE)));
      threadPool.submit(new ImporterDirectoryWatcher(odeProperties, securityEnrollmentPath, 
         backupPath.resolve(securityEnrollmentPath.getFileName()), failurePath.resolve(securityEnrollmentPath.getFileName()), odeProperties.getFileWatcherPeriod(), 
         new SecurityFileImportProcessor(odeProperties, ImporterFileType.SECURITY_ENROLLMENT_ZIP_FILE, pubKeyHexBytes)));

      // Create unfiltered exporters
      threadPool.submit(new StompStringExporter(odeProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeBsmJson()));
      threadPool.submit(new StompStringExporter(odeProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeTimJson()));
      threadPool.submit(new StompStringExporter(odeProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicDriverAlertJson()));
      threadPool.submit(new StompStringExporter(odeProperties, UNFILTERED_OUTPUT_TOPIC, template, odeProperties.getKafkaTopicOdeTimBroadcastJson()));

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
