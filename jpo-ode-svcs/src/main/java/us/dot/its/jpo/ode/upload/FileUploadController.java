package us.dot.its.jpo.ode.upload;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.exporter.FilteredBsmExporter;
import us.dot.its.jpo.ode.exporter.RawBsmExporter;
import us.dot.its.jpo.ode.importer.ImporterWatchService;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

@Controller
public class FileUploadController {
    private static final String OUTPUT_TOPIC = "/topic/messages";
    private static final String FILTERED_OUTPUT_TOPIC = "/topic/filtered_messages";

    private static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final StorageService storageService;
    private OdeProperties odeProperties;

    @Autowired
    public FileUploadController(StorageService storageService, OdeProperties odeProperties,
            SimpMessagingTemplate template) throws IllegalAccessException {
        super();
        this.odeProperties = odeProperties;
        this.storageService = storageService;

        Path bsmPath = Paths.get(odeProperties.getUploadLocationRoot(), odeProperties.getUploadLocationBsm());
        logger.debug("UPLOADER - Bsm directory: {}", bsmPath);
        
        Path messageFramePath = Paths.get(odeProperties.getUploadLocationRoot(),
                odeProperties.getUploadLocationMessageFrame());
        logger.debug("UPLOADER - Message Frame directory: {}", messageFramePath);
        
        Path backupPath = Paths.get(odeProperties.getUploadLocationRoot(), "backup");
        logger.debug("UPLOADER - Backup directory: {}", backupPath);

        launchImporter(bsmPath, backupPath);

        
        launchImporter(messageFramePath, backupPath);
        
        try {
            Executors.newSingleThreadExecutor().submit(new RawBsmExporter(
                    odeProperties, OUTPUT_TOPIC, template));
        } catch (Exception e) {
            logger.error("Error launching Exporter", e);
        }
        
        try {
            Executors.newSingleThreadExecutor().submit(new FilteredBsmExporter(
                    odeProperties, FILTERED_OUTPUT_TOPIC, template));
        } catch (Exception e) {
            logger.error("Error launching Exporter", e);
        }
    }

    private ExecutorService launchImporter(Path dirPath, Path backupPath) {
        ExecutorService importer = Executors.newSingleThreadExecutor();
        logger.debug("UPLOADER - Upload directory: {}", dirPath);
        importer.submit(new ImporterWatchService(odeProperties, dirPath, backupPath,
                LoggerFactory.getLogger(ImporterWatchService.class)));
        
        return importer;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping("/upload/{type}")
    @ResponseBody
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable("type") String type) {

        if (("bsm").equals(type)) {
            logger.debug("BSM file recieved: {}", file.getOriginalFilename());
        } else if (("messageFrame").equals(type)) {
            logger.debug("Message Frame file recieved: {}", file.getOriginalFilename());
        } else if (("json").equals(type)) {
            logger.debug("JSON file recieved: {}", file.getOriginalFilename());
        } else {
            logger.error("File storage error: Unknown file type provided");
            return "{\"success\": false}";
        }

        logger.debug("File received at endpoint: /upload/{}, name={}", type, file.getOriginalFilename());
        try {
            storageService.store(file, type);
        } catch (Exception e) {
            logger.error("File storage error: " + e);
            return "{\"success\": false}";
        }

        return "{\"success\": true}";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
