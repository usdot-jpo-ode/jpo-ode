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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
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
   public static final int OUTPUT_SCHEMA_VERSION = 7;
   private String pluginsLocations = "plugins";
   private String kafkaBrokers = null;
   private static final String DEFAULT_KAFKA_PORT = "9092";
   private String kafkaProducerType = AppContext.DEFAULT_KAFKA_PRODUCER_TYPE;
   private Boolean verboseJson = false;
   private int importProcessorBufferSize = OdePlugin.INPUT_STREAM_BUFFER_SIZE;
   private String hostId;
   private List<Path> uploadLocations = new ArrayList<>();

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
    * UDP Properties
    */
   private int trustRetries = 2; // if trust handshake fails, how many times to retry
   private int messagesUntilTrustReestablished = 10; // renew trust session every x messages

   /*
    * Kafka Topics
    * 
    */
   private String[] kafkaTopicsDisabled = {
         // disable all POJO topics by default except "topic.OdeBsmPojo". Never
         // "topic.OdeBsmPojo because that's the only way to get data into
         // "topic.OdeBsmJson
         "topic.OdeBsmRxPojo", "topic.OdeBsmTxPojo", "topic.OdeBsmDuringEventPojo", "topic.OdeTimBroadcastPojo" };
   private Set<String> kafkaTopicsDisabledSet = new HashSet<>();

   // BSM
   private String kafkaTopicOdeBsmPojo = "topic.OdeBsmPojo";
   private String kafkaTopicOdeBsmJson = "topic.OdeBsmJson";
   private String kafkaTopicOdeBsmRxPojo = "topic.OdeBsmRxPojo";
   private String kafkaTopicOdeBsmTxPojo = "topic.OdeBsmTxPojo";
   private String kafkaTopicOdeBsmDuringEventPojo = "topic.OdeBsmDuringEventPojo";
   private String kafkaTopicFilteredOdeBsmJson = "topic.FilteredOdeBsmJson";
   private String kafkaTopicOdeRawEncodedBSMJson = "topic.OdeRawEncodedBSMJson";
   private int bsmReceiverPort = 46800;
   private int bsmBufferSize = 500;

   // TIM
   private String kafkaTopicOdeTimJson = "topic.OdeTimJson";
   private String kafkaTopicOdeDNMsgJson = "topic.OdeDNMsgJson";
   private String kafkaTopicOdeTimRxJson = "topic.OdeTimRxJson";
   private String kafkaTopicOdeTimBroadcastPojo = "topic.OdeTimBroadcastPojo";
   private String kafkaTopicOdeTimBroadcastJson = "topic.OdeTimBroadcastJson";
   private String kafkaTopicJ2735TimBroadcastJson = "topic.J2735TimBroadcastJson";
   private String kafkaTopicFilteredOdeTimJson = "topic.FilteredOdeTimJson";
   private String kafkaTopicOdeRawEncodedTIMJson = "topic.OdeRawEncodedTIMJson";
   private int timReceiverPort = 47900;
   private int timBufferSize = 500;
   
   //SPAT
   private String kafkaTopicOdeSpatTxPojo = "topic.OdeSpatTxPojo";
   private String kafkaTopicOdeSpatPojo = "topic.OdeSpatPojo";
   private String kafkaTopicOdeSpatJson = "topic.OdeSpatJson";
   private String kafkaTopicOdeSpatRxPojo = "topic.OdeSpatRxPojo";
   private String kafkaTopicOdeSpatRxJson = "topic.OdeSpatRxJson";
   private String kafkaTopicFilteredOdeSpatJson = "topic.FilteredOdeSpatJson";
   private String kafkaTopicOdeRawEncodedSPATJson = "topic.OdeRawEncodedSPATJson";
   private int spatReceiverPort = 44910;
   private int spatBufferSize = 500;

   //SSM
   private String kafkaTopicOdeSsmPojo = "topic.OdeSsmPojo";
   private String kafkaTopicOdeSsmJson = "topic.OdeSsmJson";
   private String kafkaTopicOdeRawEncodedSSMJson = "topic.OdeRawEncodedSSMJson";
   private int ssmReceiverPort = 44900;
   private int ssmBufferSize = 500;

   //SRM
   private String kafkaTopicOdeSrmTxPojo = "topic.OdeSrmTxPojo";
   private String kafkaTopicOdeSrmJson = "topic.OdeSrmJson";
   private String kafkaTopicOdeRawEncodedSRMJson = "topic.OdeRawEncodedSRMJson";
   private int srmReceiverPort = 44930;
   private int srmBufferSize = 500;
   
   //MAP
   private String kafkaTopicOdeRawEncodedMAPJson = "topic.OdeRawEncodedMAPJson";
   private String kafkaTopicOdeMapTxPojo = "topic.OdeMapTxPojo";
   private String kafkaTopicOdeMapJson = "topic.OdeMapJson";
   private int mapReceiverPort = 44920;
   private int mapBufferSize = 2048;

   // PSM
   private String kafkaTopicOdeRawEncodedPSMJson = "topic.OdeRawEncodedPSMJson";
   private String kafkaTopicOdePsmTxPojo = "topic.OdePsmTxPojo";
   private String kafkaTopicOdePsmJson = "topic.OdePsmJson";
   private int psmReceiverPort = 44940;
   private int psmBufferSize = 500;
 
   // Generic Receiver
   private int genericReceiverPort = 44990;
   private int genericBufferSize = 2000;
   
// DriverAlerts
   private String kafkaTopicDriverAlertJson = "topic.OdeDriverAlertJson";

   // ASN.1 CODEC
   private String kafkaTopicAsn1DecoderInput = "topic.Asn1DecoderInput";
   private String kafkaTopicAsn1DecoderOutput = "topic.Asn1DecoderOutput";
   private String kafkaTopicAsn1EncoderInput = "topic.Asn1EncoderInput";
   private String kafkaTopicAsn1EncoderOutput = "topic.Asn1EncoderOutput";

   // SDX Depositor Module
   private String kafkaTopicSdwDepositorInput = "topic.SDWDepositorInput";

   //Signed Tim with expiration
   private String kafkaTopicSignedOdeTimJsonExpiration = "topic.OdeTIMCertExpirationTimeJson";
   /*
    * Security Properties
    */
   private String caCertPath;
   private String selfCertPath;
   private String selfPrivateKeyReconstructionFilePath;
   private String selfSigningPrivateKeyFilePath;

   private static final byte[] JPO_ODE_GROUP_ID = "jode".getBytes();

   @Autowired
   BuildProperties buildProperties;

   @PostConstruct
   void initialize() {
      setVersion(buildProperties.getVersion());
      logger.info("groupId: {}", buildProperties.getGroup());
      logger.info("artifactId: {}", buildProperties.getArtifact());
      logger.info("version: {}", version);
      OdeMsgMetadata.setStaticSchemaVersion(OUTPUT_SCHEMA_VERSION);

      uploadLocations.add(Paths.get(uploadLocationRoot));

      String hostname;
      try {
         hostname = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException e) {
         // Let's just use a random hostname
         hostname = UUID.randomUUID().toString();
         logger.error("Unknown host error: {}, using random", e);
      }
      hostId = hostname;
      logger.info("Host ID: {}", hostId);
      EventLogger.logger.info("Initializing services on host {}", hostId);

      if (kafkaBrokers == null) {

         logger.warn("ode.kafkaBrokers property not defined. Will try DOCKER_HOST_IP => {}", kafkaBrokers);

         String dockerIp = CommonUtils.getEnvironmentVariable("DOCKER_HOST_IP");

         if (dockerIp == null) {
            logger.warn(
                  "Neither ode.kafkaBrokers ode property nor DOCKER_HOST_IP environment variable are defined. Defaulting to localhost.");
            dockerIp = "localhost";
         }
         kafkaBrokers = dockerIp + ":" + DEFAULT_KAFKA_PORT;

         // URI for the security services /sign endpoint
         if (securitySvcsSignatureUri == null) {
            securitySvcsSignatureUri = "http://" + dockerIp + ":" + securitySvcsPort + "/"
                  + securitySvcsSignatureEndpoint;
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

   public int getMessagesUntilTrustReestablished() {
      return messagesUntilTrustReestablished;
   }

   public void setMessagesUntilTrustReestablished(int messagesUntilTrustReestablished) {
      this.messagesUntilTrustReestablished = messagesUntilTrustReestablished;
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

   public Boolean getVerboseJson() {
      return verboseJson;
   }

   public void setVerboseJson(Boolean verboseJson) {
      this.verboseJson = verboseJson;
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

   public int getTimReceiverPort() {
      return timReceiverPort;
   }

   public void setTimReceiverPort(int timReceiverPort) {
      this.timReceiverPort = timReceiverPort;
   }

   public int getTimBufferSize() {
      return timBufferSize;
   }

   public void setTimBufferSize(int timBufferSize) {
      this.timBufferSize = timBufferSize;
   }

   public int getSsmReceiverPort() {
      return ssmReceiverPort;
   }

   public void setSsmReceiverPort(int ssmReceiverPort) {
      this.ssmReceiverPort = ssmReceiverPort;
   }

   public int getSsmBufferSize() {
      return ssmBufferSize;
   }

   public void setSsmBufferSize(int ssmBufferSize) {
      this.ssmBufferSize = ssmBufferSize;
   }

   public int getSrmReceiverPort() {
      return srmReceiverPort;
   }

   public void setSrmReceiverPort(int srmReceiverPort) {
      this.srmReceiverPort = srmReceiverPort;
   }

   public int getSrmBufferSize() {
      return srmBufferSize;
   }

   public void setSrmBufferSize(int srmBufferSize) {
      this.srmBufferSize = srmBufferSize;
   }

   public int getSpatReceiverPort() {
      return spatReceiverPort;
   }

   public void setSpatReceiverPort(int spatReceiverPort) {
      this.spatReceiverPort = spatReceiverPort;
   }

   public int getSpatBufferSize() {
      return spatBufferSize;
   }

   public void setSpatBufferSize(int spatBufferSize) {
      this.spatBufferSize = spatBufferSize;
   }

   public int getMapReceiverPort() {
      return mapReceiverPort;
   }

   public void setMapReceiverPort(int mapReceiverPort) {
      this.mapReceiverPort = mapReceiverPort;
   }

   public int getMapBufferSize() {
      return mapBufferSize;
   }

   public void setMapBufferSize(int mapBufferSize) {
      this.mapBufferSize = mapBufferSize;
   }

   public int getPsmReceiverPort() {
      return psmReceiverPort;
   }

   public void setPsmReceiverPort(int psmReceiverPort) {
      this.psmReceiverPort = psmReceiverPort;
   }

   public int getPsmBufferSize() {
      return psmBufferSize;
   }

   public void setPsmBufferSize(int psmBufferSize) {
      this.psmBufferSize = psmBufferSize;
   }

   public int getGenericReceiverPort() {
      return genericReceiverPort;
   }

   public void setGenericReceiverPort(int genericReceiverPort) {
      this.genericReceiverPort = genericReceiverPort;
   }

   public int getGenericBufferSize() {
      return genericBufferSize;
   }

   public void setGenericBufferSize(int psmBufferSize) {
      this.genericBufferSize = genericBufferSize;
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

   public void setKafkaTopicsDisabledSet(Set<String> kafkaTopicsDisabledSet) {
      this.kafkaTopicsDisabledSet = kafkaTopicsDisabledSet;
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

   public String getKafkaTopicOdeDNMsgJson() {
      return kafkaTopicOdeDNMsgJson;
   }

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

   public String getKafkaTopicSdwDepositorInput() {
      return kafkaTopicSdwDepositorInput;
   }

   public void setKafkaTopicSdwDepositorInput(String kafkaTopicSdwDepositorInput) {
      this.kafkaTopicSdwDepositorInput = kafkaTopicSdwDepositorInput;
   }

   public String getKafkaTopicSignedOdeTimJsonExpiration() {
      return kafkaTopicSignedOdeTimJsonExpiration;
   }
   public void setKafkaTopicSignedOdeTimJsonExpiration(String kafkaTopicSignedOdeTimJsonExpiration) {
      this.kafkaTopicSignedOdeTimJsonExpiration = kafkaTopicSignedOdeTimJsonExpiration;
   }
	
	public String getKafkaTopicOdeSpatTxPojo() {
		return kafkaTopicOdeSpatTxPojo;
	}
	
	
	public void setKafkaTopicOdeSpatTxPojo(String kafkaTopicOdeSpatTxPojo) {
		this.kafkaTopicOdeSpatTxPojo = kafkaTopicOdeSpatTxPojo;
	}


	public String getKafkaTopicOdeSpatPojo() {
		return kafkaTopicOdeSpatPojo;
	}


	public void setKafkaTopicOdeSpatPojo(String kafkaTopicOdeSpatPojo) {
		this.kafkaTopicOdeSpatPojo = kafkaTopicOdeSpatPojo;
	}


	public String getKafkaTopicOdeSpatJson() {
		return kafkaTopicOdeSpatJson;
	}


	public void setKafkaTopicOdeSpatJson(String kafkaTopicOdeSpatJson) {
		this.kafkaTopicOdeSpatJson = kafkaTopicOdeSpatJson;
	}


	public String getKafkaTopicOdeSpatRxPojo() {
		return kafkaTopicOdeSpatRxPojo;
	}


	public void setKafkaTopicOdeSpatRxPojo(String kafkaTopicOdeSpatRxPojo) {
		this.kafkaTopicOdeSpatRxPojo = kafkaTopicOdeSpatRxPojo;
	}


	public String getKafkaTopicOdeSpatRxJson() {
		return kafkaTopicOdeSpatRxJson;
	}


	public void setKafkaTopicOdeSpatRxJson(String kafkaTopicOdeSpatRxJson) {
		this.kafkaTopicOdeSpatRxJson = kafkaTopicOdeSpatRxJson;
	}


	public String getKafkaTopicFilteredOdeSpatJson() {
		return kafkaTopicFilteredOdeSpatJson;
	}


	public void setKafkaTopicFilteredOdeSpatJson(String kafkaTopicFilteredOdeSpatJson) {
		this.kafkaTopicFilteredOdeSpatJson = kafkaTopicFilteredOdeSpatJson;
	}


	public String getKafkaTopicOdeRawEncodedBSMJson() {
		return kafkaTopicOdeRawEncodedBSMJson;
	}


	public void setKafkaTopicOdeRawEncodedBSMJson(String kafkaTopicOdeRawEncodedBSMJson) {
		this.kafkaTopicOdeRawEncodedBSMJson = kafkaTopicOdeRawEncodedBSMJson;
	}


	public String getKafkaTopicOdeRawEncodedTIMJson() {
		return kafkaTopicOdeRawEncodedTIMJson;
	}


	public void setKafkaTopicOdeRawEncodedTIMJson(String kafkaTopicOdeRawEncodedTIMJson) {
		this.kafkaTopicOdeRawEncodedTIMJson = kafkaTopicOdeRawEncodedTIMJson;
	}


	public String getKafkaTopicOdeRawEncodedSPATJson() {
		return kafkaTopicOdeRawEncodedSPATJson;
	}


	public void setKafkaTopicOdeRawEncodedSPATJson(String kafkaTopicOdeRawEncodedSPATJson) {
		this.kafkaTopicOdeRawEncodedSPATJson = kafkaTopicOdeRawEncodedSPATJson;
	}
	
	public String getKafkaTopicOdeRawEncodedMAPJson() {
		return kafkaTopicOdeRawEncodedMAPJson;
	}

	public void setKafkaTopicOdeRawEncodedMAPJson(String kafkaTopicOdeRawEncodedMAPJson) {
		this.kafkaTopicOdeRawEncodedMAPJson = kafkaTopicOdeRawEncodedMAPJson;
	}


	public String getKafkaTopicOdeMapTxPojo() {
		return kafkaTopicOdeMapTxPojo;
	}


	public void setKafkaTopicOdeMapTxPojo(String kafkaTopicOdeMapTxPojo) {
		this.kafkaTopicOdeMapTxPojo = kafkaTopicOdeMapTxPojo;
	}


	public String getKafkaTopicOdeMapJson() {
		return kafkaTopicOdeMapJson;
	}


	public void setKafkaTopicOdeMapJson(String kafkaTopicOdeMapJson) {
		this.kafkaTopicOdeMapJson = kafkaTopicOdeMapJson;
	}

   public String getKafkaTopicOdeRawEncodedPSMJson() {
		return kafkaTopicOdeRawEncodedPSMJson;
	}

	public void setKafkaTopicOdeRawEncodedPSMJson(String kafkaTopicOdeRawEncodedPSMJson) {
		this.kafkaTopicOdeRawEncodedPSMJson = kafkaTopicOdeRawEncodedPSMJson;
	}


	public String getKafkaTopicOdePsmTxPojo() {
		return kafkaTopicOdePsmTxPojo;
	}


	public void setKafkaTopicOdePsmTxPojo(String kafkaTopicOdePsmTxPojo) {
		this.kafkaTopicOdePsmTxPojo = kafkaTopicOdePsmTxPojo;
	}


	public String getKafkaTopicOdePsmJson() {
		return kafkaTopicOdePsmJson;
	}


	public void setKafkaTopicOdePsmJson(String kafkaTopicOdePsmJson) {
		this.kafkaTopicOdePsmJson = kafkaTopicOdePsmJson;
	}

   public String getKafkaTopicOdeRawEncodedSSMJson() {
		return kafkaTopicOdeRawEncodedSSMJson;
	}

	public void setKafkaTopicOdeRawEncodedSSMJson(String kafkaTopicOdeRawEncodedSSMJson) {
		this.kafkaTopicOdeRawEncodedSSMJson = kafkaTopicOdeRawEncodedSSMJson;
	}


	public String getKafkaTopicOdeSsmPojo() {
		return kafkaTopicOdeSsmPojo;
	}


	public void setKafkaTopicOdeSsmPojo(String kafkaTopicOdeSsmPojo) {
		this.kafkaTopicOdeSsmPojo = kafkaTopicOdeSsmPojo;
	}


	public String getKafkaTopicOdeSsmJson() {
		return kafkaTopicOdeSsmJson;
	}


	public void setKafkaTopicOdeSsmJson(String kafkaTopicOdeSsmJson) {
		this.kafkaTopicOdeSsmJson = kafkaTopicOdeSsmJson;
	}

   public String getKafkaTopicOdeRawEncodedSRMJson() {
		return kafkaTopicOdeRawEncodedSRMJson;
	}

	public void setKafkaTopicOdeRawEncodedSRMJson(String kafkaTopicOdeRawEncodedSRMJson) {
		this.kafkaTopicOdeRawEncodedSRMJson = kafkaTopicOdeRawEncodedSRMJson;
	}


	public String getKafkaTopicOdeSrmTxPojo() {
		return kafkaTopicOdeSrmTxPojo;
	}


	public void setKafkaTopicOdeSrmTxPojo(String kafkaTopicOdeSrmTxPojo) {
		this.kafkaTopicOdeSrmTxPojo = kafkaTopicOdeSrmTxPojo;
	}


	public String getKafkaTopicOdeSrmJson() {
		return kafkaTopicOdeSrmJson;
	}


	public void setKafkaTopicOdeSrmJson(String kafkaTopicOdeSrmJson) {
		this.kafkaTopicOdeSrmJson = kafkaTopicOdeSrmJson;
	}
	   
}
