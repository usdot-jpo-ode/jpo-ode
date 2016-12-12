package us.dot.its.jpo.ode.importer;

//import java.nio.file.InvalidPathException;
//import java.nio.file.Paths;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.storage.FileSystemStorageService;
import us.dot.its.jpo.ode.util.JsonUtils;

public class Importer implements Runnable {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Path folder;
	private int interval;
	private Asn1Plugin asn1Coder;

	public int getInterval() {
		return interval;
	}

	public void setInterval(int importerInterval) {
		this.interval = importerInterval;
	}

	public Importer(OdeProperties odeProps)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String uploadLocation = odeProps.getUploadLocation();
		try {
			folder = Paths.get(uploadLocation);
		} catch (Exception ex) {
			logger.error("Not a valid path: " + uploadLocation, ex);
		}

		logger.info("Watching folder: " + folder);

		interval = odeProps.getImporterInterval();
		
		logger.info("Loading ASN1 Coder: {}", odeProps.getAsn1CoderClassName());
		this.asn1Coder = (Asn1Plugin) PluginFactory.getPluginByName(odeProps.getAsn1CoderClassName());
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
						Path newPath = watchEventCurrent.context();
						logger.info("New file detected: {}", newPath);

						String topicName = "BSM";
						String line = null;
						try (Scanner scanner = new Scanner(newPath.toAbsolutePath())) {
							while (scanner.hasNextLine()) {
								line = scanner.nextLine();
								Asn1Object bsm;
								String encoded;
								try {
									bsm = (Asn1Object) JsonUtils.fromJson(line, J2735Bsm.class);
									logger.info("Read JSON data: {}", bsm);
									encoded = asn1Coder.UPER_EncodeHex(bsm);
									logger.info("Encoded data: {}", encoded);
								} catch (Exception e) {
									logger.warn("Message is not JSON. Assuming HEX...", e);
									encoded = line;
								}

								J2735Bsm decoded = (J2735Bsm) asn1Coder.UPER_DecodeHex(encoded);
								/*
								 * Send decoded.toJson() to kafka queue Receive from same Kafka
								 * topic queue
								 */
								FileSystemStorageService.produceMessage(topicName, decoded);
								FileSystemStorageService.consumeMessage(topicName);
								logger.info(decoded.toJson());
							}
						} catch (Exception e) {
							logger.error("Error decoding data: " + line, e);;
						}

						// Oulogger.info.out.println("New folder created: " +
						// newPath);
						// TODO: handle file here...
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
