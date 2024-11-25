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
package us.dot.its.jpo.ode.services.asn1;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import mockit.Capturing;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.SDXDepositorTopics;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.rsu.RsuProperties;
import us.dot.its.jpo.ode.security.SecurityServicesProperties;
import us.dot.its.jpo.ode.services.asn1.Asn1CommandManager.Asn1CommandManagerException;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class Asn1CommandManagerTest {

   @Tested
   Asn1CommandManager testAsn1CommandManager;

   @Injectable
   OdeKafkaProperties injectableOdeKafkaProperties;

   @Injectable
   SDXDepositorTopics injectableSDXDepositorTopics;

   @Injectable
   RsuProperties injectableRsuProperties;

   @Injectable
   SecurityServicesProperties injectableSecurityServicesProperties;

   @Capturing
   MessageProducer<String, String> capturingMessageProducer;
   @Capturing
   SnmpSession capturingSnmpSession;

   @Injectable
   OdeTravelerInputData injectableOdeTravelerInputData;

   @Mocked
   MessageProducer<String, String> mockMessageProducer;

   @Test
   public void testPackageSignedTimIntoAsd() {
      testAsn1CommandManager.packageSignedTimIntoAsd(injectableOdeTravelerInputData.getRequest(), "message");
   }

   @Test
   public void depositToSDWJsonShouldCallMessageProducer() throws Asn1CommandManagerException {
      JSONObject deposit = new JSONObject();
      deposit.put("estimatedRemovalDate", "2023-11-04T17:47:11-05:00");
      deposit.put("encodedMsg", "message");

      testAsn1CommandManager.depositToSdw(deposit.toString());
   }

   @Test
   public void depositToSDWShouldCallMessageProducer() throws Asn1CommandManagerException {
      testAsn1CommandManager.depositToSdw("message");
   }

   @Test
   public void testSendToRsus(@Mocked OdeTravelerInputData mockOdeTravelerInputData)
         throws IOException, ParseException {

      testAsn1CommandManager.sendToRsus(mockOdeTravelerInputData.getRequest(), "message");
   }

   @Test
   public void testSendToRsusSnmpException(@Mocked OdeTravelerInputData mockOdeTravelerInputData)
         throws IOException, ParseException {

      testAsn1CommandManager.sendToRsus(mockOdeTravelerInputData.getRequest(), "message");
   }

}
