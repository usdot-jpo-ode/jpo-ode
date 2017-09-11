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
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

public class ImporterDirectoryWatcherTest {

   ImporterDirectoryWatcher testImporterDirectoryWatcher;

   @Injectable
   OdeProperties injectableOdeProperties;
   @Mocked
   Path mockDir;
   @Injectable
   Path backupDir;

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
               times = 2;
            }
         };
      } catch (IOException e) {
         fail("Unexpected exception in expectations block: " + e);
      }
      testImporterDirectoryWatcher = new ImporterDirectoryWatcher(injectableOdeProperties, mockDir, backupDir, ImporterFileType.BSM_LOG_FILE);
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
      new ImporterDirectoryWatcher(injectableOdeProperties, mockDir, backupDir, ImporterFileType.BSM_LOG_FILE);
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

   @Test
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

   @Test
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
               
               capturingImporterProcessor.processAndBackupFile((Path) any, (Path) any);
               times = 0;
            }
         };
      } catch (InterruptedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterDirectoryWatcher.pollDirectory(mockWatchService);
   }

   @Test
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

               capturingImporterProcessor.processAndBackupFile((Path) any, (Path) any);
               times = 1;
            }
         };
      } catch (InterruptedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterDirectoryWatcher.pollDirectory(mockWatchService);
   }
   
   @Test
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

               capturingImporterProcessor.processAndBackupFile((Path) any, (Path) any);
               times = 0;
            }
         };
      } catch (InterruptedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      testImporterDirectoryWatcher.pollDirectory(mockWatchService);
   }

}
