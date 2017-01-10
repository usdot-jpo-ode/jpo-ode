package us.dot.its.jpo.ode.upload;

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
import us.dot.its.jpo.ode.exporter.Exporter;
import us.dot.its.jpo.ode.importer.Importer;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

@Controller
public class FileUploadController {
   private static final String OUTPUT_TOPIC = "/topic/messages";

   private static Logger logger = LoggerFactory.getLogger(FileUploadController.class);
   private Logger data = LoggerFactory.getLogger("data");

   private final StorageService storageService;
   private ExecutorService importer;
   private ExecutorService exporter;

   @Autowired
   public FileUploadController(StorageService storageService, OdeProperties odeProperties,
         SimpMessagingTemplate template) {
      super();
      this.storageService = storageService;

      data.info("Creating importer and exporter");
      importer = Executors.newSingleThreadExecutor();
      exporter = Executors.newSingleThreadExecutor();

      try {
         importer.submit(new Importer(odeProperties));
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
         logger.error("Error launching Importer", e);
      }

      try {
         exporter.submit(new Exporter(odeProperties, template, OUTPUT_TOPIC));
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
         logger.error("Error launching Exporter", e);
      }

   }

   // @GetMapping("/")
   // public String listUploadedFiles(Model model) throws IOException {
   //
   // model.addAttribute("files", storageService
   // .loadAll()
   // .map(path ->
   // MvcUriComponentsBuilder
   // .fromMethodName(FileUploadController.class, "serveFile",
   // path.getFileName().toString())
   // .build().toString())
   // .collect(Collectors.toList()));
   //
   // return "uploadForm";
   // }

   @GetMapping("/files/{filename:.+}")
   @ResponseBody
   public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

      Resource file = storageService.loadAsResource(filename);
      return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
   }

   @PostMapping("/")
   @ResponseBody
   public String handleFileUpload(@RequestParam("file") MultipartFile file) {

      storageService.store(file);

      /*
       * redirectAttributes.addFlashAttribute("message",
       * "You successfully uploaded " + file.getOriginalFilename() + "!");
       */

      return "{\"success\": true}";
      // return "redirect:/helloworld";
   }

   @ExceptionHandler(StorageFileNotFoundException.class)
   public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
      return ResponseEntity.notFound().build();
   }

}
