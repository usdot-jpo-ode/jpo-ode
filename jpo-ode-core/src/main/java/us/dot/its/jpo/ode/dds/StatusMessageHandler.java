/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
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
