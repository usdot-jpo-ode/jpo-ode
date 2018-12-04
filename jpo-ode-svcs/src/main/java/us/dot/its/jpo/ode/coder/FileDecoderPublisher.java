package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.stream.JsonDecoderPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

public class FileDecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(FileDecoderPublisher.class);

   private JsonDecoderPublisher jsonDecPub;

   @Autowired
   public FileDecoderPublisher(OdeProperties odeProperties) {

      OdeStringPublisher bsmStringMsgPub = new OdeStringPublisher(odeProperties);
      this.jsonDecPub = new JsonDecoderPublisher(bsmStringMsgPub);
   }

   public void decodeAndPublishFile(Path filePath, BufferedInputStream fileInputStream, ImporterFileType fileType) {

      String fileName = filePath.toFile().getName();

      logger.info("Decoding and publishing file {}", fileName);

      try {
         logger.info("Decoding {} as json file.", filePath);
         jsonDecPub.decodeAndPublish(fileInputStream, fileName, fileType);
      } catch (Exception e) {
         logger.error("Failed to decode and publish file.", e);
      }
   }
}
