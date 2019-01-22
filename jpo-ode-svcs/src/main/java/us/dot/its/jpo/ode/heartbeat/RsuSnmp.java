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
package us.dot.its.jpo.ode.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

public class RsuSnmp {

    private static Logger logger = LoggerFactory.getLogger(RsuSnmp.class);

    private RsuSnmp() {
    }

    public static String sendSnmpV3Request(String ip, String oid, Snmp snmp, String username) {

        if (ip == null || oid == null || snmp == null) {
            logger.debug("Invalid SNMP request parameter");
            throw new IllegalArgumentException("Invalid SNMP request parameter");
        }
        
        // Setup snmp request
        Address targetAddress = GenericAddress.parse(ip + "/161");

        UserTarget target = new UserTarget();
        target.setAddress(targetAddress);
        target.setRetries(1);
        target.setTimeout(2000);
        target.setVersion(SnmpConstants.version3);
        if (username != null) {
            target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
            target.setSecurityName(new OctetString(username));
        }

        PDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);

        // Try to send the snmp request
        ResponseEvent responseEvent;
        try {
            responseEvent = snmp.send(pdu, target);
            snmp.close();
        } catch (Exception e) {
            responseEvent = null;
            logger.debug("SNMP4J library exception", e);
        }

        // Interpret snmp response
        String stringResponse;
        if (responseEvent == null) {
            logger.debug("SNMP connection error");
            stringResponse = "[ERROR] SNMP connection error";
        } else if (responseEvent.getResponse() == null) {
            logger.debug("Empty SNMP response");
            stringResponse = "[ERROR] Empty SNMP response";
        } else {
            stringResponse = responseEvent.getResponse().getVariableBindings().toString();
        }

        return stringResponse;
    }

}
