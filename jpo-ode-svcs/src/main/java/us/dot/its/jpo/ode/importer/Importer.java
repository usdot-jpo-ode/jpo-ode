package us.dot.its.jpo.ode.importer;

//import java.nio.file.InvalidPathException;
//import java.nio.file.Paths;

//import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
//import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.bsm.BsmCoder;
//import us.dot.its.jpo.ode.plugin.PluginFactory;
//import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
//import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
//import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
//import us.dot.its.jpo.ode.storage.FileSystemStorageService;
//import us.dot.its.jpo.ode.util.JsonUtils;
//import us.dot.its.jpo.ode.bsm.BsmCoder;

public class Importer implements Runnable {
	
	public static final int HEALTHY = 0;  					// 0000000
	public static final int NO_INBOX_FOLDER = 1;  			// 0000001
	public static final int NO_BACKUP_FOLDER   = 2;  		// 0000010
	public static final int WATCH_SVC_ERROR = 4;			// 0000100
	public static final int FATAL_ERROR = 15;				// 0001111
	public static final int MOVE2BACKUP_ERROR = 16;			// 0010000
	public static final int ASN_DECODING_ERROR = 32;		// 0100000
	public static final int PROCESSING_EXISTING_ERROR = 64; // 1000000

	

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// inboxFolder is the directory where the BSM and other data files get dropped by the OBE/OBU
	private Path inboxFolder;	
	// bakupFolder is where the incoming files (from the inboxFolder) are moved after they are processed
	private Path backupFolder;
	
	// ASN.1 decoder/encoder
	//private Asn1Plugin asn1Coder;
	
	// status of the importer
	public int importerStatus = HEALTHY;
	
	// Stats
	public int processedFileCount = 0;
	public long startProcessingTime = 0;
	public long lastFileProcessedTime = 0;
	
	// The BSM coder
	public BsmCoder bsmCoder;

	
	
	public int getImporterStatus() {
		return importerStatus;
	}
	
	
	
	
	public String getImporterErrors() {
		String errorDescription = "";
		if (importerStatus == HEALTHY) {
			errorDescription = "No errors, importer is working like a champ";
		}
		else {
			if ((importerStatus & NO_INBOX_FOLDER) == NO_INBOX_FOLDER){
				if (!errorDescription.isEmpty()){
					errorDescription += " | ";
				}
				errorDescription += ("FATAL ERROR: Inbox folder was not found and was not created at this location: " + inboxFolder.toString());
			}
			if ((importerStatus & NO_BACKUP_FOLDER) == NO_BACKUP_FOLDER){
				if (!errorDescription.isEmpty()){
					errorDescription += " | ";
				}
				errorDescription += ("FATAL ERROR: Backup folder was not found and was not created at this location: " + backupFolder.toString());
			}
			if ((importerStatus & WATCH_SVC_ERROR) == WATCH_SVC_ERROR){
				if (!errorDescription.isEmpty()){
					errorDescription += " | ";
				}
				errorDescription += ("FATAL ERROR: Could not start up the service that monitors for new files in folder " + inboxFolder.toString());
			}
			if ((importerStatus & MOVE2BACKUP_ERROR) == MOVE2BACKUP_ERROR){
				if (!errorDescription.isEmpty()){
					errorDescription += " | ";
				}
				errorDescription += ("ERROR: Could not rename and move file from the inbox folder (" + inboxFolder.toString() + ") to backup folder ("  + backupFolder.toString() + ")");
			}
			if ((importerStatus & ASN_DECODING_ERROR) == ASN_DECODING_ERROR){
				if (!errorDescription.isEmpty()){
					errorDescription += " | ";
				}
				errorDescription += ("ERROR: Encountered error in decoding ASN.1 file");
			}
			if ((importerStatus & PROCESSING_EXISTING_ERROR) == PROCESSING_EXISTING_ERROR){
				if (!errorDescription.isEmpty()){
					errorDescription += " | ";
				}
				errorDescription += ("ERROR: Could not process existing files in the inbox folder: " + inboxFolder.toString());
			}
		}
		return errorDescription;
		
	}
	
	
	
	public void verifyInboxFolder() throws IOException{
		/*
		 * Checks if the inbox and backup folders exist. If they don't they get created. 
		 */
		
		if (!Files.exists(inboxFolder)) {
			logger.warn("IMPORTER - Inbox Folder does not exist");
			try {
				Files.createDirectory(inboxFolder);
			} catch (IOException e) {
		    	logger.error("IMPORTER - Could not create Inbox Folder");
		    	importerStatus |= NO_INBOX_FOLDER | NO_BACKUP_FOLDER;
		    	logger.info("IMPORTER - status: {}" , importerStatus);
		    	throw e;
		    }
		}
		else {
			logger.info("IMPORTER - Inbox Folder exists: " + inboxFolder.toString());
		}
		if (!Files.exists(backupFolder)) {
			logger.warn("IMPORTER - Backup Folder does not exist");
			try {
				Files.createDirectory(backupFolder);
			} catch (IOException e) {
		    	logger.error("IMPORTER - Could not create Backup Folder");
		    	importerStatus |= NO_BACKUP_FOLDER;
		    	logger.info("IMPORTER - status: {}" , importerStatus);
		    	throw e;
		    }
		}
		else {
			logger.info("IMPORTER - Backup Folder exists: " + backupFolder.toString());
		}
	}
	
	

	public Importer(OdeProperties odeProps)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		//this.odeProperties = odeProps;
		
		
		
		String uploadLocation = odeProps.getUploadLocation();
		try {
			inboxFolder = Paths.get(uploadLocation);
			backupFolder = Paths.get(uploadLocation, "backup");
			verifyInboxFolder();	
			
		} catch (Exception ex) {
			logger.error("IMPORTER -  The path provided for the inbox folder is not valid: " + uploadLocation, ex);
			return;
		} 
		logger.info("IMPORTER - Watching inbox folder: " + inboxFolder);

		
		
		//logger.info("IMPORTER - Loading ASN1 Coder: {}", odeProps.getAsn1CoderClassName());
		//this.asn1Coder = (Asn1Plugin) PluginFactory.getPluginByName(odeProps.getAsn1CoderClassName());	
		logger.info("IMPORTER - Instantiating BSM Coder...");
		bsmCoder = new BsmCoder(odeProps);
		
		logger.info("Publishing to {}", OdeProperties.KAFKA_TOPIC_J2735_BSM);
	}

	public Path getInboxFolder() {
		return inboxFolder;
	}

	public void setInboxFolder(Path folder) {
		this.inboxFolder = folder;
	}
	
	public void disposeOfProcessedFile(Path filePath) {
		try {
			// TODO(Cris): handle other file types here...
			String processedFileName = Integer.toString((int)System.currentTimeMillis()) + "-" + filePath.getFileName().toString().replaceFirst("uper", "pbo");
			// TODO (Cris): move magic subfolder name into config
			Path targetPath = backupFolder.resolveSibling("backup\\" +  processedFileName);
			Files.move(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (Exception e){
			importerStatus |= MOVE2BACKUP_ERROR;
			logger.error("IMPORTER -  Error moving file to temporary directory: " + filePath.toString(), e);
		}
	}
	

	public void processFile(Path filePath) throws Exception {
		//String topicName = "BSM";
		//String line = "";
		
		/*
		// TODO: figure out what type of file this is and handle it accordingly
		try (Scanner scanner = new Scanner(filePath.toAbsolutePath())) {
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				
//				if(!line.isEmpty()) {
				// TODO (Cris): process BSM data here, for now it's not important.
				if(false) {
					Asn1Object bsm;
					String encoded;
					try {
						bsm = (Asn1Object) JsonUtils.fromJson(line, J2735Bsm.class);
						logger.info("IMPORTER - Read JSON data: {}", bsm);
						encoded = asn1Coder.UPER_EncodeHex(bsm);
						logger.info("IMPORTER - Encoded data: {}", encoded);
					} catch (Exception e) {
						logger.warn("IMPORTER - Message is not JSON. Assuming HEX...");
						encoded = line;
					}
					
					logger.info("IMPORTER - Data to decode: ", encoded);
	
					J2735Bsm decoded = (J2735Bsm) asn1Coder.UPER_DecodeHex(encoded);
					
					// Update stats
					processedFileCount++;
					lastFileProcessedTime = System.currentTimeMillis();
					
					
					 // Send decoded.toJson() to kafka queue Receive from same Kafka
					 // topic queue
					 
					FileSystemStorageService.produceMessage(topicName, decoded);
					FileSystemStorageService.consumeMessage(topicName);
					logger.info("IMPORTER - Decoded Data:", decoded.toJson());
				}
			}
			// Now rename file and move it to the backup folder			
			try {
				// TODO(Cris): handle other file types here...
				String processedFileName = Integer.toString((int)System.currentTimeMillis()) + "-" + filePath.getFileName().toString().replaceFirst("uper", "pbo");
				Path targetPath = backupFolder.resolveSibling("backup\\" +  processedFileName);
				Files.move(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			}
			catch (Exception e){
				importerStatus |= MOVE2BACKUP_ERROR;
				logger.error("IMPORTER -  Error moving file to temporary directory: " + line, e);
			}

		
		} catch (Exception e) {
			importerStatus |= ASN_DECODING_ERROR;
			logger.error("IMPORTER -  Error opening file: " + filePath.toString(), e);
			throw (e);
		}
		*/
		File bsmFile = filePath.toFile();
		
		int retryCount = 2;
		while (retryCount-- > 0) {
			try (InputStream inputStream = new FileInputStream(filePath.toFile())) {
				
				// TODO (Cris): enable
				//this.bsmCoder.decodeFromHexAndPublish(
				//		inputStream, OdeProperties.KAFKA_TOPIC_J2735_BSM);
				inputStream.close();
				// bsmFile.delete();
				disposeOfProcessedFile(filePath);
				break;
			} catch (Exception e) {
				logger.info("unable to open file: " + filePath 
						+ " retrying " + retryCount + " more times", e);
				Thread.sleep(100);
			}
		}						
	}
	
	
	public void processExistingFiles(Path pathToFileDirectory) {
		
		// TODO (Cris): test to see what happens when new files are arriving during the iteration
		try (DirectoryStream<Path> stream =
		     Files.newDirectoryStream(pathToFileDirectory, "*.{uper, bsm}")) {
		    for (Path entry: stream) {
		    	try {
			        System.out.println(entry.getFileName());
			        processFile(entry);
		    	}
		    	catch(Exception e) {
		    		;
		    	}
		    }
		} catch (IOException e) {
		    // IOException thrown by newDirectoryStream.	
			importerStatus |= PROCESSING_EXISTING_ERROR;
		    logger.error("IMPORTER -  Error validating import location.", e);
		}
		
	}
	

	@Override
	public void run() {
		
		// If we have any fatal errors at this point we abort
		if ((importerStatus & FATAL_ERROR) != 0) {
			logger.error("IMPORTER -  Importer cannot run, see error above. Execution terminated.");
			return;
		}
		
		// Reset the stats
		processedFileCount = 0;
		startProcessingTime = System.currentTimeMillis();
		lastFileProcessedTime = 0;

		// First process any files that may already be in the directory. 
		processExistingFiles(inboxFolder);
		
		// Then set up monitoring so all newly created files are detected and processed.
		
		// We obtain the file system of the Path
		FileSystem fs = inboxFolder.getFileSystem();
		boolean isRunning = true;
      
		while (isRunning) {
			// We create the new WatchService using the new try() block
			try (WatchService service = fs.newWatchService()) {

				// We register the folder to the service
				// We watch for creation events
				inboxFolder.register(service, ENTRY_CREATE, ENTRY_MODIFY);

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
						Path newPath = watchEventCurrent.context();
						logger.info("IMPORTER - New file detected: {}", newPath);
						
						processFile(newPath);

						
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

	}

	