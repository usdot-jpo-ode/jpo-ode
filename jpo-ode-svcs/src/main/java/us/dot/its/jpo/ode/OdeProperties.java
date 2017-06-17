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

   /*
    * General Properties
    */
   private String pluginsLocations = "plugins";
   private String asn1CoderClassName = "us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder";
   private String kafkaBrokers = null;
   private String kafkaProducerType = AppContext.DEFAULT_KAFKA_PRODUCER_TYPE;
   private Boolean verboseJson = false;
   private String externalIpv4 = "";
   private String externalIpv6 = "";

   // File import properties
   private String uploadLocationRoot = "uploads";
   private String uploadLocationBsm = "bsm";
   private String uploadLocationMessageFrame = "messageframe";

   /*
    * USDOT Situation Data Clearinghouse (SDC)/ Situation Data Warehouse (SDW),
    * a.k.a Data Distribution System (DDS) Properties
    */
   // DDS WebSocket Properties
   private String ddsCasUrl = "https://cas.connectedvcs.com/accounts/v1/tickets";
   private String ddsCasUsername = "";
   private String ddsCasPass = "";
   private String ddsWebsocketUrl = "wss://webapp2.connectedvcs.com/whtools23/websocket";

   // IPv4 address and listening UDP port for SDC
   private String sdcIp = "104.130.170.234";// NOSONAR
   private int sdcPort = 46753;

   // Enable/disable depositing sanitized BSMs to SDC
   private boolean depositSanitizedBsmToSdc = true;

   private int serviceRespExpirationSeconds = 10;

   private int serviceResponseBufferSize = 500;

   /*
    * BSM Properties
    */
   private String kafkaTopicBsmSerializedPojo = "j2735Bsm";
   private String kafkaTopicBsmRawJson = "j2735BsmRawJson";
   private String kafkaTopicBsmFilteredJson = "j2735BsmFilteredJson";
   private int bsmReceiverPort = 46800;
   private int bsmBufferSize = 500;

   /*
    * Vehicle Situation Data (VSD) Properties
    */
   private int vsdBufferSize = 500;
   private int vsdReceiverPort = 46753;
   private int vsdDepositorPort = 5555;
   private int vsdTrustport = 5556;

   /*
    * Intersection Situation Data (ISD) Properties
    */
   private String kafkaTopicEncodedIsd = "encodedIsd";
   private int isdBufferSize = 500;
   private int isdReceiverPort = 46801;
   private int isdDepositorPort = 6666;
   private int isdTrustPort = 6667;
   private int dataReceiptExpirationSeconds = 2;
   private int messagesUntilTrustReestablished = 5;
   int dataReceiptBufferSize = 500;

   private String hostId;
   private List<Path> uploadLocations = new ArrayList<>();

   protected static final byte[] JPO_ODE_GROUP_ID = "jode".getBytes();

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

   public String getKafkaTopicBsmFilteredJson() {
      return kafkaTopicBsmFilteredJson;
   }

   public void setKafkaTopicBsmFilteredJson(String kafkaTopicBsmFilteredJson) {
      this.kafkaTopicBsmFilteredJson = kafkaTopicBsmFilteredJson;
   }

   public String getKafkaTopicBsmSerializedPojo() {
      return kafkaTopicBsmSerializedPojo;
   }

   public void setKafkaTopicBsmSerializedPojo(String kafkaTopicBsmSerializedPOJO) {
      this.kafkaTopicBsmSerializedPojo = kafkaTopicBsmSerializedPOJO;
   }

   public int getVsdReceiverPort() {
      return vsdReceiverPort;
   }

   public void setVsdReceiverPort(int vsdReceiverPort) {
      this.vsdReceiverPort = vsdReceiverPort;
   }

   public int getVsdBufferSize() {
      return vsdBufferSize;
   }

   public void setVsdBufferSize(int vsdBufferSize) {
      this.vsdBufferSize = vsdBufferSize;
   }

   public Boolean getVerboseJson() {
      return verboseJson;
   }

   public int getIsdBufferSize() {
      return isdBufferSize;
   }

   public void setIsdBufferSize(int isdBufferSize) {
      this.isdBufferSize = isdBufferSize;
   }

   public void setVerboseJson(Boolean verboseJson) {
      this.verboseJson = verboseJson;
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

   public String getExternalIpv4() {
      return externalIpv4;
   }

   public void setExternalIpv4(String externalIpv4) {
      this.externalIpv4 = externalIpv4;
   }

   public String getExternalIpv6() {
      return externalIpv6;
   }

   public void setExternalIpv6(String externalIpv6) {
      this.externalIpv6 = externalIpv6;
   }

   public String getKafkaTopicEncodedIsd() {
      return kafkaTopicEncodedIsd;
   }

   public void setKafkaTopicEncodedIsd(String kafkaTopicEncodedIsd) {
      this.kafkaTopicEncodedIsd = kafkaTopicEncodedIsd;
   }

   public int getVsdDepositorPort() {
      return vsdDepositorPort;
   }

   public void setVsdDepositorPort(int vsdSenderPort) {
      this.vsdDepositorPort = vsdSenderPort;
   }

   public int getIsdReceiverPort() {
      return isdReceiverPort;
   }

   public void setIsdReceiverPort(int isdReceiverPort) {
      this.isdReceiverPort = isdReceiverPort;
   }

   public int getIsdDepositorPort() {
      return isdDepositorPort;
   }

   public void setIsdDepositorPort(int isdDepositorPort) {
      this.isdDepositorPort = isdDepositorPort;
   }

   public String getDdsCasPass() {
      return ddsCasPass;
   }

   public void setDdsCasPass(String ddsCasPass) {
      this.ddsCasPass = ddsCasPass;
   }

   public String getKafkaTopicBsmRawJson() {
      return kafkaTopicBsmRawJson;
   }

   public void setKafkaTopicBsmRawJson(String kafkaTopicBsmRawJson) {
      this.kafkaTopicBsmRawJson = kafkaTopicBsmRawJson;
   }

   public void setReturnIp(String returnIp) {
      this.externalIpv4 = returnIp;
   }

   public int getBsmReceiverPort() {
      return bsmReceiverPort;
   }

   public void setBsmReceiverPort(int bsmReceiverPort) {
      this.bsmReceiverPort = bsmReceiverPort;
   }

   public int getBsmBufferSize() {
      return bsmBufferSize;
   }

   public void setBsmBufferSize(int bsmBufferSize) {
      this.bsmBufferSize = bsmBufferSize;
   }

   public boolean getDepositSanitizedBsmToSdc() {
      return depositSanitizedBsmToSdc;
   }

   public void setDepositSanitizedBsmToSdc(boolean depositSanitizedBsmToSdc) {
      this.depositSanitizedBsmToSdc = depositSanitizedBsmToSdc;
   }

   public int getServiceRespExpirationSeconds() {
      return serviceRespExpirationSeconds;
   }

   public void setServiceRespExpirationSeconds(int serviceRespExpirationSeconds) {
      this.serviceRespExpirationSeconds = serviceRespExpirationSeconds;
   }

   public int getServiceResponseBufferSize() {
      return serviceResponseBufferSize;
   }

   public void setServiceResponseBufferSize(int serviceResponseBufferSize) {
      this.serviceResponseBufferSize = serviceResponseBufferSize;
   }

   public int getVsdTrustport() {
      return vsdTrustport;
   }

   public void setVsdTrustport(int vsdTrustport) {
      this.vsdTrustport = vsdTrustport;
   }

   public int getIsdTrustPort() {
      return isdTrustPort;
   }

   public void setIsdTrustPort(int isdTrustPort) {
      this.isdTrustPort = isdTrustPort;
   }

   public int getDataReceiptExpirationSeconds() {
      return dataReceiptExpirationSeconds;
   }

   public void setDataReceiptExpirationSeconds(int dataReceiptExpirationSeconds) {
      this.dataReceiptExpirationSeconds = dataReceiptExpirationSeconds;
   }

   public int getMessagesUntilTrustReestablished() {
      return messagesUntilTrustReestablished;
   }

   public void setMessagesUntilTrustReestablished(int messagesUntilTrustReestablished) {
      this.messagesUntilTrustReestablished = messagesUntilTrustReestablished;
   }

   public int getDataReceiptBufferSize() {
      return dataReceiptBufferSize;
   }

}
