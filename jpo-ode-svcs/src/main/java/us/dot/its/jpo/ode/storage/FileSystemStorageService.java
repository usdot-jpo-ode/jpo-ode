package us.dot.its.jpo.ode.storage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
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
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;

@Service
public class FileSystemStorageService implements StorageService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Path rootLocation;
	private Asn1Plugin asn1Coder;

	@Autowired
	public FileSystemStorageService(OdeProperties properties) {
		this.rootLocation = Paths.get(properties.getUploadLocation());
		logger.info("Upload location: {}", this.rootLocation);
		logger.info("Loading ASN1 Coder: {}", properties.getAsn1CoderClassName());
		this.asn1Coder = (Asn1Plugin) PluginFactory.getPluginByName(properties.getAsn1CoderClassName());
	}

	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
			}
			Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));

		} catch (FileAlreadyExistsException fae) {
			logger.info("File already exisits");
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
		}
		encodeData(file);
	}

	private void encodeData(MultipartFile file) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(file.getInputStream());

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Asn1Object bsm = (Asn1Object) JsonUtils.fromJson(line, J2735Bsm.class);

				String encoded = asn1Coder.UPER_EncodeBase64(bsm);
				logger.info(encoded);
				J2735Bsm decoded = (J2735Bsm) asn1Coder.UPER_DecodeBase64(encoded);
				logger.info("Latitude: {}", decoded.coreData.position.getLatitude().toPlainString());
				logger.info("Longitude: {}", decoded.coreData.position.getLongitude().toPlainString());
				logger.info("Elevation: {}", decoded.coreData.position.getElevation().toPlainString());

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (scanner != null)
				scanner.close();
		}

	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(path -> this.rootLocation.relativize(path));
		} catch (IOException e) {
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
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
