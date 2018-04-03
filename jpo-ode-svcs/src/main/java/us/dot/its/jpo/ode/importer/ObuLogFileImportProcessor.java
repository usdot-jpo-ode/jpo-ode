package us.dot.its.jpo.ode.importer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.FileAsn1CodecPublisher;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.util.FileUtils;

public class ObuLogFileImportProcessor extends ImporterProcessor {

   private static final Logger logger = LoggerFactory.getLogger(ObuLogFileImportProcessor.class);
// Removed for ODE-559
//   private FileDecoderPublisher decoderPublisherManager;
   private FileAsn1CodecPublisher codecPublisher;

   public ObuLogFileImportProcessor(OdeProperties odeProperties, ImporterFileType fileType) {
      super(odeProperties, fileType);
   // Removed for ODE-559
//      this.decoderPublisherManager = new FileDecoderPublisher(odeProperties);
      this.codecPublisher = new FileAsn1CodecPublisher(odeProperties);
   }

   public boolean processFile(Path filePath) {

      /*
       * ODE-559 vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
       * removed lines below when asn1_codec was integrated
       */
//      try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
//         BufferedInputStream bis = new BufferedInputStream(inputStream, odeProperties.getImportProcessorBufferSize());
//         decoderPublisherManager.decodeAndPublishFile(filePath, bis, fileType);
//         bis = new BufferedInputStream(inputStream, odeProperties.getImportProcessorBufferSize());
//      } catch (Exception e) {
//         logger.error("Unable to open or process file: " + filePath, e);
//      }
      // ODE-559 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

      // ODE-559
      boolean success = true;
      InputStream inputStream = null;
      BufferedInputStream bis = null;
      
      try {
         inputStream = new FileInputStream(filePath.toFile());
         String probeContentType = Files.probeContentType(filePath);
         if (probeContentType != null && (
               probeContentType.equals("application/gzip") || 
               probeContentType.equals("application/x-zip-compressed"))) { 
            inputStream = new GZIPInputStream(inputStream);
         }
         bis = new BufferedInputStream(inputStream, odeProperties.getImportProcessorBufferSize());
         codecPublisher.publishFile(filePath, bis);
      } catch (Exception e) {
         success = false;
         String msg = "Failed to open or process file: " + filePath;
         logger.error(msg, e);
         EventLogger.logger.error(msg, e);  
      } finally {
         try {
            if (bis != null) {
               bis.close();
            }
            if (inputStream != null) {
               inputStream.close();
            }
         } catch (IOException e) {
            logger.error("Failed to close file stream: {}", e);
         }
      }
      return success;
   }

   public void backupFile(boolean success, Path filePath, Path backupDir, Path failureDir) {
      try {
         if (success) {
            FileUtils.backupFile(filePath, backupDir);
            logger.info("File moved to backup: {}", backupDir);
            EventLogger.logger.info("File moved to backup: {}", backupDir);  
         } else {
            FileUtils.moveFile(filePath, failureDir);
            logger.info("File moved to failure directory: {}", failureDir);  
            EventLogger.logger.info("File moved to failure directory: {}", failureDir);
         }
      } catch (IOException e) {
         logger.error("Unable to backup file: " + filePath, e);
      }
   }
}
