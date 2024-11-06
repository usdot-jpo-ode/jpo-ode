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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.plugin.OdePlugin;


@Configuration
@ConfigurationProperties(prefix = "ode")
@Data
public class OdeProperties implements EnvironmentAware {

   private static final Logger logger = LoggerFactory.getLogger(OdeProperties.class);

   @Autowired
   private Environment env;

   /*
    * General Properties
    */
   public static final int OUTPUT_SCHEMA_VERSION = 7;
   private String pluginsLocations = "plugins";
   @Value("${ode.host-ip:localhost}")
   private String hostIP;

   private Boolean verboseJson = false;
   private int importProcessorBufferSize = OdePlugin.INPUT_STREAM_BUFFER_SIZE;
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
   private String uploadLocationObuLog = "bsmlog";
   private Integer fileWatcherPeriod = 5; // time to wait between processing inbox directory for new files

   /*
    * UDP Properties
    */
   private int trustRetries = 2; // if trust handshake fails, how many times to retry
   private int messagesUntilTrustReestablished = 10; // renew trust session every x messages

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
      logger.info("groupId: {}", buildProperties.getGroup());
      logger.info("artifactId: {}", buildProperties.getArtifact());
      logger.info("version: {}", buildProperties.getVersion());
      OdeMsgMetadata.setStaticSchemaVersion(OUTPUT_SCHEMA_VERSION);

      uploadLocations.add(Paths.get(uploadLocationRoot));

      // URI for the security services /sign endpoint
      if (securitySvcsSignatureUri == null) {
         securitySvcsSignatureUri = "http://" + hostIP + ":" + securitySvcsPort + "/"
               + securitySvcsSignatureEndpoint;
      }
   }

   public String getVersion() {
      return buildProperties.getVersion();
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

   @Override
   public void setEnvironment(Environment environment) {
      env = environment;
   }
}
