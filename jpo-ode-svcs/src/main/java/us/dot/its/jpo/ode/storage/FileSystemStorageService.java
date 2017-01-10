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

@Service
public class FileSystemStorageService implements StorageService {
   private static Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);
   private Logger data = LoggerFactory.getLogger("data");

   private OdeProperties properties;
   private final Path rootLocation;

   @Autowired
   public FileSystemStorageService(OdeProperties properties) {
      this.properties = properties;

      this.rootLocation = Paths.get(this.properties.getUploadLocation());
      logger.info("Upload location: {}", this.rootLocation);
      data.info("File System storage service started.", this.rootLocation);
   }

   @Override
   public void store(MultipartFile file) {
      Path path = this.rootLocation.resolve(file.getOriginalFilename());
      try {
         Files.deleteIfExists(path);
         data.info("Trying to delete file in shared directory", this.rootLocation);
      } catch (IOException e) {
    	  data.info("Failed to delete existing file in shared directory", this.rootLocation);
    	  throw new StorageException("Failed to delete existing file " + path, e);
      }
      try {
         if (file.isEmpty()) {
        	 data.info("Failed to store empty file", this.rootLocation);
        	 throw new StorageException("Failed to store empty file " + path);
         }
         Files.copy(file.getInputStream(), path);
         data.info("Attempting to copy file into shared directory", this.rootLocation);
      } catch (IOException e) {
    	  data.info("Failed to store file in shared directory", this.rootLocation);
    	  throw new StorageException("Failed to store file " + path, e);
      }
   }

   @Override
   public Stream<Path> loadAll() {
      try {
         return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
               .map(path -> this.rootLocation.relativize(path));
      } catch (IOException e) {
    	  data.info("Failed to read stored files", this.rootLocation);
    	  throw new StorageException("Failed to read stored files", e);
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
         if (resource.exists() || resource.isReadable()) {
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
   }

   @Override
   public void init() {
      try {
         Files.createDirectory(rootLocation);
      } catch (IOException e) {
    	  data.info("Failed to initialize storage service",this.rootLocation);
    	  throw new StorageException("Could not initialize storage", e);
      }
   }
}
