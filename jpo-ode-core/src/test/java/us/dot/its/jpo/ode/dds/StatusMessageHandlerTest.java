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
package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.junit.Test;

import ch.qos.logback.classic.Logger;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import us.dot.its.jpo.ode.model.OdeControlData;
import us.dot.its.jpo.ode.model.OdeStatus;
import us.dot.its.jpo.ode.model.StatusTag;
import us.dot.its.jpo.ode.wrapper.WebSocketClient;

public class StatusMessageHandlerTest {

	@Mocked
	WebSocketClient mockWebSocketClient;

	@Test
	public void testOnMessage(@Mocked DdsStatusMessage statusMsg, @Mocked OdeControlData controlData,
			@Mocked final Logger logger) {
		StatusMessageHandler statusMessageHandler = new StatusMessageHandler(mockWebSocketClient);
		statusMessageHandler.onMessage(statusMsg);

		new Verifications() {
			{
				new OdeControlData(statusMsg);
				logger.info(controlData.toJson(false));
			}
		};
	}

	@Test
	public void testOnMessageException(@Mocked DdsStatusMessage statusMsg, @Mocked OdeControlData controlData,
			@Mocked final Logger logger) {
		Exception e = new Exception();
		new Expectations() {
			{
				new OdeControlData(statusMsg);
				result = e;
			}
		};

		StatusMessageHandler statusMessageHandler = new StatusMessageHandler(mockWebSocketClient);
		statusMessageHandler.onMessage(statusMsg);

		new Verifications() {
			{
				logger.error("Error handling ControlMessage. ", e);
			}
		};
	}

	@Test
	public void testOnClose(@Mocked DdsStatusMessage statusMsg, @Mocked OdeControlData controlData,
			@Mocked final Logger logger, @Mocked Session session, @Mocked CloseReason reason) {

		StatusMessageHandler statusMessageHandler = new StatusMessageHandler(mockWebSocketClient);
		statusMessageHandler.onClose(session, reason);

		new Verifications() {
			{
				new OdeControlData(StatusTag.CLOSED);
				controlData.setMessage(anyString);
				logger.info(controlData.toJson(false));
			}
		};
	}

	@Test
	public void testOnOpen(@Mocked DdsStatusMessage statusMsg, @Mocked OdeControlData controlData,
			@Mocked final Logger logger, @Mocked Session session, @Mocked CloseReason reason) {

		StatusMessageHandler statusMessageHandler = new StatusMessageHandler(mockWebSocketClient);
		statusMessageHandler.onOpen(session, null);

		new Verifications() {
			{
				new OdeControlData(StatusTag.OPENED);
				controlData.setMessage("WebSocket Connection to DDS Opened.");
				logger.info(controlData.toJson(false));
			}
		};
	}

	@Test
	public void testOnError(@Mocked DdsStatusMessage statusMsg, @Mocked OdeControlData controlData,
			@Mocked final Logger logger, @Mocked Session session, @Mocked CloseReason reason) {

		StatusMessageHandler statusMessageHandler = new StatusMessageHandler(mockWebSocketClient);
		statusMessageHandler.onError(session, new Throwable());

		new Verifications() {
			{
				new OdeControlData(StatusTag.ERROR);
				controlData.setMessage(anyString);
				logger.info(controlData.toJson(false));
			}
		};
	}

	@Test
	public void buildOdeMessage(@Mocked DdsStatusMessage message) {
		StatusMessageHandler statusMessageHandler = new StatusMessageHandler(mockWebSocketClient);
		OdeStatus status = (OdeStatus) statusMessageHandler.buildOdeMessage(message);

		assertEquals(status.getMessage(), message.toString());
	}

}
