/*============================================================================
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

package us.dot.its.jpo.ode.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import us.dot.its.jpo.ode.archive.ArchiveImporterProperties;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;

/**
 * A service class responsible for file storage and management operations in the filesystem.
 * It provides methods to store, load, retrieve, and delete files. This service is implemented
 * to handle specific log file types and maintain files in organized directories.
 */
@Service
@Slf4j
public class FileSystemStorageService implements StorageService {

  private static final String PARTIAL_SUFFIX = ".part";
  private static final String INCOMING_DIR = ".incoming";

  private final Path logFileLocation;
  private final Path archiveFileLocation;
  private final Path incomingLocation;

  /**
   * Constructs a FileSystemStorageService instance and initializes the file storage locations
   * based on the provided properties.
   *
   * @param properties         The configuration properties used to determine the root and OBU log
   *                           file upload locations.
   * @param archiveProperties  The archive importer properties, which determine the archive inbox.
   */
  @Autowired
  public FileSystemStorageService(FileImporterProperties properties,
      ArchiveImporterProperties archiveProperties) {

    this.logFileLocation = Paths.get(properties.getUploadLocationRoot(),
        properties.getObuLogUploadLocation());
    this.archiveFileLocation = Paths.get(properties.getUploadLocationRoot(),
        archiveProperties.getInboxDir());
    // Uploads are written here first, outside the directories the importers scan
    this.incomingLocation = Paths.get(properties.getUploadLocationRoot(), INCOMING_DIR);

    log.info("Upload location (OBU log file): {}", this.logFileLocation);
    log.info("Upload location (S3 archive file): {}", this.archiveFileLocation);
  }

  /**
   * Stores a given MultipartFile in the appropriate directory based on the provided file type.
   *
   * <p>The method determines the destination path for the file based on its {@link LogFileType}.
   * Only the last element of the original file name is used, so a file cannot be written outside
   * the destination directory. The file is first written to a {@code .part} file in the
   * {@code .incoming} directory of the upload root, which the importers do not scan, and then moved
   * into the destination directory, so the importers never read a partially written file. An
   * existing file with the same name is replaced. If any of these operations fail, a {@link StorageException} is thrown.
   *
   * @param file The file to be stored. Must not be empty.
   * @param type The type of the file, which determines the destination path. Expected types include
   *             {@code LogFileType.BSM}, {@code LogFileType.OBU}, {@code LogFileType.ARCHIVE} or
   *             {@code LogFileType.UNKNOWN}.
   *
   * @throws StorageException If the file type is unknown, the file name is invalid, the file is
   *                          empty, or an error occurs during file storage.
   */
  @Override
  public void store(MultipartFile file, LogFileType type) {

    // Discern the destination directory via the file type
    Path directory = switch (type) {
      case BSM, OBU -> this.logFileLocation;
      case ARCHIVE -> this.archiveFileLocation;
      default -> {
        log.error("File type unknown: {} {}", type, file.getName());
        throw new StorageException("File type unknown: " + type + " " + file.getName());
      }
    };
    Path path = resolveFileName(directory, file.getOriginalFilename());

    // Check file is not empty
    if (file.isEmpty()) {
      log.error("File is empty: {}", path);
      throw new StorageException("File is empty: " + path);
    }

    // Copy the file to a temporary name, then rename it, replacing any existing file
    Path partialPath = incomingLocation.resolve(
        type.name().toLowerCase() + "-" + path.getFileName() + PARTIAL_SUFFIX);
    try {
      log.info("Copying file {} to {}", file.getOriginalFilename(), path);
      Files.createDirectories(incomingLocation);
      Files.createDirectories(directory);
      Files.copy(file.getInputStream(), partialPath, StandardCopyOption.REPLACE_EXISTING);
      Files.move(partialPath, path, StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.ATOMIC_MOVE);
    } catch (Exception e) {
      log.error("Failed to store file in shared directory {}", path);
      deletePartialFile(partialPath);
      throw new StorageException("Failed to store file in shared directory " + path, e);
    }
  }

  private static Path resolveFileName(Path directory, String originalFilename) {
    if (originalFilename == null || originalFilename.isBlank()) {
      throw new StorageException("File name is missing");
    }
    Path fileName;
    try {
      // Browsers may send a full client path, keep only its last element
      fileName = Paths.get(originalFilename.replace('\\', '/')).getFileName();
    } catch (RuntimeException e) {
      throw new StorageException("Invalid file name: " + originalFilename, e);
    }
    if (fileName == null || fileName.toString().startsWith(".")) {
      throw new StorageException("Invalid file name: " + originalFilename);
    }
    Path path = directory.resolve(fileName).normalize();
    if (!path.startsWith(directory.normalize())) {
      throw new StorageException("Invalid file name: " + originalFilename);
    }
    return path;
  }

  private static void deletePartialFile(Path partialPath) {
    try {
      Files.deleteIfExists(partialPath);
    } catch (IOException e) {
      log.warn("Unable to delete partial file {}", partialPath);
    }
  }
}
