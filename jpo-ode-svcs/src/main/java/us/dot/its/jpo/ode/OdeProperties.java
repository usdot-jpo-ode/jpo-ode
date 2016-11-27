package us.dot.its.jpo.ode;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import us.dot.its.jpo.ode.storage.StorageException;

@ConfigurationProperties("ode")
public class OdeProperties {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String uploadLocation = "uploads";
	private String pluginsLocations = "plugins";
	private String asn1CoderClassName = "us.dot.its.jpo.ode.plugins.oss.j2735.OssAsn1Coder";
	
	public String getUploadLocation() {
		return uploadLocation;
	}
	public void setUploadLocation(String uploadLocation) {
		this.uploadLocation = uploadLocation;
	}
	public String getPluginsLocations() {
		return pluginsLocations;
	}
	public void setPluginsLocations(String pluginsLocations) {
		this.pluginsLocations = pluginsLocations;
	}
	
	public String getAsn1CoderClassName() {
		return asn1CoderClassName;
	}
	public void setAsn1CoderClassName(String asn1CoderClassName) {
		this.asn1CoderClassName = asn1CoderClassName;
	}
	public void init() {
        try {
           Files.createDirectory(Paths.get(uploadLocation));
        } catch (FileAlreadyExistsException fae) {
        	logger.info("Upload directory already exisits");
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
	}

}
