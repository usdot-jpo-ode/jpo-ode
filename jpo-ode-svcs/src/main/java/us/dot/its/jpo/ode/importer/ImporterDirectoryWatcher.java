package us.dot.its.jpo.ode.importer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;

public class ImporterDirectoryWatcher implements Runnable {
   
   public enum ImporterFileType {
      OBU_LOG_FILE
   }

   private static final Logger logger = LoggerFactory.getLogger(ImporterDirectoryWatcher.class);

   private boolean watching;

   private ImporterProcessor importerProcessor;

   private Path inbox;
   private Path backup;
   private Path failed;

   private ScheduledExecutorService executor;

   private Integer timePeriod;

   public ImporterDirectoryWatcher(OdeProperties odeProperties, Path dir, Path backupDir, Path failureDir, ImporterFileType fileType, Integer timePeriod) {
      this.inbox = dir;
      this.backup = backupDir;
      this.failed = failureDir;
      this.watching = true;
      this.timePeriod = timePeriod;

      try {
         OdeFileUtils.createDirectoryRecursively(inbox);
         logger.debug("Created directory {}", inbox);
         OdeFileUtils.createDirectoryRecursively(failed);
         logger.debug("Created directory {}", failed);
         OdeFileUtils.createDirectoryRecursively(backup);
         logger.debug("Created directory {}", backup);
      } catch (IOException e) {
         logger.error("Error creating directory: " + inbox, e);
      }

      this.importerProcessor = new ImporterProcessor(odeProperties, fileType);
      
      executor = Executors.newScheduledThreadPool(1);
   }

   @Override
   public void run() {

      logger.info("Processing inbox directory {} every {} seconds.", inbox, timePeriod);

      // Watch directory for file events
      executor.scheduleWithFixedDelay(() -> {
         importerProcessor.processDirectory(inbox, backup, failed);
      }, 0, timePeriod, TimeUnit.SECONDS);
      
      try {
         // This line will only execute in the event that .scheduleWithFixedDelay() throws an error
         executor.awaitTermination(timePeriod, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         logger.error("Directory watcher polling loop interrupted!", e);
      }

      // ODE-646: the old method of watching the directory used file
      // event notifications and was unreliable for large quantities of files
      
      // while (isWatching()) {
      // //pollDirectory(watcher);
      // pollDirectoryNew();
      // }
   }

   @Deprecated // TODO - replaced by periodic checking
   public void pollDirectory(WatchService watcher) {
      // wait for key to be signaled
      WatchKey wk;
      try {
         wk = watcher.take();
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         logger.error("[CRITICAL] Watch service interrupted: {}", e);
         return;
      }

      for (WatchEvent<?> event : wk.pollEvents()) {
         Kind<?> kind = event.kind();

         if (ENTRY_MODIFY == kind) {
            logger.debug("Notable watch event kind: {}", event.kind());

            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path filename = inbox.resolve(ev.context());
            logger.debug("File event on {}", filename);

            importerProcessor.processAndBackupFile(filename, backup, failed);
         } else if (OVERFLOW == kind) {
            continue;
         } else {
            logger.error("Unhandled watch event kind: {}", event.kind());
         }
      }

      if (!wk.reset()) {
         logger.error("Failed to reset directory watcher.");
      }
   }

   public boolean isWatching() {
      return watching;
   }

   public void setWatching(boolean watching) {
      this.watching = watching;
   }

}
