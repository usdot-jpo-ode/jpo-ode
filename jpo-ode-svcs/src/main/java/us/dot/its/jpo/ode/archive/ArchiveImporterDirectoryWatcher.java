package us.dot.its.jpo.ode.archive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;

/**
 * Periodically imports the S3 archive files placed in the archive inbox directory, either through
 * a mounted volume or the {@code POST /upload/archive} endpoint.
 */
@Slf4j
@Component
@EnableScheduling
@ConditionalOnProperty(prefix = "ode.archive-importer", name = "enabled", havingValue = "true",
    matchIfMissing = true)
public class ArchiveImporterDirectoryWatcher {

  private final ArchiveImportProcessor processor;
  private final Path inboxPath;
  private final Path backupPath;
  private final Path failedPath;

  /**
   * Creates the inbox, backup and failed directories of the archive importer.
   *
   * @param fileImporterProperties provides the upload root and the backup and failed directories
   * @param properties             provides the archive inbox directory
   * @param processor              imports the files
   */
  public ArchiveImporterDirectoryWatcher(FileImporterProperties fileImporterProperties,
      ArchiveImporterProperties properties, ArchiveImportProcessor processor) {
    this.processor = processor;
    Path root = Paths.get(fileImporterProperties.getUploadLocationRoot());
    this.inboxPath = root.resolve(properties.getInboxDir());
    this.backupPath = root.resolve(fileImporterProperties.getBackupDir())
        .resolve(properties.getInboxDir());
    this.failedPath = root.resolve(fileImporterProperties.getFailedDir())
        .resolve(properties.getInboxDir());

    for (Path dir : new Path[] {inboxPath, backupPath, failedPath}) {
      try {
        Files.createDirectories(dir);
      } catch (IOException e) {
        log.error("Unable to create archive importer directory {}", dir, e);
      }
    }
    log.info("Archive importer inbox: {}", inboxPath);
  }

  /**
   * Imports the ready files in the inbox. Runs with a fixed delay, so a long import is never
   * started again before it finishes.
   */
  @Scheduled(fixedDelayString = "${ode.file-importer.time-period}", timeUnit = TimeUnit.SECONDS)
  public void run() {
    int imported = processor.processDirectory(inboxPath, backupPath, failedPath);
    if (imported > 0) {
      log.info("Imported {} archive files", imported);
    }
  }
}
