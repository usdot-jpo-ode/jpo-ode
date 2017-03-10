package us.dot.its.jpo.ode.heartbeat;

import java.io.IOException;

import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RsuHealthController {

    private RsuHealthController() {
    }

    @RequestMapping(value = "/rsuHeartbeat", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public static String heartBeat(@RequestParam("ip") String ip, @RequestParam("oid") String oid) throws IOException {

        if (ip == null || oid == null) {
            throw new IllegalArgumentException("[ERROR] Endpoint received null argument.");
        }

        TransportMapping transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        transport.listen();

        return RsuSnmp.sendSnmpV3Request(ip, oid, snmp);
    }

}
