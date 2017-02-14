package us.dot.its.jpo.ode.importer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import static java.nio.file.StandardWatchEventKinds.*;

import us.dot.its.jpo.ode.coder.AbstractCoder;
import us.dot.its.jpo.ode.eventlog.EventLogger;

public class ImporterWatchService extends ImporterFileService implements Runnable {

    private Path inbox;
    private Path backup;
    private AbstractCoder coder;
    private Logger logger;
    private String filetypes;
    private String topic;

    public ImporterWatchService(Path dir, Path backupDir, AbstractCoder coder, Logger logger, String filetypes,
            String kafkaTopic) {

        this.inbox = dir;
        this.backup = backupDir;
        this.coder = coder;
        this.logger = logger;
        this.filetypes = filetypes; // "*.{uper, bsm, bin, hex}"
        this.topic = kafkaTopic;
        init();
    }

    public void init() {

        // Create the inbox directory
        try {
            super.createDirectoryRecursively(inbox);
            logger.debug("IMPORTER - Created directory {}", inbox);
        } catch (IOException e) {
            logger.error("IMPORTER -  Error creating directory ({}): {}", inbox, e);
        }

        // Process files already in the directory
        processExistingFiles();
    }

    public void processExistingFiles() {
        // Process files already in the directory
        logger.debug("IMPORTER - Processing existing files at location: {}", inbox);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inbox, filetypes)) {

            if (stream == null) {
                throw new IOException("Directory stream failed to create (null) for " + inbox);
            }

            for (Path entry : stream) {
                logger.debug("Found a file to process: {}", entry.getFileName());
                processFile(entry);
            }

            stream.close();
        } catch (Exception e) {
            logger.error("IMPORTER -  Error processing existing files.", e);
        }
    }

    public void processFile(Path filePath) {

        try (InputStream inputStream = new FileInputStream(filePath.toFile())) {

            EventLogger.logger.info("Processing file {}", filePath.toFile());

            if ( filePath.toString().endsWith("uper") || filePath.toString().endsWith("bin") ) {
                coder.decodeFromStreamAndPublish(inputStream, topic);
            } else if (filePath.toString().endsWith("hex")) {
                coder.decodeFromHexAndPublish(inputStream, topic);
            } else {
                throw new IOException("Unknown file extension.");
            }
        } catch (IOException e) {
            logger.error("IMPORTER - Unable to open file: {}", e);
        }

        try {
            super.backupFile(filePath, backup);
        } catch (IOException e) {
            logger.error("IMPORTER - Unable to backup file: {}", e);
        }
    }

    @Override
    public void run() {

        logger.info("IMPORTER - Directory watcher service run initiated.");
        
        // Begin by processing all files already in the inbox
        processExistingFiles();
        
        // Create a generic watch service
        WatchService watcher = null;
        try {
            watcher = FileSystems.getDefault().newWatchService();
            if (watcher == null) {
                throw new IOException("Watch service null");
            }
        } catch (IOException e) {
            logger.error("IMPORTER - Watch service failed to create: {}", e);
            return;
        }
        
        // Tell the watch service to track the inbox directory
        WatchKey key = null;
        try {
            key = inbox.register(watcher,ENTRY_CREATE,ENTRY_MODIFY);
            if (key == null) {
                throw new IOException("Watch key null");
            }
        } catch (IOException e) {
            logger.error("IMPORTER - Watch key failed to register: {}", e);
            return;
        }
        
        // Watch directory for file events
        while (true) {

            // wait for key to be signaled
            try {
                key = watcher.take();
            } catch (InterruptedException e) {
                logger.error("[CRITICAL] IMPORTER - Watch service interrupted: {}", e);
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();

                try {
                    processFile(filename);
                } catch (Exception e) {
                    logger.error("IMPORTER - Error processing file: {}", e);
                    return;
                }
            }
        }
    }
}
  
