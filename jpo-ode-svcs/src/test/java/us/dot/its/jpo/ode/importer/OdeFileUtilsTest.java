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

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.importer.OdeFileUtils;

public class OdeFileUtilsTest {
   
   @Injectable
   Path dir;
   @Mocked
   Path backupDir;

   @Test
   public void createDirectoryRecursivelyShouldThrowExceptionDirDoesNotExist() {
       try {
           OdeFileUtils.createDirectoryRecursively(dir);
           fail("Expected IOException directory does not exist");
       } catch (Exception e) {
           assertEquals(IOException.class, e.getClass());
           assertEquals("Failed to verify directory creation - directory does not exist.", e.getMessage());
       }
   }

   @Test
   public void createDirectoryRecursivelyShouldThrowExceptionUnableToCreateDirectory(@Mocked final Files unused) {

       try {
           new Expectations() {
               {
                   Files.createDirectories((Path) any);
                   result = new IOException("testException123");
               }
           };
       } catch (IOException e) {
           fail("Unexpected exception in expectations block: " + e);
       }

       try {
          OdeFileUtils.createDirectoryRecursively(dir);
           fail("Expected IOException while trying to create directory:");
       } catch (Exception e) {
           assertEquals(IOException.class, e.getClass());
           assertTrue(e.getMessage().startsWith("Exception while trying to create directory:"));
       }
   }

   @Test
   public void testCreateDirectoryRecursively() {
       new Expectations() {
           {
               dir.toFile().exists();
               result = true;
           }
       };

       try {
          OdeFileUtils.createDirectoryRecursively(dir);
       } catch (Exception e) {
           fail("Unexpected exception: " + e);
       }
   }

   @Test
   public void backupFileShouldThrowExceptionBackupDirDoesNotExist(@Mocked Path mockFile) {

       new Expectations() {
           {
               backupDir.toFile().exists();
               result = false;
           }
       };

       try {
          OdeFileUtils.backupFile(mockFile, backupDir);
           fail("Expected IOException while trying to backup file:");
       } catch (Exception e) {
           assertEquals(IOException.class, e.getClass());
           assertTrue(e.getMessage().startsWith("Backup directory does not exist:"));
       }
   }

   @Test
   public void backupFileShouldThrowExceptionUnableToMoveFile(@Mocked final Files unused, @Injectable Path mockFile) {

       try {
           new Expectations() {
               {
                   backupDir.toFile().exists();
                   result = true;

                   Files.move((Path) any, (Path) any, (CopyOption) any);
                   result = new IOException("testException123");
               }
           };
       } catch (IOException e) {
           fail("Unexpected exception in expectations block: " + e);
       }

       try {
          OdeFileUtils.backupFile(mockFile, backupDir);
           fail("Expected IOException while trying to move file:");
       } catch (Exception e) {
           assertEquals(IOException.class, e.getClass());
           assertTrue(e.getMessage().startsWith("Unable to move file to backup:"));
       }
   }

   @Test
   public void testBackupFile(@Mocked final Files unused, @Injectable Path mockFile) {

       new Expectations() {
           {
               backupDir.toFile().exists();
               result = true;
           }
       };

       try {
          OdeFileUtils.backupFile(mockFile, backupDir);
       } catch (Exception e) {
           fail("Unexpected exception: " + e);
       }
   }

}
