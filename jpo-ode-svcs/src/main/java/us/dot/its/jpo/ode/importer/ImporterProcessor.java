package us.dot.its.jpo.ode.importer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.FileDecoderPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterDirType;

public class ImporterProcessor {

   private static final Logger logger = LoggerFactory.getLogger(ImporterProcessor.class);
   private FileDecoderPublisher decoderPublisherManager;
   private OdeProperties odeProperties;
   
   public ImporterProcessor(OdeProperties odeProperties) {
      this.decoderPublisherManager = new FileDecoderPublisher(odeProperties);
      this.odeProperties = odeProperties;
   }

   public void processDirectory(Path dir, Path backupDir) {
      int count = 0;
      // Process files already in the directory
      logger.debug("Started processing existing files at location: {}", dir);
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {

         for (Path entry : stream) {
            logger.debug("Found a file to process: {}", entry.getFileName());
            processAndBackupFile(entry, backupDir);
            count++;
         }

         logger.debug("Finished processing {} existing files at location: {}", count, dir);
      } catch (Exception e) {
         logger.error("Error processing existing files.", e);
      }
   }

   public void processAndBackupFile(Path filePath, Path backupDir, ImporterDirType dirType) {

      try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
          BufferedInputStream bis = new BufferedInputStream(inputStream, 
              odeProperties.getImportProcessorBufferSize());
          decoderPublisherManager.decodeAndPublishFile(filePath, bis, dirType);
      } catch (Exception e) {
         logger.error("Unable to open or process file: " + filePath, e);
      }

      try {
         OdeFileUtils.backupFile(filePath, backupDir);
      } catch (IOException e) {
         logger.error("Unable to backup file: " + filePath, e);
      }
   }
}
