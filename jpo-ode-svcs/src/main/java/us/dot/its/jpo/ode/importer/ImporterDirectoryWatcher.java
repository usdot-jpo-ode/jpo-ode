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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;

public class ImporterDirectoryWatcher implements Runnable {
   
   public enum ImporterFileType {
      LOG_FILE, JSON_FILE
   }

   private static final Logger logger = LoggerFactory.getLogger(ImporterDirectoryWatcher.class);

   private boolean watching;

   private ImporterProcessor importerProcessor;

   private Path inbox;
   private Path backup;
   private Path failed;

   private ScheduledExecutorService executor;

   private Integer timePeriod;

   public ImporterDirectoryWatcher(OdeProperties odeProperties, OdeKafkaProperties odeKafkaProperties, Path dir, Path backupDir, Path failureDir, ImporterFileType fileType, Integer timePeriod) {
      this.inbox = dir;
      this.backup = backupDir;
      this.failed = failureDir;
      this.watching = true;
      this.timePeriod = timePeriod;

      try {
         OdeFileUtils.createDirectoryRecursively(inbox);
         String msg = "Created directory {}";
         logger.debug(msg, inbox);
         OdeFileUtils.createDirectoryRecursively(failed);
         logger.debug(msg, failed);
         OdeFileUtils.createDirectoryRecursively(backup);
         logger.debug(msg, backup);
      } catch (IOException e) {
         logger.error("Error creating directory: " + inbox, e);
      }

      this.importerProcessor = new ImporterProcessor(odeProperties, odeKafkaProperties, fileType);
      
      executor = Executors.newScheduledThreadPool(1);
   }

   @Override
   public void run() {

      logger.info("Processing inbox directory {} every {} seconds.", inbox, timePeriod);

      // ODE-646: the old method of watching the directory used file
      // event notifications and was unreliable for large quantities of files
      // Watch directory for file events
      executor.scheduleWithFixedDelay(() -> importerProcessor.processDirectory(inbox, backup, failed),
          0, timePeriod, TimeUnit.SECONDS);
      
      try {
         // This line will only execute in the event that .scheduleWithFixedDelay() throws an error
         executor.awaitTermination(timePeriod, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         logger.error("Directory watcher polling loop interrupted!", e);
      }
   }

   public boolean isWatching() {
      return watching;
   }

   public void setWatching(boolean watching) {
      this.watching = watching;
   }

}
