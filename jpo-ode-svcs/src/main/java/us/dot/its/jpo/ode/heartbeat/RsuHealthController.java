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

import java.io.IOException;

import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.dot.its.jpo.ode.util.CodecUtils;

@RestController
public class RsuHealthController {

    private RsuHealthController() {
    }

    @GetMapping(value = "/rsuHeartbeat", produces = "application/json")
    @ResponseBody
    public static String heartBeat(@RequestHeader("Authorization") String auth, @RequestParam("ip") String ip, @RequestParam("oid") String oid) throws IOException {

        if (ip == null || oid == null) {
            throw new IllegalArgumentException("[ERROR] Endpoint received null argument.");
        }

        TransportMapping transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);

        String username = null;
        if (auth != null) {
          String[] auth2 = auth.split(" ");
          if (auth2[0].equals("Basic")) {
            String unpw = auth2[1];
            byte[] unpw2 = CodecUtils.fromBase64(unpw);
            String unpw3 = new String(unpw2);
            String[] unpw4 = unpw3.split(":");
            if (unpw4.length == 2) {
              username = unpw4[0];
              String password = unpw4[1];
              OctetString un = new OctetString(username);
              UsmUser usmUser = new UsmUser(
                  un, 
                  AuthMD5.ID, 
                  new OctetString(password),
                  null, 
                  null
              );
              usm.addUser(un, usmUser);
            }
          }
        }
        
        SecurityModels.getInstance().addSecurityModel(usm);
        transport.listen();

        return RsuSnmp.sendSnmpV3Request(ip, oid, snmp, username);
    }

}
