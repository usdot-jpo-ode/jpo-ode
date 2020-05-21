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

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.ServiceRequest.OdeInternal.RequestVerb;
import us.dot.its.jpo.ode.services.asn1.Asn1CommandManager.Asn1CommandManagerException;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class Asn1CommandManagerTest {

   @Tested
   Asn1CommandManager testAsn1CommandManager;

   @Injectable
   OdeProperties injectableOdeProperties;

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
   public void depositToSDWShouldCallMessageProducer() throws Asn1CommandManagerException {
      testAsn1CommandManager.depositToSdw("message");
   }

   @Test
   public void testSendToRsus(@Mocked OdeTravelerInputData mockOdeTravelerInputData)
         throws DdsRequestManagerException, IOException, ParseException {
      
      testAsn1CommandManager.sendToRsus(mockOdeTravelerInputData.getRequest(), "message");
   }

   @Test
   public void testSendToRsusSnmpException(@Mocked OdeTravelerInputData mockOdeTravelerInputData)
         throws DdsRequestManagerException, IOException, ParseException {
     
      testAsn1CommandManager.sendToRsus(mockOdeTravelerInputData.getRequest(), "message");
   }

}
