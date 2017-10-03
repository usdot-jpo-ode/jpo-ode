package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.stream.BsmAsn1CodecPublisher;

public class FileAsn1CodecPublisher {

   private static final Logger logger = LoggerFactory.getLogger(FileAsn1CodecPublisher.class);

   private BsmAsn1CodecPublisher codecPublisher;
   
   @Autowired
   public FileAsn1CodecPublisher(OdeProperties odeProperties) {

      ByteArrayPublisher messagePub = new ByteArrayPublisher(odeProperties);

      this.codecPublisher = new BsmAsn1CodecPublisher(messagePub);
   }

   public void publishFile(Path filePath, BufferedInputStream fileInputStream) {
      String fileName = filePath.toFile().getName();

      logger.info("Publishing file {}", fileName);
      
      try {
         logger.info("Publishing data from {} to asn1_codec.", filePath);
         codecPublisher.publish(fileInputStream, fileName);
      } catch (Exception e) {
         logger.error("Failed to decode and publish file.", e);
      }
   }
}
