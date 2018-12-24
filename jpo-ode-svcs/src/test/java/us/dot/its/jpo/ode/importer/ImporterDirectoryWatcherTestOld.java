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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

@Ignore
public class ImporterDirectoryWatcherTestOld {

   ImporterDirectoryWatcher testImporterDirectoryWatcher;

   @Injectable
   OdeProperties injectableOdeProperties;
   @Mocked
   Path mockDir;
   @Injectable
   Path failureDir;
   @Injectable
   Path backupDir;
   @Injectable 
   Integer timePeriod = 5;

   @Mocked
   WatchKey mockWatchKey;
   @Mocked
   WatchService mockWatchService;
   @Mocked
   WatchEvent<Path> mockWatchEvent;

   @Capturing
   OdeFileUtils capturingOdeFileUtils;
   @Capturing
   ImporterProcessor capturingImporterProcessor;

   @Before
   public void createTestObject() {
      try {
         new Expectations() {
            {
               OdeFileUtils.createDirectoryRecursively((Path) any);
               times = 3;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      testImporterDirectoryWatcher = new ImporterDirectoryWatcher(injectableOdeProperties, mockDir, backupDir, failureDir, ImporterFileType.OBU_LOG_FILE, timePeriod);
      testImporterDirectoryWatcher.setWatching(false);
   }
   
   @Test
   public void testConstructorOdeUtilsException() {
      try {
         new Expectations() {
            {
               OdeFileUtils.createDirectoryRecursively((Path) any);
               result = new IOException("acceptionIsKindofAWord");
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      new ImporterDirectoryWatcher(injectableOdeProperties, mockDir, backupDir, failureDir, ImporterFileType.OBU_LOG_FILE, timePeriod);
   }

   @Test(timeout = 4000)
   public void runShouldCatchException() {
      try {
         new Expectations() {
            {
               mockDir.register((WatchService) any, (Kind<?>) any);
               result = null;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      testImporterDirectoryWatcher.run();
   }

   @Test(timeout = 4000)
   public void shouldRunNoProblems() {
      try {
         new Expectations() {
            {
               mockDir.register((WatchService) any, (Kind<?>) any);
               result = mockWatchKey;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      testImporterDirectoryWatcher.run();
   }

   @Test @Ignore
   public void pollDirectoryShouldInterruptThreadOnWatcherError() {
      try {
         new Expectations(Thread.class) {
            {
               mockWatchService.take();
               result = new InterruptedException("testException123");
               Thread.currentThread().interrupt();
               times = 1;
            }
         };
      } catch (InterruptedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterDirectoryWatcher.pollDirectory(mockWatchService);
   }

   @Test @Ignore
   public void pollDirectoryOverflowShouldContinue() {
      List<WatchEvent<Path>> pollEventsList = new ArrayList<WatchEvent<Path>>();
      pollEventsList.add(mockWatchEvent);
      try {
         new Expectations() {
            {
               mockWatchService.take();
               result = mockWatchKey;

               mockWatchKey.pollEvents();
               result = pollEventsList;

               mockWatchKey.reset();
               result = true;

               mockWatchEvent.kind();
               result = StandardWatchEventKinds.OVERFLOW;
               
               capturingImporterProcessor.processAndBackupFile((Path) any, (Path) any, (Path) any);
               times = 0;
            }
         };
      } catch (InterruptedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterDirectoryWatcher.pollDirectory(mockWatchService);
   }

   @Test @Ignore
   public void pollDirectoryEntryModifyShouldProcessFile() {
      List<WatchEvent<Path>> pollEventsList = new ArrayList<WatchEvent<Path>>();
      pollEventsList.add(mockWatchEvent);
      try {
         new Expectations() {
            {
               mockWatchService.take();
               result = mockWatchKey;

               mockWatchKey.pollEvents();
               result = pollEventsList;

               mockWatchKey.reset();
               result = false;

               mockWatchEvent.kind();
               result = StandardWatchEventKinds.ENTRY_MODIFY;

               capturingImporterProcessor.processAndBackupFile((Path) any, (Path) any, (Path) any);
               times = 1;
            }
         };
      } catch (InterruptedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterDirectoryWatcher.pollDirectory(mockWatchService);
   }
   
   @Test @Ignore
   public void pollDirectoryUnknownKindShouldThrowError() {
      List<WatchEvent<Path>> pollEventsList = new ArrayList<WatchEvent<Path>>();
      pollEventsList.add(mockWatchEvent);
      try {
         new Expectations() {
            {
               mockWatchService.take();
               result = mockWatchKey;

               mockWatchKey.pollEvents();
               result = pollEventsList;

               mockWatchKey.reset();
               result = false;

               mockWatchEvent.kind();
               result = StandardWatchEventKinds.ENTRY_DELETE;

               capturingImporterProcessor.processAndBackupFile((Path) any, (Path) any, (Path) any);
               times = 0;
            }
         };
      } catch (InterruptedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterDirectoryWatcher.pollDirectory(mockWatchService);
   }

}
