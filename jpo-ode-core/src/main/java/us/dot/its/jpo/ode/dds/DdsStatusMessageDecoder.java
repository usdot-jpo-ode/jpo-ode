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

import javax.websocket.DecodeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.model.StatusTag;
import us.dot.its.jpo.ode.util.JsonUtils;

public class DdsStatusMessageDecoder extends DdsDecoder {

   private static final Logger logger = LoggerFactory
         .getLogger(DdsStatusMessageDecoder.class);
   
   private static final int MSG_COMPONENT_TAG_INDEX = 0;
   private static final int MSG_COMPONENT_VALUE_INDEX = 1;
   private static final int RECORD_COUNT_VALUE_INDEX = 1;

   protected static StatusTag getResponseTag(String tagName) {
      StatusTag[] tags = StatusTag.values();

      for (StatusTag tag : tags) {
         if (tagName.equals(tag.name())) {
            return tag;
         }
      }

      return null;
   }

   @Override
   public DdsMessage decode(String message) throws DecodeException {
      DdsStatusMessage statusMsg = null;
      try {
         String[] msgComponents = parseFullMsg(message);
         StatusTag tag = getResponseTag(msgComponents[MSG_COMPONENT_TAG_INDEX]);
         if (tag != null) {
            statusMsg = new DdsStatusMessage().setTag(tag);
            switch(tag) {
            case CONNECTED: {
               String connectionDetails = msgComponents[MSG_COMPONENT_VALUE_INDEX];
               statusMsg.setConnectionDetails(connectionDetails);
            }
            break;
            case START: {
               String jsonMessage = msgComponents[MSG_COMPONENT_VALUE_INDEX];
               try {
                  ObjectNode rootNode = JsonUtils.toObjectNode(jsonMessage);
                  
                  statusMsg
                     .setDialog(DdsRequest.Dialog.getById(rootNode.get("dialogID").asInt()))
                     .setEncoding(rootNode.get("resultEncoding").textValue());
                        
               } catch (Exception e) {
                  logger.error("Error processing START tag", e);
               }
            }
            break;
            case STOP: {
               String recordCount = msgComponents[MSG_COMPONENT_VALUE_INDEX];
               String[] rcArray = patseRecordCount(recordCount);
               if (rcArray.length == 2) {
                  try {
                     if (statusMsg != null) {
                        statusMsg.setRecordCount(Integer.valueOf(rcArray[RECORD_COUNT_VALUE_INDEX]));
                     }
                  } catch (Exception e) {
                     logger.error("Error processing STOP tag", e);
                  }
               } else {
                  logger.error("Invalid format for recordCount. "
                        + "Expecting \"recordCount=n\" but received \"{}\"", 
                        recordCount);
               }
            }
            break;
            
            case DEPOSITED: {
               String depositCount = msgComponents[MSG_COMPONENT_VALUE_INDEX];
               try {
                  if (statusMsg != null) {
                     statusMsg.setRecordCount(Integer.valueOf(depositCount));
                  }
               } catch (Exception e) {
                  logger.error("Error processing DEPOSITED tag", e);
               }
            }
            break;
            
            case ERROR: {
               logger.error("Received Error message from DDS: {}", message);
            }
            break;
            
            default:
               logger.error("Received {} message: {}", tag, message);
            }
         }
      } catch (Exception e) {
         logger.error("Error decoding ", e);
      } finally {
      }

      return statusMsg;
   }

   public String[] patseRecordCount(String recordCount) {
      return recordCount.split("\\s*=\\s*");
   }

   public String[] parseFullMsg(String message) {
      return message.split("\\s*:\\s*", 2);
   }

   @Override
   public boolean willDecode(String message) {
      return getResponseTag(parseFullMsg(message)[MSG_COMPONENT_TAG_INDEX]) == StatusTag.START;
   }
}
