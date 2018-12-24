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
      logger.debug("Received from DDS: {}", message);
      DdsStatusMessage statusMsg = null;
      try {
         String[] msgComponents = parseFullMsg(message);
         StatusTag tag = getResponseTag(msgComponents[MSG_COMPONENT_TAG_INDEX]);
         if (tag != null) {
            statusMsg = new DdsStatusMessage().setTag(tag);
            switch(tag) {
            case CONNECTED:
            	evaluateConnected(statusMsg, msgComponents);
            	break;
            case START:
            	evaluateStart(statusMsg, msgComponents);
            	break;
            case STOP:
            	evaluateStop(statusMsg, msgComponents);
            	break;
            case DEPOSITED:
            	evaluateDeposited(statusMsg, msgComponents);
            	break;   
            case ERROR:
               logger.error("Received Error message from DDS: {}", message);
               break;
            default:
               logger.error("Received {} message: {}", tag, message);
            }
         }
      } catch (Exception e) {
         logger.error("Error decoding ", e);
      }

      return statusMsg;
   }
   
   private void evaluateConnected(DdsStatusMessage statusMsg, String[] msgComponents){
       String connectionDetails = msgComponents[MSG_COMPONENT_VALUE_INDEX];
       statusMsg.setConnectionDetails(connectionDetails);
   }
   
   private void evaluateStart(DdsStatusMessage statusMsg, String[] msgComponents){
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
   
   private void evaluateStop(DdsStatusMessage statusMsg, String[] msgComponents){
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
   
   private void evaluateDeposited(DdsStatusMessage statusMsg, String[] msgComponents){
	   String depositCount = msgComponents[MSG_COMPONENT_VALUE_INDEX];
       try {
          if (statusMsg != null) {
             statusMsg.setRecordCount(Integer.valueOf(depositCount));
          }
       } catch (Exception e) {
          logger.error("Error processing DEPOSITED tag", e);
       }
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
