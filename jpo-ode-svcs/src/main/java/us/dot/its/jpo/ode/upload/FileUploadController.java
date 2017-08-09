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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.exporter.FilteredBsmExporter;
import us.dot.its.jpo.ode.exporter.OdeBsmExporter;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

@Controller
public class FileUploadController {
   // private static final String OUTPUT_TOPIC = "/topic/messages";
   private static final String FILTERED_OUTPUT_TOPIC = "/topic/filtered_messages";
   private static final String ODE_BSM_OUTPUT_TOPIC = "/topic/ode_bsm_messages";

   private static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

   private final StorageService storageService;

   @Autowired
   public FileUploadController(StorageService storageService, OdeProperties odeProperties,
         SimpMessagingTemplate template) {
      super();
      this.storageService = storageService;

      ExecutorService threadPool = Executors.newCachedThreadPool();

      Path bsmPath = Paths.get(odeProperties.getUploadLocationRoot(), odeProperties.getUploadLocationBsm());
      logger.debug("UPLOADER - Bsm directory: {}", bsmPath);

      Path messageFramePath = Paths.get(odeProperties.getUploadLocationRoot(),
            odeProperties.getUploadLocationMessageFrame());
      logger.debug("UPLOADER - Message Frame directory: {}", messageFramePath);

      Path backupPath = Paths.get(odeProperties.getUploadLocationRoot(), "backup");
      logger.debug("UPLOADER - Backup directory: {}", backupPath);

      // Create the importers that watch folders for new/modified files
      threadPool.submit(new ImporterDirectoryWatcher(odeProperties, bsmPath, backupPath));
      threadPool.submit(new ImporterDirectoryWatcher(odeProperties, messageFramePath, backupPath));

      // Create the exporters
      threadPool.submit(new OdeBsmExporter(odeProperties, ODE_BSM_OUTPUT_TOPIC, template));
      threadPool.submit(new FilteredBsmExporter(odeProperties, FILTERED_OUTPUT_TOPIC, template));
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
