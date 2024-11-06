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
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.time.Duration;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

@Disabled
public class ImporterDirectoryWatcherTestOld {

   ImporterDirectoryWatcher testImporterDirectoryWatcher;

   @Injectable
   OdeProperties injectableOdeProperties;
   @Injectable
   OdeKafkaProperties odeKafkaProperties;
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

   @BeforeEach
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
      testImporterDirectoryWatcher = new ImporterDirectoryWatcher(injectableOdeProperties, odeKafkaProperties, mockDir, backupDir, failureDir, ImporterFileType.LOG_FILE, timePeriod);
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
      new ImporterDirectoryWatcher(injectableOdeProperties, odeKafkaProperties, mockDir, backupDir, failureDir, ImporterFileType.LOG_FILE, timePeriod);
   }

   @Test
   public void runShouldCatchException() {
      assertTimeout(Duration.ofMillis(4000), () -> {
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
      });
   }

   @Test
   @Timeout(4)
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

}
