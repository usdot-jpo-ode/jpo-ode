package us.dot.its.jpo.ode;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@ConfigurationProperties("ode")
@org.springframework.context.annotation.PropertySource("classpath:application.properties")
public class OdeProperties implements EnvironmentAware {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private static Environment env;
	
	private String uploadLocation = "uploads";
	private String pluginsLocations = "plugins";
	private String asn1CoderClassName = "us.dot.its.jpo.ode.plugins.oss.j2735.OssAsn1Coder";
	private String kafkaBrokers = "localhost:9092";
	private String kafkaProducerType = "sync";
	private int    importerInterval = 1000;
	
	private String hostId;
	
	public static String getProperty(String key) {
		return env.getProperty(key);
	}
	
	public static String getProperty(String key, String defaultValue) {
		return env.getProperty(key, defaultValue);
	}
	
	public static Object getProperty(String key, int i) {
		return env.getProperty(key, Integer.class, i);
	}

	public String getHostId() {
		return hostId;
	}

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
	public String getKafkaBrokers() {
		return kafkaBrokers;
	}

	public void setKafkaBrokers(String kafkaBrokers) {
		this.kafkaBrokers = kafkaBrokers;
	}

	public String getKafkaProducerType() {
		return kafkaProducerType;
	}

	public void setKafkaProducerType(String kafkaProducerType) {
		this.kafkaProducerType = kafkaProducerType;
	}


	
	public int getImporterInterval() {
		return importerInterval;
	}

	public void setImporterInterval(int importerInterval) {
		this.importerInterval = importerInterval;
	}

	public static Environment getEnv() {
		return env;
	}

	public static void setEnv(Environment env) {
		OdeProperties.env = env;
	}



	public void init() {
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// Let's just use a random hostname
			hostname = UUID.randomUUID().toString();
		}
		hostId = hostname;
		
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}


}
