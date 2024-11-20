/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.importer;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

public class ImporterProcessorTest {

   @Tested
   ImporterProcessor testImporterProcessor;

   @Injectable
   ImporterFileType injectableImporterDirType = ImporterFileType.LOG_FILE;

//   @Capturing
//   FileAsn1CodecPublisher capturingFileAsn1CodecPublisher;
//   @Capturing
//   OdeFileUtils capturingOdeFileUtils;



   @Mocked
   Path mockFile;
   @Mocked
   Path mockFileBackup;
   
   @Injectable
   Path injectableDir;
   @Injectable
   Path injectableBackupDir;
   @Injectable
   Path injectableFailureDir;
   

   @Test @Disabled
   public void processExistingFilesShouldCatchExceptionFailedToCreateStream() {

      try {
         new Expectations() {
            {
               Files.newDirectoryStream((Path) any);
               result = new IOException("testException123");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterProcessor.processDirectory(injectableDir, injectableBackupDir, injectableFailureDir);
   }
 
   @Test @Disabled
   public void processExistingFilesShouldProcessOneFile(@Mocked DirectoryStream<Path> mockDirectoryStream,
         @Mocked Iterator<Path> mockIterator) {

      try {
         new Expectations() {
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

      testImporterProcessor.processDirectory(injectableDir, injectableBackupDir, injectableFailureDir);
   }

   @Test @Disabled
   public void processAndBackupFileFileShouldCatchExceptionStream() {

      try {
         new Expectations() {
            {
               new FileInputStream((File) any);
               result = new IOException("testException123");
            }
         };
      } catch (FileNotFoundException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      testImporterProcessor.processAndBackupFile(mockFile, injectableBackupDir, injectableFailureDir);
   }

}
