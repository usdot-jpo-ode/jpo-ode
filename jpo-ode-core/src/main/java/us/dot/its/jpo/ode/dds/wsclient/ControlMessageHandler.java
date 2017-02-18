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
package us.dot.its.jpo.ode.dds.wsclient;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.ControlMessage;
import us.dot.its.jpo.ode.model.ControlTag;
import us.dot.its.jpo.ode.model.OdeControlData;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

public class ControlMessageHandler implements WebSocketMessageHandler<ControlMessage> {

   private static final Logger logger = LoggerFactory
         .getLogger(ControlMessageHandler.class);
   
   @Override
   public void onMessage(ControlMessage controlMessage) {
      try {
         if (controlMessage != null) {
            OdeControlData controlData = new OdeControlData(controlMessage);
            handleControlMessage(controlData);
         }
      } catch (Exception e) {
         logger.error("Error handling ControlMessage. ", e);
      } finally {
      }
   }
   
   private void handleControlMessage(OdeControlData controlData) {
      logger.info(controlData.toJson(false));
   }
   
   @Override
   public void onOpen(Session session, EndpointConfig config) {
      OdeControlData controlData = new OdeControlData(ControlTag.OPENED);
      controlData.setMessage("WebSocket Connection to DDS Opened.");

      handleControlMessage(controlData);
   }
   
   @Override
   public void onClose(Session session, CloseReason reason) {
      OdeControlData controlData = new OdeControlData(ControlTag.CLOSED);
      controlData.setMessage("WebSocket Connection to DDS Closed. Reason: " + reason.getReasonPhrase());

      handleControlMessage(controlData);
   }

   @Override
   public void onError(Session session, Throwable t) {
      OdeControlData controlData = new OdeControlData(ControlTag.ERROR);
      controlData.setMessage("WebSocket Connection to DDS Errored. Message: " + t.getMessage());

      handleControlMessage(controlData);
   }
   
}
