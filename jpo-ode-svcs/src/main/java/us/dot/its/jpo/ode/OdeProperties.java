package us.dot.its.jpo.ode;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import groovy.lang.MissingPropertyException;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.eventlog.EventLogger;

@ConfigurationProperties("ode")
@PropertySource("classpath:application.properties")
public class OdeProperties implements EnvironmentAware {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment env;

    private List<Path> uploadLocations = new ArrayList<>();

    private String uploadLocationRoot = "uploads";
    private String uploadLocationBsm = "bsm";
    private String uploadLocationMessageFrame = "messageframe";
    private String pluginsLocations = "plugins";
    private String asn1CoderClassName = "us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder";
    private String kafkaBrokers = null;
    private String kafkaProducerType = AppContext.DEFAULT_KAFKA_PRODUCER_TYPE;
    private String ddsCasUrl = "https://cas.connectedvcs.com/accounts/v1/tickets";
    private String ddsCasUsername = "";
    private String ddsCasPass = "";
    private String ddsWebsocketUrl = "wss://webapp2.connectedvcs.com/whtools23/websocket";
    private String kafkaTopicBsmSerializedPOJO = "topic.J2735Bsm";
    private String kafkaTopicBsmJSON = "topic.J2735BsmRawJSON";
    private String kafkaTopicVsdm = "topic.J2735Vsdm";
    
    private int receiverPort = 46753;
    private int vsdmBufferSize = 10000;
    private Boolean vsdmVerboseJson = false;
    private String sdcIp = "";
    private int sdcPort;
    private String returnIp = "";
    private int returnPort;
    private int serviceRequestSenderPort;
    private int vsdmSenderPort;

    private String hostId;

    public OdeProperties() {
        super();
        init();
    }
    
    public void init() {

        uploadLocations.add(Paths.get(uploadLocationRoot));
        uploadLocations.add(Paths.get(uploadLocationRoot, uploadLocationBsm));
        uploadLocations.add(Paths.get(uploadLocationRoot, uploadLocationMessageFrame));

        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // Let's just use a random hostname
            hostname = UUID.randomUUID().toString();
            logger.info("Unknown host error: {}, using random", e);
        }
        hostId = hostname;
        logger.info("Host ID: {}", hostId);
        EventLogger.logger.info("Initializing services on host {}", hostId);

        if (kafkaBrokers == null) {
            logger.info(
                    "ode.kafkaBrokers property not defined. Will try DOCKER_HOST_IP from which will derive the Kafka bootstrap-server");
            
            kafkaBrokers = System.getenv("DOCKER_HOST_IP") + ":9092";
        }

        if (kafkaBrokers == null)
            throw new MissingPropertyException(
                    "Neither ode.kafkaBrokers ode property nor DOCKER_HOST_IP environment variable are defined");
    }

    public List<Path> getUploadLocations() {
        return this.uploadLocations;
    }

    public String getProperty(String key) {
        return env.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return env.getProperty(key, defaultValue);
    }

    public Object getProperty(String key, int i) {
        return env.getProperty(key, Integer.class, i);
    }

    public String getHostId() {
        return hostId;
    }

    public String getUploadLocationBsm() {
        return uploadLocationBsm;
    }

    public void setUploadLocationBsm(String uploadLocation) {
        this.uploadLocationBsm = uploadLocation;
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

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    public String getUploadLocationMessageFrame() {
        return uploadLocationMessageFrame;
    }

    public void setUploadLocationMessageFrame(String uploadLocationMessageFrame) {
        this.uploadLocationMessageFrame = uploadLocationMessageFrame;
    }

    public String getUploadLocationRoot() {
        return uploadLocationRoot;
    }

    public void setUploadLocationRoot(String uploadLocationRoot) {
        this.uploadLocationRoot = uploadLocationRoot;
    }

    public String getDdsCasUrl() {
        return ddsCasUrl;
    }

    public void setDdsCasUrl(String ddsCasUrl) {
        this.ddsCasUrl = ddsCasUrl;
    }

    public String getDdsCasUsername() {
        return ddsCasUsername;
    }

    public void setDdsCasUsername(String ddsCasUsername) {
        this.ddsCasUsername = ddsCasUsername;
    }

    public String getDdsCasPassword() {
        return ddsCasPass;
    }

    public void setDdsCasPassword(String ddsCasPassword) {
        this.ddsCasPass = ddsCasPassword;
    }

    public String getDdsWebsocketUrl() {
        return ddsWebsocketUrl;
    }

    public void setDdsWebsocketUrl(String ddsWebsocketUrl) {
        this.ddsWebsocketUrl = ddsWebsocketUrl;
    }

    public String getKafkaTopicBsmSerializedPOJO() {
        return kafkaTopicBsmSerializedPOJO;
    }

    public void setKafkaTopicBsmSerializedPOJO(String kafkaTopicBsmSerializedPOJO) {
        this.kafkaTopicBsmSerializedPOJO = kafkaTopicBsmSerializedPOJO;
    }

    public String getKafkaTopicBsmJSON() {
        return kafkaTopicBsmJSON;
    }

    public void setKafkaTopicBsmJSON(String kafkaTopicBsmJSON) {
        this.kafkaTopicBsmJSON = kafkaTopicBsmJSON;
    }

	public int getReceiverPort() {
		return receiverPort;
	}

	public void setReceiverPort(int vsdmPort) {
		this.receiverPort = vsdmPort;
	}

	public int getVsdmBufferSize() {
		return vsdmBufferSize;
	}

	public void setVsdmBufferSize(int vsdmBufferSize) {
		this.vsdmBufferSize = vsdmBufferSize;
	}

	public Boolean getVsdmVerboseJson() {
		return vsdmVerboseJson;
	}

	public void setVsdmVerboseJson(Boolean vsdmVerboseJson) {
		this.vsdmVerboseJson = vsdmVerboseJson;
	}
  
	public String getSdcIp() {
		return sdcIp;
	}

	public void setSdcIp(String sdcIp) {
		this.sdcIp = sdcIp;
	}

	public int getSdcPort() {
		return sdcPort;
	}

	public void setSdcPort(int sdcPort) {
		this.sdcPort = sdcPort;
	}

	public String getReturnIp() {
		return returnIp;
	}

	public void setReturnIp(String returnIp) {
		this.returnIp = returnIp;
	}

	public int getReturnPort() {
		return returnPort;
	}

	public void setReturnPort(int returnPort) {
		this.returnPort = returnPort;
	}

	public int getServiceRequestSenderPort() {
		return serviceRequestSenderPort;
	}

	public void setServiceRequestSenderPort(int serviceRequestSenderPort) {
		this.serviceRequestSenderPort = serviceRequestSenderPort;
	}

	public int getVsdmSenderPort() {
		return vsdmSenderPort;
	}

	public void setVsdmSenderPort(int vsdmSenderPort) {
		this.vsdmSenderPort = vsdmSenderPort;
	}

	public String getKafkaTopicVsdm() {
		return kafkaTopicVsdm;
	}

}
