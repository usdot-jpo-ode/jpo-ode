package us.dot.its.jpo.ode.importer;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.FileAsn1CodecPublisher;
import us.dot.its.jpo.ode.coder.FileDecoderPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

public class ImporterProcessorTest {

   @Tested
   ImporterProcessor testImporterProcessor;

   @Injectable
   OdeProperties injectableOdeProperties;
   @Injectable
   ImporterFileType injectableImporterDirType = ImporterFileType.BSM_LOG_FILE;

   @Capturing
   FileAsn1CodecPublisher capturingFileAsn1CodecPublisher;
   @Capturing
   OdeFileUtils capturingOdeFileUtils;



   @Mocked
   Path mockFile;
   @Mocked
   Path mockFileBackup;
   
   @Injectable
   Path injectableDir;
   @Injectable
   Path injectableBackupDir;
   

   @Test
   public void processExistingFilesShouldCatchExceptionFailedToCreateStream() {

      try {
         new Expectations(Files.class) {
            {
               Files.newDirectoryStream((Path) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterProcessor.processDirectory(injectableDir, injectableBackupDir);
   }
 
   @Test
   public void processExistingFilesShouldProcessOneFile(@Mocked DirectoryStream<Path> mockDirectoryStream,
         @Mocked Iterator<Path> mockIterator) {

      try {
         new Expectations(Files.class) {
            {
               Files.newDirectoryStream((Path) any);
               result = mockDirectoryStream;
               mockDirectoryStream.iterator();
               result = mockIterator;
               mockIterator.hasNext();
               returns(true, false);
               mockIterator.next();
               returns(mockFile, null);
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterProcessor.processDirectory(injectableDir, injectableBackupDir);
   }

   @Test
   public void processAndBackupFileFileShouldCatchExceptionStream() {

      try {
         new Expectations(FileInputStream.class) {
            {
               new FileInputStream((File) any);
               result = new IOException("testException123");
            }
         };
      } catch (FileNotFoundException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      testImporterProcessor.processAndBackupFile(mockFile, injectableBackupDir);
   }

   @Test
   public void processAndBackupFileShouldOdeFileUtilsException() {

      
      
      try {
         new Expectations(FileInputStream.class) {
            {
               new FileInputStream((File) any);
               result = null;
               capturingFileAsn1CodecPublisher.publishFile((Path) any, (BufferedInputStream) any, ImporterFileType.BSM_LOG_FILE);
               times = 1;

            
            }
         };
      } catch (Exception e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      testImporterProcessor.processAndBackupFile(mockFile, injectableBackupDir);
   }


}
