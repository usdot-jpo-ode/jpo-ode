package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

public class FileAsn1CodecPublisher {

   public class FileAsn1CodecPublisherException extends Exception {

      private static final long serialVersionUID = 1L;

      public FileAsn1CodecPublisherException(String string, Exception e) {
         super (string, e);
      }

   }

   private static final Logger logger = LoggerFactory.getLogger(FileAsn1CodecPublisher.class);

   private LogFileToAsn1CodecPublisher codecPublisher;
   
   @Autowired
   public FileAsn1CodecPublisher(OdeProperties odeProperties) {

      StringPublisher messagePub = new StringPublisher(odeProperties);

      this.codecPublisher = new LogFileToAsn1CodecPublisher(messagePub);
   }

   public void publishFile(Path filePath, BufferedInputStream fileInputStream, ImporterFileType fileType) 
         throws FileAsn1CodecPublisherException {
      String fileName = filePath.toFile().getName();

      logger.info("Publishing file {}", fileName);
      
      try {
         logger.info("Publishing data from {} to asn1_codec.", filePath);
         codecPublisher.publish(fileInputStream, fileName, fileType);
      } catch (Exception e) {
         throw new FileAsn1CodecPublisherException("Failed to publish file.", e);
      }
   }

}
