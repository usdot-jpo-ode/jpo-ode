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
package us.dot.its.jpo.ode.traveler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import us.dot.its.jpo.ode.plugin.SnmpProtocol;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.rsu.RsuProperties;
import us.dot.its.jpo.ode.snmp.SnmpFourDot1Protocol;
import us.dot.its.jpo.ode.snmp.SnmpNTCIP1218Protocol;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;

@RestController
public class TimDeleteController {

   private static final Logger logger = LoggerFactory.getLogger(TimDeleteController.class);

   private static final String ERRSTR = "error";

   private RsuProperties rsuProperties;
   
   @Autowired
   public TimDeleteController(RsuProperties rsuProperties) {
      super();
      this.rsuProperties = rsuProperties;
   }

   @CrossOrigin
   @DeleteMapping(value = "/tim")
   public ResponseEntity<String> deleteTim(@RequestBody String jsonString,
         @RequestParam(value = "index", required = true) Integer index) { // NOSONAR

      if (null == jsonString) {
        logger.error("Empty request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, "Empty request"));
      }

      RSU queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);
      TimTransmogrifier.updateRsuCreds(queryTarget, rsuProperties);

      logger.info("TIM delete call, RSU info {}", queryTarget);

      SnmpSession ss = null;
      try {
         ss = new SnmpSession(queryTarget);
      } catch (IOException e) {
         logger.error("Error creating TIM delete SNMP session", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(JsonUtils.jsonKeyValue(ERRSTR, e.getMessage()));
      } catch (NullPointerException e) {
         logger.error("TIM query error, malformed JSON", e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, "Malformed JSON"));
      }

      SnmpProtocol snmpProtocol = queryTarget.getSnmpProtocol();
      
      if (snmpProtocol == null) {
         logger.error("No SNMP protocol specified.");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(JsonUtils.jsonKeyValue(ERRSTR, "No SNMP protocol specified."));
      }

      PDU pdu = null;
      if (snmpProtocol.equals(SnmpProtocol.FOURDOT1)) {
         pdu = new ScopedPDU();
         pdu.add(SnmpFourDot1Protocol.getVbRsuSrmStatus(index, 6));
         pdu.setType(PDU.SET);
      }
      else if (snmpProtocol.equals(SnmpProtocol.NTCIP1218)) {
         pdu = new ScopedPDU();
         pdu.add(SnmpNTCIP1218Protocol.getVbRsuMsgRepeatStatus(index, 6));
         pdu.setType(PDU.SET);
      }
      else {
         logger.error("Unsupported SNMP protocol");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(JsonUtils.jsonKeyValue(ERRSTR, "Unsupported SNMP protocol"));
      }
      
      ResponseEvent rsuResponse = null;
      try {
         rsuResponse = ss.set(pdu, ss.getSnmp(), ss.getTarget(), false);
      } catch (IOException e) {
         logger.error("Error sending TIM query PDU to RSU", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(JsonUtils.jsonKeyValue(ERRSTR, e.getMessage()));
      }

      // Provide error codes/text returned from RSU and our interpretation of them
      HttpStatus httpResponseReturnCode = null;
      String httpResponseBodyMessage = "";
      String rsuIpAddress = queryTarget.getRsuTarget();
      if (null == rsuResponse || null == rsuResponse.getResponse()) {
         // Timeout
         httpResponseReturnCode = HttpStatus.REQUEST_TIMEOUT;
         String timeoutMessage = "Timeout. No response from RSU.";
         httpResponseBodyMessage = JsonUtils.jsonKeyValue(ERRSTR, timeoutMessage);
         logger.error("Failed to delete message at index {} for RSU {}: {}", index, rsuIpAddress, timeoutMessage);
      } else if (rsuResponse.getResponse().getErrorStatus() == 0) {
         // Success
         httpResponseReturnCode = HttpStatus.OK;
         httpResponseBodyMessage = JsonUtils.jsonKeyValue("deleted_msg", Integer.toString(index));
         logger.info("Successfully deleted message at index {} for RSU {}", index, rsuIpAddress);
      } else {
         // Error
         httpResponseReturnCode = HttpStatus.BAD_REQUEST;
         int errorCodeReturnedByRSU = rsuResponse.getResponse().getErrorStatus();
         String errorTextReturnedByRSU = rsuResponse.getResponse().getErrorStatusText();
         String givenReason = "Error code " + Integer.toString(errorCodeReturnedByRSU) + ": " + errorTextReturnedByRSU;
         String interpretation = interpretErrorCode(errorCodeReturnedByRSU, index);
         httpResponseBodyMessage = JsonUtils.jsonKeyValue(ERRSTR, givenReason + " => Interpretation: " + interpretation);
         logger.error("Failed to delete message at index {} for RSU {} due to error: {} => Interpretation: {}", index, rsuIpAddress, givenReason, interpretation);
      }

      return ResponseEntity.status(httpResponseReturnCode).body(httpResponseBodyMessage);
   }

   private String interpretErrorCode(int errorCodeReturnedByRSU, int index) {
      if (errorCodeReturnedByRSU == 12) {
         return "Message previously deleted or doesn't exist at index " + Integer.toString(index);
      } else if (errorCodeReturnedByRSU == 10) {
         return "Possible SNMP protocol mismatch, check RSU configuration";
      } else {
         return "Unknown error";
      }
   }

}
