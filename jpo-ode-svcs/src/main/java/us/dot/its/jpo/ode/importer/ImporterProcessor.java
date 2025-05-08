/*=============================================================================
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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher.LogFileToAsn1CodecPublisherException;
import us.dot.its.jpo.ode.importer.parser.LogFileParserFactory;
import us.dot.its.jpo.ode.importer.parser.LogFileParserFactory.LogFileParserFactoryException;

/**
 * The {@code ImporterProcessor} class is responsible for processing files within directories and subdirectories.
 * It detects the file format, handles compression if necessary, and publishes the contents of the file using
 * a configured {@link LogFileToAsn1CodecPublisher}. The processed files are then moved to the appropriate
 * directory based on the result of the processing.
 *
 * <p>The class can handle gzipped, zipped, and plain file formats. Within the handling of zip files, it is capable
 * of accessing and processing individual entries in the archive. Failed or successfully processed files are moved to
 * predefined failure or backup directories, respectively.
 */
@Slf4j
public class ImporterProcessor {

  private final int bufferSize;
  private final LogFileToAsn1CodecPublisher codecPublisher;
  private final ImporterFileType fileType;

  /**
   * Constructs an instance of ImporterProcessor for processing and encoding files
   * of a specific type. This class facilitates proper handling of log files and
   * their transformation into {@link us.dot.its.jpo.ode.model.OdeData} objects
   * based on the specified importer file type and buffer size.
   *
   * @param publisher  The publisher responsible for encoding and transferring log files
   *                   to their respective downstream systems or topics.
   * @param fileType   The type of file to be processed by this processor, which determines
   *                   the specific handling and encoding logic.
   * @param bufferSize The size of the buffer to be used during file processing operations,
   *                   impacting memory usage and performance.
   */
  public ImporterProcessor(LogFileToAsn1CodecPublisher publisher, ImporterFileType fileType, int bufferSize) {
    this.codecPublisher = publisher;
    this.bufferSize = bufferSize;
    this.fileType = fileType;
  }

  /**
   * Processes all the files in the given directory. If a subdirectory is encountered,
   * it recursively processes that subdirectory.
   *
   * <p>For each file, it attempts to parse the file into {@link us.dot.its.jpo.ode.model.OdeData} objects,
   * produce the parsed data to the message type's respective topics, and moves the processed file
   * to either the backup directory or the failure directory based on the processing result.</p>
   *
   * @param dir        The directory containing files to be processed. Must not be {@code null}.
   * @param backupDir  The directory where successfully processed files will be moved. Must not be {@code null}.
   * @param failureDir The directory where failed files will be moved. Must not be {@code null}.
   */
  public int processDirectory(Path dir, Path backupDir, Path failureDir) {
    int successCount = 0;
    // Process files already in the directory
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
      for (Path entry : stream) {
        if (entry.toFile().isDirectory()) {
          successCount += processDirectory(entry, backupDir, failureDir);
        } else {
          log.debug("Found a file to process: {}", entry.getFileName());
          var success = processFile(entry);
          if (success) {
            OdeFileUtils.backupFile(entry, backupDir);
            log.info("File moved to backup: {}", backupDir);
            successCount++;
          } else {
            OdeFileUtils.moveFile(entry, failureDir);
            log.info("File moved to failure directory: {}", failureDir);
          }
        }
      }
    } catch (IOException e) {
      log.error("Error processing files.", e);
    }
    log.debug("Processed {} files.", successCount);
    return successCount;
  }

  private boolean processFile(Path filePath) {
    FileFormat detectedFileFormat;
    try {
      detectedFileFormat = FileFormat.detectFileFormat(filePath);
      log.info("Treating as {} file", detectedFileFormat.toString());
    } catch (IOException e) {
      log.error("Failed to detect file type: {}", filePath, e);
      return false;
    }

    boolean success = true;
    try (InputStream inputStream = getInputStream(filePath, detectedFileFormat)) {
      if (detectedFileFormat == FileFormat.ZIP) {
        publishZipFiles(filePath, (ZipInputStream) inputStream);
      } else {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream, this.bufferSize)) {
          publishFile(filePath, bis);
        }
      }
    } catch (Exception e) {
      success = false;
      log.error("Failed to open or process file: {}", filePath, e);
    }

    return success;
  }

  private InputStream getInputStream(Path filePath, FileFormat fileFormat) throws IOException {
    return switch (fileFormat) {
      case GZIP -> new GZIPInputStream(new FileInputStream(filePath.toFile()));
      case ZIP -> new ZipInputStream(new FileInputStream(filePath.toFile()));
      default -> new FileInputStream(filePath.toFile());
    };
  }

  private void publishFile(Path filePath, BufferedInputStream bis) throws
      LogFileParserFactoryException, LogFileToAsn1CodecPublisherException {
    var fileName = filePath.getFileName().toString();
    var parser = LogFileParserFactory.getLogFileParser(fileName);
    codecPublisher.publish(bis, fileName, fileType, parser);
  }


  private void publishZipFiles(Path filePath, ZipInputStream inputStream)
      throws IOException, LogFileParserFactoryException, LogFileToAsn1CodecPublisherException {
    try (BufferedInputStream bis = new BufferedInputStream(inputStream, this.bufferSize)) {
      while (inputStream.getNextEntry() != null) {
        publishFile(filePath, bis);
      }
    }
  }
}
