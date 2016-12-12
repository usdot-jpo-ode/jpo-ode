package us.dot.its.jpo.ode;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import us.dot.its.jpo.ode.bsm.BsmMessagePrinter;
import us.dot.its.jpo.ode.importer.Importer;
import us.dot.its.jpo.ode.storage.StorageException;

@SpringBootApplication
@EnableConfigurationProperties(OdeProperties.class)
public class OdeSvcsApplication {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private ExecutorService importer;
	private static SerializableMessageProducerPool<String, byte[]> messageProducerPool;
	private static SerializableMessageConsumerPool<String, byte[]> messageConsumerPool;

	public static void main(String[] args) {
		SpringApplication.run(OdeSvcsApplication.class, args);
	}

	@Bean
	CommandLineRunner init(OdeProperties odeProperties) {
		return (args) -> {
			odeProperties.init();
			try {
				Files.createDirectory(Paths.get(odeProperties.getUploadLocation()));
			} catch (FileAlreadyExistsException fae) {
				logger.info("Upload directory already exisits");
			} catch (IOException e) {
				throw new StorageException("Could not initialize storage", e);
			}

			messageProducerPool = new SerializableMessageProducerPool<String, byte[]>(odeProperties);

			messageConsumerPool = new SerializableMessageConsumerPool<String, byte[]>(
					odeProperties.getHostId(), new BsmMessagePrinter(), odeProperties);

			
			importer = Executors.newSingleThreadExecutor();
			
			importer.submit(new Importer(odeProperties));
		};
	}

	public static SerializableMessageProducerPool<String, byte[]> getMessageProducerPool() {
		return messageProducerPool;
	}

	public static SerializableMessageConsumerPool<String, byte[]> getMessageConsumerPool() {
		return messageConsumerPool;
	}
	
	@PreDestroy
	public void clanup() {
		importer.shutdown();
	}

}
