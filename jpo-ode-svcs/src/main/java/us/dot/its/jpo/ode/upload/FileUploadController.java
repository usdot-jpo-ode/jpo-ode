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
import us.dot.its.jpo.ode.coder.BsmCoder;
import us.dot.its.jpo.ode.coder.MessageFrameCoder;
import us.dot.its.jpo.ode.exporter.Exporter;
import us.dot.its.jpo.ode.importer.ImporterWatchService;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

@Controller
public class FileUploadController {
    private static final String OUTPUT_TOPIC = "/topic/messages";

    private static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final StorageService storageService;
    private ExecutorService bsmImporter;
    private ExecutorService messageFrameImporter;
    private ExecutorService bsmExporter;

    @Autowired
    public FileUploadController(StorageService storageService, OdeProperties odeProperties,
            SimpMessagingTemplate template) throws IllegalAccessException {
        super();
        this.storageService = storageService;

        bsmImporter = Executors.newSingleThreadExecutor();
        messageFrameImporter = Executors.newSingleThreadExecutor();
        bsmExporter = Executors.newSingleThreadExecutor();

        Path bsmPath = Paths.get(odeProperties.getUploadLocationRoot(), odeProperties.getUploadLocationBsm());
        logger.debug("UPLOADER - Bsm directory: {}", bsmPath);
        
        Path messageFramePath = Paths.get(odeProperties.getUploadLocationRoot(),
                odeProperties.getUploadLocationMessageFrame());
        logger.debug("UPLOADER - Message Frame directory: {}", messageFramePath);
        
        Path backupPath = Paths.get(odeProperties.getUploadLocationRoot(), "backup");
        logger.debug("UPLOADER - Backup directory: {}", backupPath);

        bsmImporter.submit(new ImporterWatchService(bsmPath, backupPath, new BsmCoder(odeProperties),
                LoggerFactory.getLogger(ImporterWatchService.class), odeProperties.FILE_TYPES,
                odeProperties.KAFKA_TOPIC_J2735_BSM));

        messageFrameImporter.submit(new ImporterWatchService(messageFramePath, backupPath, new MessageFrameCoder(odeProperties),
                LoggerFactory.getLogger(ImporterWatchService.class), odeProperties.FILE_TYPES,
                odeProperties.KAFKA_TOPIC_J2735_BSM));

        try {
            bsmExporter.submit(new Exporter(odeProperties, template, OUTPUT_TOPIC));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            logger.error("Error launching Exporter", e);
        }

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
