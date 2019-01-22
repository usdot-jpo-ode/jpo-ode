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

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeControlData;
import us.dot.its.jpo.ode.model.OdeMessage;
import us.dot.its.jpo.ode.model.OdeStatus;
import us.dot.its.jpo.ode.model.OdeStatus.Code;
import us.dot.its.jpo.ode.model.StatusTag;
import us.dot.its.jpo.ode.wrapper.AbstractWebsocketMessageHandler;
import us.dot.its.jpo.ode.wrapper.WebSocketClient;

public class StatusMessageHandler extends AbstractWebsocketMessageHandler<DdsStatusMessage> {

    private static final Logger logger = LoggerFactory.getLogger(StatusMessageHandler.class);

    public StatusMessageHandler(WebSocketClient client) {
        super(client);
    }

    @Override
    public void onMessage(DdsStatusMessage statusMsg) {
        super.onMessage(statusMsg);

        try {
            if (statusMsg != null) {
                OdeControlData controlData = new OdeControlData(statusMsg);
                handleControlMessage(controlData);
            }
        } catch (Exception e) {
            logger.error("Error handling ControlMessage. ", e);
        }
    }

    private void handleControlMessage(OdeControlData controlData) {
        String infoMsg = controlData.toJson(false);
        logger.info(infoMsg);
        EventLogger.logger.info(infoMsg);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.getClient().onOpen(session);
        OdeControlData controlData = new OdeControlData(StatusTag.OPENED);
        controlData.setMessage("WebSocket Connection to DDS Opened.");

        handleControlMessage(controlData);
    }

    @Override
    public void onClose(Session session, CloseReason reason) {
        this.getClient().onClose(reason);
        OdeControlData controlData = new OdeControlData(StatusTag.CLOSED);
        controlData.setMessage("WebSocket Connection to DDS Closed. Reason: " + reason.getReasonPhrase());

        handleControlMessage(controlData);
    }

    @Override
    public void onError(Session session, Throwable t) {
        this.getClient().onError(t);
        OdeControlData controlData = new OdeControlData(StatusTag.ERROR);
        controlData.setMessage("WebSocket Connection to DDS Errored. Message: " + t.getMessage());

        handleControlMessage(controlData);
    }

    @Override
    public OdeMessage buildOdeMessage(DdsStatusMessage message) {
        OdeStatus status = new OdeStatus();

        status.setCode(Code.SUCCESS);
        status.setMessage(message.toString());

        return status;
    }

}
