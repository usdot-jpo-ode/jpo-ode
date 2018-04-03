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
import us.dot.its.jpo.ode.importer.ImporterProcessor.ImporterFileType;
import us.dot.its.jpo.ode.util.FileUtils;

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
   ImporterFileType injectableImporterFileType = ImporterFileType.OBU_LOG_FILE;
   @Injectable
   Integer timePeriod = 5;

   @Capturing
   FileUtils capturingOdeFileUtils;
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
            FileUtils.createDirectoryRecursively((Path) any);
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
