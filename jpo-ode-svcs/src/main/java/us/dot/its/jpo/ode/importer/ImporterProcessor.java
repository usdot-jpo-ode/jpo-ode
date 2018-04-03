package us.dot.its.jpo.ode.importer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.util.FileUtils;

public abstract class ImporterProcessor {
   public enum ImporterFileType {
      OBU_LOG_FILE,
      SECURITY_ENROLLMENT_ZIP_FILE, SECURITY_APPLICATION_CERT
   }

   private static final Logger logger = LoggerFactory.getLogger(ImporterProcessor.class);
   protected OdeProperties odeProperties;
   protected ImporterFileType fileType;

   public ImporterProcessor(OdeProperties odeProperties, ImporterFileType fileType) {
      this.odeProperties = odeProperties;
      this.fileType = fileType;
   }

   public int processDirectory(Path dir, Path backupDir, Path failureDir) {
      int count = 0;
      // Process files already in the directory
      // logger.debug("Processing files at location: {}", dir);
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {

         for (Path entry : stream) {
            if (entry.toFile().isDirectory()) {
               processDirectory(entry, backupDir, failureDir);
            } else {
               logger.debug("Found a file to process: {}", entry.getFileName());
               boolean success = processFile(entry);
               backupFile(success, entry, backupDir, failureDir);
               count++;
            }
         }

         //logger.debug("Finished processing {} files at location: {}", count, dir);
      } catch (Exception e) {
         logger.error("Error processing files.", e);
      }
      return count;
   }

   public abstract boolean processFile(Path filePath);

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
