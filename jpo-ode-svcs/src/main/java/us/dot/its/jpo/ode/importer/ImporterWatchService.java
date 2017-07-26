package us.dot.its.jpo.ode.importer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.BsmStreamDecoderPublisher;
import us.dot.its.jpo.ode.eventlog.EventLogger;

public class ImporterWatchService extends ImporterFileService implements Runnable {
   
   private static final Logger logger = LoggerFactory.getLogger(ImporterWatchService.class);

    private Path inbox;
    private Path backup;
    private OdeProperties odeProperties;

    public ImporterWatchService(
        OdeProperties odeProperties, 
        Path dir, 
        Path backupDir) {

        this.inbox = dir;
        this.backup = backupDir;
        this.odeProperties = odeProperties;
        
        init();
    }

    public void init() {

        // Create the inbox directory
        try {
            super.createDirectoryRecursively(inbox);
            logger.debug("Created directory {}", inbox);
            super.createDirectoryRecursively(backup);
            logger.debug("Created directory {}", backup);
        } catch (IOException e) {
            logger.error("Error creating directory: " + inbox, e);
        }

        // Process files already in the directory
        processExistingFiles();
    }

    public void processExistingFiles() {
        int count = 0;
        // Process files already in the directory
        logger.debug("Started processing existing files at location: {}", inbox);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inbox)) {

            if (stream == null) {
                throw new IOException("Directory stream failed to create (null) for " + inbox);
            }

            for (Path entry : stream) {
                logger.debug("Found a file to process: {}", entry.getFileName());
                processFile(entry);
                count++;
            }

            stream.close();
            logger.debug("Finished processing {} existing files at location: {}", count, inbox);
        } catch (Exception e) {
            logger.error("Error processing existing files.", e);
        }
    }

    public void processFile(Path filePath) {

        try (InputStream inputStream = new FileInputStream(filePath.toFile())) {

            EventLogger.logger.info("Processing file {}", filePath.toFile());

            BsmStreamDecoderPublisher coder = 
                    new BsmStreamDecoderPublisher(this.odeProperties, filePath);
            
            if (filePath.toString().endsWith(".hex") || filePath.toString().endsWith(".txt")) {
               coder.decodeHexAndPublish(inputStream);
            } else if (filePath.toString().endsWith(".json")) {
               coder.decodeJsonAndPublish(inputStream);
            } else {
               coder.decodeBinaryAndPublish(inputStream);
            }
        } catch (Exception e) {
            logger.error("Unable to open or process file: " + filePath, e);
        }

        try {
            super.backupFile(filePath, backup);
        } catch (IOException e) {
            logger.error("Unable to backup file: " + filePath, e);
        }
    }

    @Override
    public void run() {

        logger.info("Directory watcher service run initiated.");

        // Begin by processing all files already in the inbox
        processExistingFiles();

        // Create a generic watch service
        WatchService watcher = null;
        try {
            watcher = inbox.getFileSystem().newWatchService();
            if (watcher == null) {
                throw new IOException("Watch service null");
            }
            
            WatchKey keyForTrackedDir = inbox.register(watcher, ENTRY_MODIFY);
            if (keyForTrackedDir == null) {
                throw new IOException("Watch key null");
            }
        } catch (IOException e) {
            logger.error("Watch service failed to create: {}", e);
            return;
        }

        logger.info("Watch service active on {}", inbox);

        // Watch directory for file events
        while (true) {

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
                
                if (OVERFLOW == kind) {
                    continue;
                } else if (ENTRY_MODIFY == kind) {
                    logger.debug("Notable watch event kind: {}", event.kind());

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = inbox.resolve(ev.context());
                    logger.debug("File event on {}", filename);

                    try {
                        processFile(filename);
                    } catch (Exception e) {
                        logger.error("Error processing file: " + filename, e);
                    }
                } else {
                    logger.error("Unhandled watch event kind: {}", event.kind());
                }
            }
            
            boolean valid = wk.reset();
            if (!valid) {
                logger.error("ERROR: Watch key invalid. Stopping watch service on {}", inbox);
                break;
            }
        }
    }
}
