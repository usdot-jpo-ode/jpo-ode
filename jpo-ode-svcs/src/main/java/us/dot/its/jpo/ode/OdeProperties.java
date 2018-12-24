/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.thymeleaf.util.StringUtils;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.plugin.OdePlugin;
import us.dot.its.jpo.ode.util.CommonUtils;

@ConfigurationProperties("ode")
@PropertySource("classpath:application.properties")
public class OdeProperties implements EnvironmentAware {

   private static final Logger logger = LoggerFactory.getLogger(OdeProperties.class);

   @Autowired
   private Environment env;

   /*
    * General Properties
    */
   private String version;
   private static final int OUTPUT_SCHEMA_VERSION = 6;
   private String pluginsLocations = "plugins";
   private String kafkaBrokers = null;
   private static final String DEFAULT_KAFKA_PORT = "9092";
   private String kafkaProducerType = AppContext.DEFAULT_KAFKA_PRODUCER_TYPE;
   private Boolean verboseJson = false;
   private String externalIpv4 = "";
   private String externalIpv6 = "";
   
   /*
    * RSU Properties
    */
   private int rsuSrmSlots = 100; // number of "store and repeat message" indicies for RSU TIMs
   private String rsuUsername = "";
   private String rsuPassword = "";
   
   /*
    * Security Services Module Properties
    */
   private String securitySvcsSignatureUri;
   private int securitySvcsPort = 8090;
   private String securitySvcsSignatureEndpoint = "sign";

   // File import properties
   private String uploadLocationRoot = "uploads";
   private String uploadLocationObuLogLog = "bsmlog";
   private Integer fileWatcherPeriod = 5; // time to wait between processing inbox directory for new files

   /*
    * USDOT Situation Data Clearinghouse (SDC)/ Situation Data Warehouse (SDW),
    * a.k.a Data Distribution System (DDS) Properties
    */
   // DDS WebSocket Properties
   private String ddsCasUrl = "https://cas.cvmvp.com/accounts/v1/tickets";
   private String ddsCasUsername = "";
   private String ddsCasPass = "";
   private String ddsWebsocketUrl = "wss://webapp.cvmvp.com/whtools/websocket";

   // IPv4 address and listening UDP port for SDC
   private String sdcIp = "104.130.170.234";// NOSONAR
   private int sdcPort = 46753;

   // Enable/disable depositing sanitized BSMs to SDC
   private boolean depositSanitizedBsmToSdc = false;
   private int serviceRespExpirationSeconds = 10;
   private int serviceResponseBufferSize = 500;
   
   /*
    * UDP Properties
    */
   private int trustRetries = 2; // if trust handshake fails, how many times to retry
   private int messagesUntilTrustReestablished = 10; // renew trust session every x messages
   
   /*
    * Kafka Topics
    * 
    */
   @Value("ode.topics.disabled")
   private String[] kafkaTopicsDisabled = {"topic.OdeBsmRxPojo", "topic.OdeBsmTxPojo", "topic.OdeBsmDuringEventPojo", "topic.OdeTimRxJson"};
   private Set<String> kafkaTopicsDisabledSet = new HashSet<>();
   
   //BSM
   private String kafkaTopicOdeBsmPojo = "topic.OdeBsmPojo";
   private String kafkaTopicOdeBsmJson = "topic.OdeBsmJson";
   private String kafkaTopicOdeBsmRxPojo= "topic.OdeBsmRxPojo";
   private String kafkaTopicOdeBsmTxPojo= "topic.OdeBsmTxPojo";
   private String kafkaTopicOdeBsmDuringEventPojo= "topic.OdeBsmDuringEventPojo";
   private String kafkaTopicFilteredOdeBsmJson = "topic.FilteredOdeBsmJson";

   //TIM
   private String kafkaTopicOdeTimPojo = "topic.OdeTimPojo";
   private String kafkaTopicOdeTimJson = "topic.OdeTimJson";
   private String kafkaTopicOdeDNMsgJson= "topic.OdeDNMsgJson";
   private String kafkaTopicOdeDNMsgPojo= "topic.OdeDNMsgPojo";
   private String kafkaTopicOdeTimRxJson= "topic.OdeTimRxJson";
   private String kafkaTopicOdeTimBroadcastPojo= "topic.OdeTimBroadcastPojo";
   private String kafkaTopicOdeTimBroadcastJson= "topic.OdeTimBroadcastJson";
   private String kafkaTopicJ2735TimBroadcastJson= "topic.J2735TimBroadcastJson";
   private String kafkaTopicFilteredOdeTimJson = "topic.FilteredOdeTimJson";

   // DriverAlerts
   private String kafkaTopicDriverAlertJson = "topic.OdeDriverAlertJson";

   //VSD
   private String kafkaTopicVsdPojo = "topic.AsnVsdPojo";

   //ISD
   private String kafkaTopicIsdPojo = "topic.AsnIsdPojo";

   //ASN.1 CODEC
   private String kafkaTopicAsn1DecoderInput = "topic.Asn1DecoderInput";
   private String kafkaTopicAsn1DecoderOutput = "topic.Asn1DecoderOutput";
   private String kafkaTopicAsn1EncoderInput = "topic.Asn1EncoderInput";
   private String kafkaTopicAsn1EncoderOutput = "topic.Asn1EncoderOutput";

   /*
    * BSM Properties
    */
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
   private int isdBufferSize = 500;
   private int isdReceiverPort = 46801;
   private int isdDepositorPort = 6666;
   private int isdTrustPort = 6667;
   private int dataReceiptBufferSize;
   private int dataReceiptExpirationSeconds;


   private int importProcessorBufferSize = OdePlugin.INPUT_STREAM_BUFFER_SIZE;


   private String hostId;
   private List<Path> uploadLocations = new ArrayList<>();

   /*
    * Security Properties
    */
   private String caCertPath;
   private String selfCertPath;
   private String selfPrivateKeyReconstructionFilePath;
   private String selfSigningPrivateKeyFilePath;

   
   private static final byte[] JPO_ODE_GROUP_ID = "jode".getBytes();


   @PostConstruct
   void initialize() {

     String pomPropsFile = "/META-INF/maven/usdot.jpo.ode/jpo-ode-svcs/pom.properties";
      try {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(pomPropsFile);
        Properties properties = new Properties();
        properties.load(resourceAsStream);
        setVersion(properties.getProperty("version"));
        logger.info("groupId: {}", properties.getProperty("groupId"));
        logger.info("artifactId: {}", properties.getProperty("artifactId"));
        logger.info("version: {}", version);
        
      } catch (IOException e) {
        logger.error("Error loading properties file " + pomPropsFile, e);
      }
      
      OdeMsgMetadata.setStaticSchemaVersion(OUTPUT_SCHEMA_VERSION);
      
      uploadLocations.add(Paths.get(uploadLocationRoot));

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

         logger.info("ode.kafkaBrokers property not defined. Will try DOCKER_HOST_IP => {}", kafkaBrokers);

         String dockerIp = CommonUtils.getEnvironmentVariable("DOCKER_HOST_IP");
         
         if (dockerIp == null) {
            logger.warn("Neither ode.kafkaBrokers ode property nor DOCKER_HOST_IP environment variable are defined. Defaulting to localhost.");
            dockerIp = "localhost";
         }
         kafkaBrokers = dockerIp + ":" + DEFAULT_KAFKA_PORT;
         
         // URI for the security services /sign endpoint
         if (securitySvcsSignatureUri == null) {
            securitySvcsSignatureUri = "http://" + dockerIp + ":" + securitySvcsPort + "/" + securitySvcsSignatureEndpoint;
         }
      }
      
      List<String> asList = Arrays.asList(this.getKafkaTopicsDisabled());
      logger.info("Disabled Topics: {}", asList);
      kafkaTopicsDisabledSet.addAll(asList);
   }
   
   

   public String getVersion() {
     return version;
   }

   public void setVersion(String version) {
     this.version = version;
   }

  public boolean dataSigningEnabled() {
      return getSecuritySvcsSignatureUri() != null &&
            !StringUtils.isEmptyOrWhitespace(getSecuritySvcsSignatureUri()) &&
            !getSecuritySvcsSignatureUri().startsWith("UNSECURE");
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

   public String getPluginsLocations() {
      return pluginsLocations;
   }

   public void setPluginsLocations(String pluginsLocations) {
      this.pluginsLocations = pluginsLocations;
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

   public String getUploadLocationRoot() {
      return uploadLocationRoot;
   }

   public void setSdcIp(String sdcIp) {
      this.sdcIp = sdcIp;
   }
   
   public String getSdcIp() {
      return sdcIp;
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

   public String getDdsCasPassword() {
      return ddsCasPass;
   }

   public void setDdsCasPassword(String ddsCasPass) {
      this.ddsCasPass = ddsCasPass;
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
   
   public void setDataReceiptBufferSize(int dataReceiptBufferSize) {
      this.dataReceiptBufferSize = dataReceiptBufferSize;
   }

   public String getCaCertPath() {
      return caCertPath;
   }

   public void setCaCertPath(String caCertPath) {
      this.caCertPath = caCertPath;
   }

   public String getSelfCertPath() {
      return selfCertPath;
   }

   public void setSelfCertPath(String selfCertPath) {
      this.selfCertPath = selfCertPath;
   }

   public String getSelfPrivateKeyReconstructionFilePath() {
      return selfPrivateKeyReconstructionFilePath;
   }

   public void setSelfPrivateKeyReconstructionFilePath(String selfPrivateKeyReconstructionFilePath) {
      this.selfPrivateKeyReconstructionFilePath = selfPrivateKeyReconstructionFilePath;
   }

   public String getSelfSigningPrivateKeyFilePath() {
      return selfSigningPrivateKeyFilePath;
   }

   public void setSelfSigningPrivateKeyFilePath(String selfSigningPrivateKeyFilePath) {
      this.selfSigningPrivateKeyFilePath = selfSigningPrivateKeyFilePath;
   }

   public int getIsdBufferSize() {
      return isdBufferSize;
   }

   public void setIsdBufferSize(int isdBufferSize) {
      this.isdBufferSize = isdBufferSize;
   }

    public int getVsdBufferSize() {
        return vsdBufferSize;
    }

   public void setVsdBufferSize(int vsdBufferSize) {
      this.vsdBufferSize = vsdBufferSize;
   }

   public int getVsdReceiverPort() {
      return vsdReceiverPort;
   }

   public void setVsdReceiverPort(int vsdReceiverPort) {
      this.vsdReceiverPort = vsdReceiverPort;
   }

   public Boolean getVerboseJson() {
      return verboseJson;
   }

   public void setVerboseJson(Boolean verboseJson) {
      this.verboseJson = verboseJson;
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

   public String getDdsWebsocketUrl() {
      return ddsWebsocketUrl;
   }

   public void setDdsWebsocketUrl(String ddsWebsocketUrl) {
      this.ddsWebsocketUrl = ddsWebsocketUrl;
   }

   public void setUploadLocationRoot(String uploadLocationRoot) {
      this.uploadLocationRoot = uploadLocationRoot;
   }

   public int getRsuSrmSlots() {
      return rsuSrmSlots;
   }

   public void setRsuSrmSlots(int rsuSrmSlots) {
      this.rsuSrmSlots = rsuSrmSlots;
   }

   public int getTrustRetries() {
      return trustRetries;
   }

   public void setTrustRetries(int trustRetries) {
      this.trustRetries = trustRetries;
   }

   public static byte[] getJpoOdeGroupId() {
      return JPO_ODE_GROUP_ID;
   }

   public int getImportProcessorBufferSize() {
        return importProcessorBufferSize;
    }

    public void setImportProcessorBufferSize(int importProcessorBufferSize) {
        this.importProcessorBufferSize = importProcessorBufferSize;
    }

    
    public String[] getKafkaTopicsDisabled() {
      return kafkaTopicsDisabled;
   }

   public void setKafkaTopicsDisabled(String[] kafkaTopicsDisabled) {
      this.kafkaTopicsDisabled = kafkaTopicsDisabled;
   }
   
   public Set<String> getKafkaTopicsDisabledSet() {
      return kafkaTopicsDisabledSet;
   }

   public void setKafkaTopicsOutputEnabledSet(Set<String> kafkaTopicsOutputEnabledSet) {
      this.kafkaTopicsDisabledSet = kafkaTopicsOutputEnabledSet;
   }

   public String getKafkaTopicIsdPojo() {
       return kafkaTopicIsdPojo;
   }

    public void setKafkaTopicIsdPojo(String kafkaTopicIsdPojo) {
       this.kafkaTopicIsdPojo = kafkaTopicIsdPojo;
    }

    public String getKafkaTopicVsdPojo() {
       return kafkaTopicVsdPojo;
    }

    public void setKafkaTopicVsdPojo(String kafkaTopicVsdPojo) {
       this.kafkaTopicVsdPojo = kafkaTopicVsdPojo;
    }

     public String getKafkaTopicFilteredOdeBsmJson() {
        return kafkaTopicFilteredOdeBsmJson;
     }

     public void setKafkaTopicFilteredOdeBsmJson(String kafkaTopicFilteredOdeBsmJson) {
        this.kafkaTopicFilteredOdeBsmJson = kafkaTopicFilteredOdeBsmJson;
     }

     public String getKafkaTopicOdeBsmPojo() {
        return kafkaTopicOdeBsmPojo;
     }

     public void setKafkaTopicOdeBsmPojo(String kafkaTopicOdeBsmPojo) {
        this.kafkaTopicOdeBsmPojo = kafkaTopicOdeBsmPojo;
     }

     public String getKafkaTopicOdeBsmJson() {
        return kafkaTopicOdeBsmJson;
     }

     public void setKafkaTopicOdeBsmJson(String kafkaTopicOdeBsmJson) {
        this.kafkaTopicOdeBsmJson = kafkaTopicOdeBsmJson;
     }

    public String getKafkaTopicAsn1DecoderInput() {
       return kafkaTopicAsn1DecoderInput;
    }

    public void setKafkaTopicAsn1DecoderInput(String kafkaTopicAsn1DecoderInput) {
       this.kafkaTopicAsn1DecoderInput = kafkaTopicAsn1DecoderInput;
    }

    public String getKafkaTopicAsn1DecoderOutput() {
       return kafkaTopicAsn1DecoderOutput;
    }

    public void setKafkaTopicAsn1DecoderOutput(String kafkaTopicAsn1DecoderOutput) {
       this.kafkaTopicAsn1DecoderOutput = kafkaTopicAsn1DecoderOutput;
    }

    public String getKafkaTopicAsn1EncoderInput() {
       return kafkaTopicAsn1EncoderInput;
    }

    public void setKafkaTopicAsn1EncoderInput(String kafkaTopicAsn1EncoderInput) {
       this.kafkaTopicAsn1EncoderInput = kafkaTopicAsn1EncoderInput;
    }

    public String getKafkaTopicAsn1EncoderOutput() {
       return kafkaTopicAsn1EncoderOutput;
    }

    public void setKafkaTopicAsn1EncoderOutput(String kafkaTopicAsn1EncoderOutput) {
       this.kafkaTopicAsn1EncoderOutput = kafkaTopicAsn1EncoderOutput;
    }

   public String getKafkaTopicOdeTimPojo() {
      return kafkaTopicOdeTimPojo;
   }

   public void setKafkaTopicOdeTimPojo(String kafkaTopicOdeTimPojo) {
      this.kafkaTopicOdeTimPojo = kafkaTopicOdeTimPojo;
   }
   public String getKafkaTopicOdeDNMsgPojo() {return kafkaTopicOdeDNMsgPojo; }

   public void setKafkaTopicOdeDNMsgPojo(String kafkaTopicOdeDNMsgPojo) {
      this.kafkaTopicOdeDNMsgPojo = kafkaTopicOdeDNMsgPojo;
   }
   public String getKafkaTopicOdeDNMsgJson() {return kafkaTopicOdeDNMsgJson; }

   public void setKafkaTopicOdeDNMsgJson(String kafkaTopicOdeDNMsgJson) {
      this.kafkaTopicOdeDNMsgJson = kafkaTopicOdeDNMsgJson;
   }

   public String getKafkaTopicOdeTimJson() {
      return kafkaTopicOdeTimJson;
   }

   public void setKafkaTopicOdeTimJson(String kafkaTopicOdeTimJson) {
      this.kafkaTopicOdeTimJson = kafkaTopicOdeTimJson;
   }

   public String getUploadLocationObuLog() {
      return uploadLocationObuLogLog;
   }

   public void setUploadLocationObuLog(String uploadLocationObuLog) {
      this.uploadLocationObuLogLog = uploadLocationObuLog;
   }

   public String getKafkaTopicOdeBsmDuringEventPojo() {
      return kafkaTopicOdeBsmDuringEventPojo;
   }

   public void setKafkaTopicOdeBsmDuringEventPojo(String kafkaTopicOdeBsmDuringEventPojo) {
      this.kafkaTopicOdeBsmDuringEventPojo = kafkaTopicOdeBsmDuringEventPojo;
   }

   public String getKafkaTopicOdeBsmRxPojo() {
      return kafkaTopicOdeBsmRxPojo;
   }

   public void setKafkaTopicOdeBsmRxPojo(String kafkaTopicOdeBsmRxPojo) {
      this.kafkaTopicOdeBsmRxPojo = kafkaTopicOdeBsmRxPojo;
   }

   public String getKafkaTopicOdeBsmTxPojo() {
      return kafkaTopicOdeBsmTxPojo;
   }

   public void setKafkaTopicOdeBsmTxPojo(String kafkaTopicOdeBsmTxPojo) {
      this.kafkaTopicOdeBsmTxPojo = kafkaTopicOdeBsmTxPojo;
   }

   public String getKafkaTopicOdeTimRxJson() {
      return kafkaTopicOdeTimRxJson;
   }

   public void setKafkaTopicOdeTimRxJson(String kafkaTopicOdeTimRxJson) {
      this.kafkaTopicOdeTimRxJson = kafkaTopicOdeTimRxJson;
   }

   public String getKafkaTopicOdeTimBroadcastPojo() {
      return kafkaTopicOdeTimBroadcastPojo;
   }

   public void setKafkaTopicOdeTimBroadcastPojo(String kafkaTopicOdeTimBroadcastPojo) {
      this.kafkaTopicOdeTimBroadcastPojo = kafkaTopicOdeTimBroadcastPojo;
   }

   public String getKafkaTopicOdeTimBroadcastJson() {
      return kafkaTopicOdeTimBroadcastJson;
   }

   public void setKafkaTopicOdeTimBroadcastJson(String kafkaTopicOdeTimBroadcastJson) {
      this.kafkaTopicOdeTimBroadcastJson = kafkaTopicOdeTimBroadcastJson;
   }

   public String getKafkaTopicJ2735TimBroadcastJson() {
      return kafkaTopicJ2735TimBroadcastJson;
   }

   public void setKafkaTopicJ2735TimBroadcastJson(String kafkaTopicJ2735TimBroadcastJson) {
      this.kafkaTopicJ2735TimBroadcastJson = kafkaTopicJ2735TimBroadcastJson;
   }

   public String getKafkaTopicFilteredOdeTimJson() {
      return kafkaTopicFilteredOdeTimJson;
   }

   public void setKafkaTopicFilteredOdeTimJson(String kafkaTopicFilteredOdeTimJson) {
      this.kafkaTopicFilteredOdeTimJson = kafkaTopicFilteredOdeTimJson;
   }

   public String getKafkaTopicDriverAlertJson() {
      return kafkaTopicDriverAlertJson;
   }

   public void setKafkaTopicDriverAlertJson(String kafkaTopicDriverAlertJson) {
      this.kafkaTopicDriverAlertJson = kafkaTopicDriverAlertJson;
   }

   public Integer getFileWatcherPeriod() {
      return fileWatcherPeriod;
   }

   public void setFileWatcherPeriod(Integer fileWatcherPeriod) {
      this.fileWatcherPeriod = fileWatcherPeriod;
   }

   public String getSecuritySvcsSignatureUri() {
      return securitySvcsSignatureUri;
   }

   public void setSecuritySvcsSignatureUri(String securitySvcsSignatureUri) {
      this.securitySvcsSignatureUri = securitySvcsSignatureUri;
   }

   public String getRsuUsername() {
      return rsuUsername;
}

   public void setRsuUsername(String rsuUsername) {
      this.rsuUsername = rsuUsername;
   }

   public String getRsuPassword() {
      return rsuPassword;
   }

   public void setRsuPassword(String rsuPassword) {
      this.rsuPassword = rsuPassword;
   }

   }
