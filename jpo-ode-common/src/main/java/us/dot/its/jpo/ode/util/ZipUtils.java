package us.dot.its.jpo.ode.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtils {

   private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

   /**
    * Unzips a zip file into a new or existing folder
    * 
    * @param zipFile
    *           input zip file
    * @param output
    *           zip file output folder
    * @throws ImporterException
    */
   public static void unZip(Path zipFilePath, Path outputDirPath) throws UtilException {

      byte[] buffer = new byte[1024];

      try {
         String probeContentType = Files.probeContentType(zipFilePath);
         if (probeContentType != null && (
               probeContentType.equals("application/gzip") || 
               probeContentType.equals("application/x-zip-compressed"))) {
            // create output directory is not exists
            File folder = outputDirPath.toFile();
            if (!folder.exists()) {
               folder.mkdirs();
            }

            // get the zip file content
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
               // get the zipped file list entry
               ZipEntry ze = zis.getNextEntry();

               while (ze != null) {
                  if (!ze.isDirectory()) { 
                     String fileName = ze.getName();
                     File newFile = Paths.get(outputDirPath.toString(), fileName).toFile();
   
                     logger.debug("unzipped: " + newFile.getAbsoluteFile());
   
                     // create all non exists folders
                     // else you will hit FileNotFoundException for compressed
                     // folder
                     new File(newFile.getParent()).mkdirs();
   
                     try (FileOutputStream fos = new FileOutputStream(newFile)) {
   
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                           fos.write(buffer, 0, len);
                        }
                     }
                  }
                  ze = zis.getNextEntry();
               }

               zis.closeEntry();
            }
         } else {
            throw new UtilException("Invalid zip file: " + zipFilePath);
         }
      } catch (Exception ex) {
         throw new UtilException("Error unziping file: " + zipFilePath, ex);
      }
   }
}
