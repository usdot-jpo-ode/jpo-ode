package us.dot.its.jpo.ode.importer;

//import java.nio.file.InvalidPathException;
//import java.nio.file.Paths;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.bsm.BsmCoder;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

@Service
public class Importer implements Runnable {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OdeProperties odeProperties;
	private Path folder;
	private int interval;

	public int getInterval() {
		return interval;
	}

	public void setInterval(int importerInterval) {
		this.interval = importerInterval;
	}

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
	}

	public Path getFolder() {
		return folder;
	}

	public void setFolder(Path folder) {
		this.folder = folder;
	}

	@Override
	public void run() {
		// Sanity check - Check if path is a folder
		try {
			Boolean isFolder = (Boolean) Files.getAttribute(folder, "basic:isDirectory", NOFOLLOW_LINKS);
			if (!isFolder) {
				throw new IllegalArgumentException("Path: " + folder + " is not a folder");
			}
		} catch (IOException ioe) {
			logger.error("Error validating import location.", ioe);
		}

		// We obtain the file system of the Path
		FileSystem fs = folder.getFileSystem();

		// We create the new WatchService using the new try() block
		try (WatchService service = fs.newWatchService()) {

			// We register the folder to the service
			// We watch for creation events
			folder.register(service, ENTRY_CREATE, ENTRY_MODIFY);

			// Start the infinite polling loop
			WatchKey key = null;
			while (true) {
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
						Path path = watchEventCurrent.context();
						logger.info("New or modified file detected: {}", path);

						BsmCoder bsmCoder = new BsmCoder(odeProperties);
						InputStream inputStream = new FileInputStream(path.toFile());
						
						J2735Bsm j2735BSM = bsmCoder.decodeFromHex(inputStream);
						bsmCoder.publish(BsmCoder.BSM_OBJECTS, j2735BSM);
					}
				}

				if (!key.reset()) {
					break; // loop
				}
			}

		} catch (Exception e) {
			logger.error("Error running the importer.", e);
		}

	}


}
