package us.dot.its.jpo.ode;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import us.dot.its.jpo.ode.storage.StorageException;

@SpringBootApplication
@EnableConfigurationProperties(OdeProperties.class)
public class OdeSvcsApplication {

   private Logger logger = LoggerFactory.getLogger(this.getClass());

   public static void main(String[] args) {
      SpringApplication.run(OdeSvcsApplication.class, args);
   }

   @Bean
   CommandLineRunner init(OdeProperties odeProperties) {
      return (args) -> {
         try {
            Files.createDirectory(Paths.get(odeProperties.getUploadLocation()));
         } catch (FileAlreadyExistsException fae) {
            logger.info("Upload directory already exisits");
         } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
         }
      };
   }

   @PreDestroy
   public void clanup() {
   }

}
