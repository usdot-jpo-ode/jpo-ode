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

import lombok.extern.slf4j.Slf4j;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SnmpProtocol;
import us.dot.its.jpo.ode.rsu.RsuProperties;
import us.dot.its.jpo.ode.snmp.SnmpFourDot1Protocol;
import us.dot.its.jpo.ode.snmp.SnmpNTCIP1218Protocol;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;

import java.io.IOException;
import java.util.HashMap;

@RestController
@Slf4j
public class TimQueryController {
    
    private static final String ERRSTR = "error";

    private final RsuProperties rsuProperties;

    @Autowired
    public TimQueryController(RsuProperties rsuProperties) {
        this.rsuProperties = rsuProperties;
    }

    /**
     * Checks given RSU for all TIMs set
     *
     * @param jsonString Request body containing RSU info
     * @return list of occupied TIM slots on RSU
     */
    @CrossOrigin
    @PostMapping(value = "/tim/query")
    public synchronized ResponseEntity<String> bulkQuery(@RequestBody String jsonString) { // NOSONAR

        if (null == jsonString || jsonString.isEmpty()) {
            log.error("Empty request.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, "Empty request."));
        }

        RSU queryTarget = null;
        try {
            queryTarget = (RSU) JsonUtils.fromJson(jsonString, RSU.class);
        } catch (Exception e) {
            log.error("Convert JSON to RSU failed.", e);
        }
        if (queryTarget == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(JsonUtils.jsonKeyValue(ERRSTR, "Unrecognized protocol"));
        }

        TimTransmogrifier.updateRsuCreds(queryTarget, rsuProperties);

        SnmpSession snmpSession = null;
        try {
            snmpSession = new SnmpSession(queryTarget);
            snmpSession.startListen();
        } catch (IOException e) {
            log.error("Error creating SNMP session.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JsonUtils.jsonKeyValue(ERRSTR, "Failed to create SNMP session."));
        }

        PDU pdu0 = new ScopedPDU();
        pdu0.setType(PDU.GET);
        PDU pdu1 = new ScopedPDU();
        pdu1.setType(PDU.GET);

        SnmpProtocol snmpProtocol = queryTarget.getSnmpProtocol();

        if (snmpProtocol == null) {
            log.error("No SNMP protocol specified.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.jsonKeyValue(ERRSTR, "No SNMP protocol specified."));
        }

        switch (snmpProtocol) {
            case SnmpProtocol.FOURDOT1 -> {
                for (int i = 0; i < rsuProperties.getSrmSlots() - 50; i++) {
                    pdu0.add(SnmpFourDot1Protocol.getVbRsuSrmStatus(i));
                }

                for (int i = 50; i < rsuProperties.getSrmSlots(); i++) {
                    pdu1.add(SnmpFourDot1Protocol.getVbRsuSrmStatus(i));
                }
            }
            case SnmpProtocol.NTCIP1218 -> {
                for (int i = 0; i < rsuProperties.getSrmSlots() - 50; i++) {
                    pdu0.add(SnmpNTCIP1218Protocol.getVbRsuMsgRepeatStatus(i));
                }

                for (int i = 50; i < rsuProperties.getSrmSlots(); i++) {
                    pdu1.add(SnmpNTCIP1218Protocol.getVbRsuMsgRepeatStatus(i));
                }
            }
            default -> {
                log.error("Unsupported SNMP protocol: {}", snmpProtocol);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(JsonUtils.jsonKeyValue(ERRSTR, "Unsupported SNMP protocol: " + snmpProtocol));
            }
        }

        ResponseEvent response0 = null;
        ResponseEvent response1 = null;
        try {
            response0 = snmpSession.getSnmp().send(pdu0, snmpSession.getTarget());
            response1 = snmpSession.getSnmp().send(pdu1, snmpSession.getTarget());
        } catch (IOException e) {
            log.error("Error creating SNMP session.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JsonUtils.jsonKeyValue(ERRSTR, "Failed to create SNMP session."));
        }

        // Process response
        if (response0 == null || response0.getResponse() == null || response1 == null
                || response1.getResponse() == null) {
            log.error("RSU query failed, timeout.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(JsonUtils.jsonKeyValue(ERRSTR, "Timeout, no response from RSU."));
        }

        HashMap<String, Boolean> resultsMap = new HashMap<>();
        for (Object vbo : response0.getResponse().getVariableBindings().toArray()) {
            VariableBinding vb = (VariableBinding) vbo;
            if (vb.getVariable().toInt() == 1) {
                resultsMap.put(vb.getOid().toString().substring(21), true);
            }
        }

        for (Object vbo : response1.getResponse().getVariableBindings().toArray()) {
            VariableBinding vb = (VariableBinding) vbo;
            if (vb.getVariable().toInt() == 1) {
                resultsMap.put(vb.getOid().toString().substring(21), true);
            }
        }

        try {
            snmpSession.endSession();
        } catch (IOException e) {
            log.error("Error closing SNMP session.", e);
        }

        log.info("RSU query successful: {}", resultsMap.keySet());
        return ResponseEntity.status(HttpStatus.OK).body(JsonUtils.jsonKeyValue("indicies_set", resultsMap.keySet().toString()));
    }
}
