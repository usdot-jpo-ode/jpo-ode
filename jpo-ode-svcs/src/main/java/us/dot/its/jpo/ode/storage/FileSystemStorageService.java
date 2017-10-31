package us.dot.its.jpo.ode.storage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;

@Service
public class FileSystemStorageService implements StorageService {
    private static Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);

    private Path rootLocation;
    private Path logFileLocation;

    @Autowired
    public FileSystemStorageService(OdeProperties properties) {

        this.rootLocation = Paths.get(properties.getUploadLocationRoot());
        this.logFileLocation = Paths.get(properties.getUploadLocationRoot(), 
           properties.getUploadLocationObuLog());

        logger.info("Upload location (root): {}", this.rootLocation);
        logger.info("Upload location (OBU log file): {}", this.logFileLocation);
    }

    @Override
    public void store(MultipartFile file, String type) {

        // Discern the destination path via the file type (bsm or messageFrame)
        Path path;
        if (("bsmlog").equals(type) || ("obulog").equals(type)) {
           path = this.logFileLocation.resolve(file.getOriginalFilename());
        } else {
            EventLogger.logger.info("File type unknown: {} {}", type, file.getName());
            throw new StorageException("File type unknown: " + type + " " + file.getName());
        }

        // Check file is not empty
        if (file.isEmpty()) {
            EventLogger.logger.info("File is empty: {}", path);
            throw new StorageException("File is empty: " + path);
        }

        // Check file does not already exist (if so, delete existing)
        try {
            EventLogger.logger.info("Deleting existing file: {}", path);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            EventLogger.logger.info("Failed to delete existing file: {} ", path);
            throw new StorageException("Failed to delete existing file: " + path, e);
        }

        // Copy the file to the relevant directory
        try {
            logger.debug("Copying file {} to {}", file.getOriginalFilename(), path);
            EventLogger.logger.info("Copying file {} to {}", file.getOriginalFilename(), path);
            Files.copy(file.getInputStream(), path);
        } catch (Exception e) {
            EventLogger.logger.info("Failed to store file in shared directory {}", path);
            throw new StorageException("Failed to store file in shared directory " + path, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            EventLogger.logger.info("Failed to read files stored in {}", this.rootLocation);
            throw new StorageException("Failed to read files stored in " + this.rootLocation, e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
        EventLogger.logger.info("Deleting {}", this.rootLocation);
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            EventLogger.logger.info("Failed to initialize storage service {}", this.rootLocation);
            throw new StorageException("Failed to initialize storage service " + this.rootLocation, e);
        }
    }

    public Path getRootLocation() {
        return rootLocation;
    }

    public void setRootLocation(Path rootLocation) {
        this.rootLocation = rootLocation;
    }
}
