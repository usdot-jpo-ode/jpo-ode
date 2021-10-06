/*******************************************************************************
 * Copyright 2020 572682
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

package us.dot.its.jpo.ode.rsu;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Capturing;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
//import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.OdeTravelerInputData;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

//@RunWith(JMockit.class)
public class RsuDepositorTest  {

	@Tested
	RsuDepositor testRsuDepositor;

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
	public void shouldConstruct(@Mocked OdeProperties mockOdeProperties) {
		testRsuDepositor = new RsuDepositor(mockOdeProperties);
		testRsuDepositor.start();
		assertEquals(mockOdeProperties, testRsuDepositor.getOdeProperties());

	}

	@Test
	public void testShutdown() {
		testRsuDepositor.shutdown();
		assertEquals(false, testRsuDepositor.isRunning());
		assertEquals(false, testRsuDepositor.isAlive());

	}


	@Test
	public void testDeposit(@Mocked OdeTravelerInputData mockOdeTravelerInputData)
			throws DdsRequestManagerException, IOException, ParseException {
			      
	      testRsuDepositor.deposit(mockOdeTravelerInputData.getRequest(), "message");
	
	}




}
