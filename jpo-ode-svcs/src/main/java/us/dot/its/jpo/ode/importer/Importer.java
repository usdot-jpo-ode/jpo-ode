package us.dot.its.jpo.ode.importer;

//import java.nio.file.InvalidPathException;
//import java.nio.file.Paths;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.bsm.BsmCoder;

public class Importer implements Runnable {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private OdeProperties odeProperties;
	private Path folder;
	private int interval;

	public Importer(OdeProperties odeProps)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.odeProperties = odeProps;
		
		String uploadLocation = this.odeProperties.getUploadLocation();
		try {
			folder = Paths.get(uploadLocation);
		} catch (Exception ex) {
			logger.error("Not a valid path: " + uploadLocation, ex);
		}

		logger.info("Watching folder: " + folder);

		interval = this.odeProperties.getImporterInterval();
		
		logger.info("Loading ASN1 Coder: {}", this.odeProperties.getAsn1CoderClassName());
		
		logger.info("Publishing to {}", OdeProperties.KAFKA_TOPIC_J2735_BSM);
	}

	public Path getFolder() {
		return folder;
	}

	public void setFolder(Path folder) {
		this.folder = folder;
	}

	@Override
	public void run() {
		boolean isRunning = false;
		// Sanity check - Check if path is a folder
		try {
			Boolean isFolder = (Boolean) Files.getAttribute(folder, "basic:isDirectory", NOFOLLOW_LINKS);
			logger.info("Watching directory: {}", folder);
			isRunning = true;
			if (!isFolder) {
				throw new IllegalArgumentException("Path: " + folder + " is not a folder");
			}
		} catch (IOException ioe) {
			logger.error("Error validating import location.", ioe);
		}

		// We obtain the file system of the Path
		FileSystem fs = folder.getFileSystem();
      BsmCoder bsmCoder = new BsmCoder(odeProperties);

		while (isRunning) {
			// We create the new WatchService using the new try() block
			try (WatchService service = fs.newWatchService()) {

				// We register the folder to the service
				// We watch for creation events
				folder.register(service, ENTRY_CREATE, ENTRY_MODIFY);

				// Start the infinite polling loop
				WatchKey key = null;
				key = service.take();

				// Dequeuing events
				Kind<?> kind = null;
				for (WatchEvent<?> watchEvent : key.pollEvents()) {
					// Get the type of the event
					kind = watchEvent.kind();
					if (OVERFLOW == kind) {
						continue; // loop
					} else if (ENTRY_CREATE == kind || ENTRY_MODIFY == kind) {
						// A new Path was created
						@SuppressWarnings("unchecked")
						WatchEvent<Path> watchEventCurrent = (WatchEvent<Path>) watchEvent;
						Path path = folder.resolve(watchEventCurrent.context());
						logger.info("New or modified file detected: {}", path);

						File bsmFile = path.toFile();
						
						int retryCount = 2;
						while (retryCount-- > 0) {
							try (InputStream inputStream = new FileInputStream(path.toFile())) {
								bsmCoder.decodeFromHexAndPublish(
										inputStream, OdeProperties.KAFKA_TOPIC_J2735_BSM);
								inputStream.close();
								bsmFile.delete();
								break;
							} catch (Exception e) {
								logger.info("unable to open file: " + path 
										+ " retrying " + retryCount + " more times", e);
								Thread.sleep(100);
							}
						}						
					}
				}

				if (!key.reset()) {
					isRunning = false; // end the loop
				}
			} catch (Exception e) {
				logger.error("Error running the importer.", e);
			}

		}

	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int importerInterval) {
		this.interval = importerInterval;
	}

	public OdeProperties getOdeProperties() {
		return odeProperties;
	}

	public void setOdeProperties(OdeProperties odeProperties) {
		this.odeProperties = odeProperties;
	}
}
