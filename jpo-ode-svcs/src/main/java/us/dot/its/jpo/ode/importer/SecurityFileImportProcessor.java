package us.dot.its.jpo.ode.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.security.CertificateLoader;
import us.dot.its.jpo.ode.util.ZipUtils;

public class SecurityFileImportProcessor extends ImporterProcessor {

   private static final Logger logger = LoggerFactory.getLogger(SecurityFileImportProcessor.class);

   
   public SecurityFileImportProcessor(OdeProperties odeProperties, ImporterFileType fileType, String pubKeyHexBytes) {
      super(odeProperties, fileType);
   }



   public boolean processFile(Path zipFilePath) {
      boolean success = true;
      try {
         // Log files contained in the zip file
         logFiles(zipFilePath);

         CertificateLoader loader = new CertificateLoader(odeProperties);
         
         switch (fileType) {
         case SECURITY_ENROLLMENT_ZIP_FILE:

            //Extract the zip file
            Path outputDirPath = Paths.get(odeProperties.getScmsEnrollmentDir());
            ZipUtils.unZip(zipFilePath, outputDirPath);

            loader.initScmsProperties();
            loader.loadEnrollmentCerts();
            
            break;
            
         case SECURITY_APPLICATION_CERT:
            loader.initScmsProperties();
            loader.loadApplicationCert();
            break;
         default:
            break;
         }
         
      } catch (Exception e) {
         success = false;
         String msg = "Failed to open or process file: " + zipFilePath;
         logger.error(msg, e);
         EventLogger.logger.error(msg, e);  
      } finally {
      }
      return success;
   }

   private void logFiles(Path filePath) throws IOException {
      try(ZipFile zf = new ZipFile(filePath.toString())) {
         Enumeration<? extends ZipEntry> entries = zf.entries();
         while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            logger.debug("Importing: {}", entry.getName());
         }
      }
   }

}
