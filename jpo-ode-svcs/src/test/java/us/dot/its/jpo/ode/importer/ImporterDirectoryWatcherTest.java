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

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

public class ImporterDirectoryWatcherTest {

   @Tested
   ImporterDirectoryWatcher testImporterDirectoryWatcher;

   @Injectable
   OdeProperties injectableOdeProperties;
   @Injectable
   Path inbox;
   @Injectable
   Path failureDir;
   @Injectable
   Path backupDir;
   @Injectable
   ImporterFileType injectableImporterFileType = ImporterFileType.LEAR_LOG_FILE;
   @Injectable
   Integer timePeriod = 5;

   @Capturing
   OdeFileUtils capturingOdeFileUtils;
   @Capturing
   ImporterProcessor capturingImporterProcessor;
   @Capturing
   Executors capturingExecutors;

   @Mocked
   ScheduledExecutorService mockScheduledExecutorService;

   @Before
   public void testConstructor() throws IOException {
      new Expectations() {
         {
            OdeFileUtils.createDirectoryRecursively((Path) any);
            times = 3;

            Executors.newScheduledThreadPool(1);
            result = mockScheduledExecutorService;
         }
      };
   }

   @Test
   public void testRun() throws InterruptedException {
      new Expectations() {
         {
            mockScheduledExecutorService.scheduleWithFixedDelay((Runnable) any, anyLong, anyLong, TimeUnit.SECONDS);

            mockScheduledExecutorService.awaitTermination(anyLong, TimeUnit.SECONDS);
         }
      };

      testImporterDirectoryWatcher.run();
   }

}
