package us.dot.its.jpo.ode;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PreDestroy;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

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
   private static final int DEFAULT_NO_THREADS = 10;
   private static final String DEFAULT_SCHEMA = "default";

   public static void main(String[] args) throws MalformedObjectNameException, InterruptedException,
         InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      SpringApplication.run(OdeSvcsApplication.class, args);
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      SystemConfig mBean = new SystemConfig(DEFAULT_NO_THREADS, DEFAULT_SCHEMA);
      ObjectName name = new ObjectName("us.dot.its.jpo.ode:type=SystemConfig");
      mbs.registerMBean(mBean, name);
   }

   @Bean
   CommandLineRunner init(OdeProperties odeProperties) {
      return args -> {
         try {
            Files.createDirectory(Paths.get(odeProperties.getUploadLocationRoot()));
            Files.createDirectory(Paths.get(odeProperties.getUploadLocationBsm()));
            Files.createDirectory(Paths.get(odeProperties.getUploadLocationMessageFrame()));
         } catch (FileAlreadyExistsException fae) {
            logger.info("Upload directory already exisits: {}", fae);
         } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
         }
      };
   }

   @PreDestroy
   public void cleanup() {
       // Unused
   }

}
