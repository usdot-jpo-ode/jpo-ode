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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.util.CommonUtils;

class OdePropertiesTest {

      @Tested
      OdeProperties testOdeProperties;
      @Injectable
      Environment mockEnv;
      @Injectable
      BuildProperties mockBuildProperties;

      @Capturing
      CommonUtils capturingCommonUtils;

      @Test
      void testInit() {
            new Expectations() {
                  {
                  }
            };
            try {
                  new OdeProperties();
            } catch (Exception e) {
                  fail("Unexpected exception: " + e);
            }
      }

      @Test
      void testSettersAndGetters() {

            String testPluginsLocations = "testpluginsLocations123456";
            String testUploadLocationObuLog = "testuploadLocationObuLog123456";
            String testUploadLocationRoot = "testUploadLocationRoot123456";
            int testMessagesUntilTrustReestablished = 17;
            String testCaCertPath = "testCaCertPath";
            String testSelfCertPath = "testSelfCertPath";
            String testSelfPrivateKeyReconstructionFilePath = "testSelfPrivateKeyReconstructionFilePath";
            String testSelfSigningPrivateKeyFilePath = "testSelfSigningPrivateKeyFilePath";
            String testKafkaTopicBsmFilteredJson = "testKafkaTopicBsmFilteredJson";
            boolean testVerboseJson = true;
            int testRsuSrmSlots = 22;
            int testTrustRetries = 23;
            String testKafkaTopicOdeBsmPojo = "testKafkaTopicOdeBsmPojo";
            String testKafkaTopicOdeBsmJson = "testKafkaTopicOdeBsmJson";
            int testImportProcessorBufferSize = 83;

            String testKafkaTopicAsn1DecoderInput = "testKafkaTopicAsn1DecoderInput";
            String testKafkaTopicAsn1DecoderOutput = "testKafkaTopicAsn1DecoderOutput";
            String testKafkaTopicAsn1EncoderInput = "testKafkaTopicAsn1EncoderInput";
            String testKafkaTopicAsn1EncoderOutput = "testKafkaTopicAsn1EncoderOutput";
            String testKafkaTopicOdeDNMsgJson = "testKafkaTopicOdeDNMsgJson";
            String testKafkaTopicOdeTimJson = "testKafkaTopicOdeTimJson";
            String testKafkaTopicOdeBsmDuringEventPojo = "testKafkaTopicOdeBsmDuringEventPojo";
            String testKafkaTopicOdeBsmRxPojo = "testKafkaTopicOdeBsmRxPojo";
            String testKafkaTopicOdeBsmTxPojo = "testKafkaTopicOdeBsmTxPojo";
            String testKafkaTopicOdeTimRxJson = "testKafkaTopicOdeTimRxJson";
            String testKafkaTopicOdeTimBroadcastPojo = "testKafkaTopicOdeTimBroadcastPojo";
            String testKafkaTopicOdeTimBroadcastJson = "testKafkaTopicOdeTimBroadcastJson";
            String testKafkaTopicJ2735TimBroadcastJson = "testKafkaTopicJ2735TimBroadcastJson";
            String testKafkaTopicFilteredOdeTimJson = "testKafkaTopicFilteredOdeTimJson";
            String testKafkaTopicDriverAlertJson = "testKafkaTopicDriverAlertJson";

            Integer testFileWatcherPeriod = 5;
            String testSecuritySvcsSignatureUri = "testSecuritySvcsSignatureUri";
            String testRsuUsername = "testRsuUsername";
            String testRsuPassword = "testRsuPassword";

            testOdeProperties.setHostIP("test-host");
            testOdeProperties.setEnv(mockEnv);
            testOdeProperties.setEnvironment(mockEnv);
            testOdeProperties.setPluginsLocations(testPluginsLocations);
            testOdeProperties.setUploadLocationObuLog(testUploadLocationObuLog);
            testOdeProperties.setUploadLocationRoot(testUploadLocationRoot);
            testOdeProperties.setMessagesUntilTrustReestablished(testMessagesUntilTrustReestablished);
            testOdeProperties.setCaCertPath(testCaCertPath);
            testOdeProperties.setSelfCertPath(testSelfCertPath);
            testOdeProperties.setSelfPrivateKeyReconstructionFilePath(testSelfPrivateKeyReconstructionFilePath);
            testOdeProperties.setSelfSigningPrivateKeyFilePath(testSelfSigningPrivateKeyFilePath);
            testOdeProperties.setKafkaTopicFilteredOdeBsmJson(testKafkaTopicBsmFilteredJson);
            testOdeProperties.setVerboseJson(testVerboseJson);
            testOdeProperties.setRsuSrmSlots(testRsuSrmSlots);
            testOdeProperties.setTrustRetries(testTrustRetries);
            testOdeProperties.setKafkaTopicOdeBsmPojo(testKafkaTopicOdeBsmPojo);
            testOdeProperties.setKafkaTopicOdeBsmJson(testKafkaTopicOdeBsmJson);
            testOdeProperties.setImportProcessorBufferSize(testImportProcessorBufferSize);

            testOdeProperties.setKafkaTopicAsn1DecoderInput(testKafkaTopicAsn1DecoderInput);
            testOdeProperties.setKafkaTopicAsn1DecoderOutput(testKafkaTopicAsn1DecoderOutput);
            testOdeProperties.setKafkaTopicAsn1EncoderInput(testKafkaTopicAsn1EncoderInput);
            testOdeProperties.setKafkaTopicAsn1EncoderOutput(testKafkaTopicAsn1EncoderOutput);
            testOdeProperties.setKafkaTopicOdeDNMsgJson(testKafkaTopicOdeDNMsgJson);
            testOdeProperties.setKafkaTopicOdeTimJson(testKafkaTopicOdeTimJson);
            testOdeProperties.setKafkaTopicOdeBsmDuringEventPojo(testKafkaTopicOdeBsmDuringEventPojo);
            testOdeProperties.setKafkaTopicOdeBsmRxPojo(testKafkaTopicOdeBsmRxPojo);
            testOdeProperties.setKafkaTopicOdeBsmTxPojo(testKafkaTopicOdeBsmTxPojo);
            testOdeProperties.setKafkaTopicOdeTimRxJson(testKafkaTopicOdeTimRxJson);
            testOdeProperties.setKafkaTopicOdeTimBroadcastPojo(testKafkaTopicOdeTimBroadcastPojo);
            testOdeProperties.setKafkaTopicOdeTimBroadcastJson(testKafkaTopicOdeTimBroadcastJson);
            testOdeProperties.setKafkaTopicJ2735TimBroadcastJson(testKafkaTopicJ2735TimBroadcastJson);
            testOdeProperties.setKafkaTopicFilteredOdeTimJson(testKafkaTopicFilteredOdeTimJson);
            testOdeProperties.setKafkaTopicDriverAlertJson(testKafkaTopicDriverAlertJson);

            testOdeProperties.setFileWatcherPeriod(testFileWatcherPeriod);
            testOdeProperties.setSecuritySvcsSignatureUri(testSecuritySvcsSignatureUri);
            testOdeProperties.setRsuUsername(testRsuUsername);
            testOdeProperties.setRsuPassword(testRsuPassword);

            assertEquals("test-host", testOdeProperties.getHostIP());
            assertEquals("Incorrect testEnv", mockEnv, testOdeProperties.getEnv());
            assertEquals("Incorrect testpluginsLocations", testPluginsLocations,
                        testOdeProperties.getPluginsLocations());
            assertEquals("Incorrect testUploadLocationObuLog", testUploadLocationObuLog,
                        testOdeProperties.getUploadLocationObuLog());
            assertEquals("Incorrect testUploadLocationRoot", testUploadLocationRoot,
                        testOdeProperties.getUploadLocationRoot());
            assertEquals("Incorrect testMessagesUntilTrustReestablished", testMessagesUntilTrustReestablished,
                        testOdeProperties.getMessagesUntilTrustReestablished());
            assertEquals("Incorrect testCaCertPath", testCaCertPath, testOdeProperties.getCaCertPath());
            assertEquals("Incorrect testSelfCertPath", testSelfCertPath, testOdeProperties.getSelfCertPath());
            assertEquals("Incorrect testSelfPrivateKeyReconstructionFilePath", testSelfPrivateKeyReconstructionFilePath,
                        testOdeProperties.getSelfPrivateKeyReconstructionFilePath());
            assertEquals("Incorrect testSelfSigningPrivateKeyFilePath", testSelfSigningPrivateKeyFilePath,
                        testOdeProperties.getSelfSigningPrivateKeyFilePath());
            assertEquals("Incorrect testKafkaTopicBsmFilteredJson", testKafkaTopicBsmFilteredJson,
                        testOdeProperties.getKafkaTopicFilteredOdeBsmJson());
            assertEquals("Incorrect testVerboseJson", testVerboseJson, testOdeProperties.getVerboseJson());
            assertEquals("Incorrect testRsuSrmSlots", testRsuSrmSlots, testOdeProperties.getRsuSrmSlots());
            assertEquals("Incorrect testTrustRetries", testTrustRetries, testOdeProperties.getTrustRetries());
            assertEquals("Incorrect testKafkaTopicOdeBsmPojo", testKafkaTopicOdeBsmPojo,
                        testOdeProperties.getKafkaTopicOdeBsmPojo());
            assertEquals("Incorrect testKafkaTopicOdeBsmJson", testKafkaTopicOdeBsmJson,
                        testOdeProperties.getKafkaTopicOdeBsmJson());
            assertEquals("Incorrect testImportProcessorBufferSize", testImportProcessorBufferSize,
                        testOdeProperties.getImportProcessorBufferSize());

            assertEquals("Incorrect testKafkaTopicAsn1DecoderInput", testKafkaTopicAsn1DecoderInput,
                        testOdeProperties.getKafkaTopicAsn1DecoderInput());
            assertEquals("Incorrect testKafkaTopicAsn1DecoderOutput", testKafkaTopicAsn1DecoderOutput,
                        testOdeProperties.getKafkaTopicAsn1DecoderOutput());
            assertEquals("Incorrect testKafkaTopicAsn1EncoderInput", testKafkaTopicAsn1EncoderInput,
                        testOdeProperties.getKafkaTopicAsn1EncoderInput());
            assertEquals("Incorrect testKafkaTopicAsn1EncoderOutput", testKafkaTopicAsn1EncoderOutput,
                        testOdeProperties.getKafkaTopicAsn1EncoderOutput());
            assertEquals("Incorrect testKafkaTopicOdeDNMsgJson", testKafkaTopicOdeDNMsgJson,
                        testOdeProperties.getKafkaTopicOdeDNMsgJson());
            assertEquals("Incorrect testKafkaTopicOdeTimJson", testKafkaTopicOdeTimJson,
                        testOdeProperties.getKafkaTopicOdeTimJson());
            assertEquals("Incorrect testKafkaTopicOdeBsmDuringEventPojo", testKafkaTopicOdeBsmDuringEventPojo,
                        testOdeProperties.getKafkaTopicOdeBsmDuringEventPojo());
            assertEquals("Incorrect testKafkaTopicOdeBsmRxPojo", testKafkaTopicOdeBsmRxPojo,
                        testOdeProperties.getKafkaTopicOdeBsmRxPojo());
            assertEquals("Incorrect testKafkaTopicOdeBsmTxPojo", testKafkaTopicOdeBsmTxPojo,
                        testOdeProperties.getKafkaTopicOdeBsmTxPojo());
            assertEquals("Incorrect testKafkaTopicOdeTimRxJson", testKafkaTopicOdeTimRxJson,
                        testOdeProperties.getKafkaTopicOdeTimRxJson());
            assertEquals("Incorrect testKafkaTopicOdeTimBroadcastPojo", testKafkaTopicOdeTimBroadcastPojo,
                        testOdeProperties.getKafkaTopicOdeTimBroadcastPojo());
            assertEquals("Incorrect testKafkaTopicOdeTimBroadcastJson", testKafkaTopicOdeTimBroadcastJson,
                        testOdeProperties.getKafkaTopicOdeTimBroadcastJson());
            assertEquals("Incorrect testKafkaTopicJ2735TimBroadcastJson", testKafkaTopicJ2735TimBroadcastJson,
                        testOdeProperties.getKafkaTopicJ2735TimBroadcastJson());
            assertEquals("Incorrect testKafkaTopicFilteredOdeTimJson", testKafkaTopicFilteredOdeTimJson,
                        testOdeProperties.getKafkaTopicFilteredOdeTimJson());
            assertEquals("Incorrect testKafkaTopicDriverAlertJson", testKafkaTopicDriverAlertJson,
                        testOdeProperties.getKafkaTopicDriverAlertJson());

            assertEquals("Incorrect testFileWatcherPeriod", testFileWatcherPeriod,
                        testOdeProperties.getFileWatcherPeriod());
            assertEquals("Incorrect testSecuritySvcsSignatureUri", testSecuritySvcsSignatureUri,
                        testOdeProperties.getSecuritySvcsSignatureUri());
            assertEquals("Incorrect testRsuUsername", testRsuUsername, testOdeProperties.getRsuUsername());
            assertEquals("Incorrect RsuPassword", testRsuPassword, testOdeProperties.getRsuPassword());

            testOdeProperties.getProperty("testProperty");
            testOdeProperties.getProperty("testProperty", 5);
            testOdeProperties.getProperty("testProperty", "testDefaultValue");
            testOdeProperties.getUploadLocations();
      }

}
