package us.dot.its.jpo.ode.snmp;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

/**
 * This utility/service class is used to receive TIM SNMP parameters, as well as the
 * encoded TIM payload, and then send a request to the RSU.
 */
public class TimManagerService {
    
    private static final Logger logger = LoggerFactory.getLogger(TimManagerService.class);
    
    private TimManagerService() {}
    
    /**
     * Create an SNMP session given the values in 
     * @param tim - The TIM parameters (payload, channel, mode, etc)
     * @param props - The SNMP properties (ip, username, password, etc)
     * @return ResponseEvent
     */
    public static ResponseEvent createAndSend(TimParameters params, SnmpProperties props) {
        
        if (null == params || null == props) {
            logger.error("TIM SERVICE - Received null object");
            return null;
        }
        
        // Initialize the SNMP session
        SnmpSession session = null;
        try {
            session = new SnmpSession(props);
        } catch (IOException e) {
            logger.error("TIM SERVICE - Failed to create SNMP session: {}", e);
            return null;
        }
        
        // Send the PDU
        ResponseEvent response = null;
        ScopedPDU pdu = createPDU(params);
        try {
            response = session.set(pdu, session.getSnmp(), session.getTransport(), session.getTarget());
        } catch (IOException | NullPointerException e) {
            logger.error("TIM SERVICE - Error while sending PDU: {}", e);
            return null;
        }
        
        return response;
        
    }
    
    /**
     * Assembles the various RSU elements of a TimParameters object into a usable PDU.
     * @param params - TimParameters POJO that stores status, channel, payload, etc.
     * @return PDU
     */
    public static ScopedPDU createPDU(TimParameters params) {
        
        if (params == null) {
            return null;
        }
        
        //////////////////////////////
        // - OID examples         - //
        //////////////////////////////
        // rsuSRMStatus.3 = 4 
        //     --> 1.4.1.11.3 = 4
        // rsuSRMTxChannel.3 = 3 
        //     --> 1.4.1.5.3 = 178
        // rsuSRMTxMode.3 = 1    
        //     --> 1.4.1.4.3 = 1
        // rsuSRMPsid.3 x "8300" 
        //     --> 1.4.1.2.3 x "8300"
        // rsuSRMDsrcMsgId.3 = 31
        //      --> 1.4.1.3.3 = 31
        // rsuSRMTxInterval.3 = 1
        //      --> 1.4.1.6.3 = 1
        // rsuSRMDeliveryStart.3 x "010114111530"
        //      --> 1.4.1.7.3 = "010114111530"
        // rsuSRMDeliveryStop.3 x "010114130000"
        //      --> 1.4.1.8.3 = "010114130000"
        // rsuSRMPayload.3 x "0EFF82445566778899000000AABBCCDDEEFF00E00EA0C12A00"
        //      --> 1.4.1.9.3 = "0EFF82445566778899000000AABBCCDDEEFF00E00EA0C12A00"
        // rsuSRMEnable.3 = 1
        //      --> 1.4.1.10.3 = 1
        //////////////////////////////
        
        VariableBinding rsuSRMPsid = new VariableBinding(new OID("1.0.15628.4.1.4.1.2.3"), new OctetString(DatatypeConverter.parseHexBinary(params.rsuSRMPsid)));
        VariableBinding rsuSRMDsrcMsgId = new VariableBinding(new OID("1.0.15628.4.1.4.1.3.3"), new Integer32(params.rsuSRMDsrcMsgId));
        VariableBinding rsuSRMTxMode = new VariableBinding(new OID("1.0.15628.4.1.4.1.4.3"), new Integer32(params.rsuSRMTxMode));
        VariableBinding rsuSRMTxChannel = new VariableBinding(new OID("1.0.15628.4.1.4.1.5.3"), new Integer32(params.rsuSRMTxChannel));
        VariableBinding rsuSRMTxInterval = new VariableBinding(new OID("1.0.15628.4.1.4.1.6.3"), new Integer32(params.rsuSRMTxInterval));
        VariableBinding rsuSRMDeliveryStart = new VariableBinding(new OID("1.0.15628.4.1.4.1.7.3"), new OctetString(DatatypeConverter.parseHexBinary(params.rsuSRMDeliveryStart)));
        VariableBinding rsuSRMDeliveryStop = new VariableBinding(new OID("1.0.15628.4.1.4.1.8.3"), new OctetString(DatatypeConverter.parseHexBinary(params.rsuSRMDeliveryStop)));
        VariableBinding rsuSRMPayload = new VariableBinding(new OID("1.0.15628.4.1.4.1.9.3"), new OctetString(DatatypeConverter.parseHexBinary(params.rsuSRMPayload)));
        VariableBinding rsuSRMEnable = new VariableBinding(new OID("1.0.15628.4.1.4.1.10.3"), new Integer32(params.rsuSRMEnable));
        VariableBinding rsuSRMStatus = new VariableBinding(new OID("1.0.15628.4.1.4.1.11.3"), new Integer32(params.rsuSRMStatus));
        
        ScopedPDU pdu = new ScopedPDU();
        pdu.add(rsuSRMPsid);
        pdu.add(rsuSRMDsrcMsgId);
        pdu.add(rsuSRMTxMode);
        pdu.add(rsuSRMTxChannel);
        pdu.add(rsuSRMTxInterval);
        pdu.add(rsuSRMDeliveryStart);
        pdu.add(rsuSRMDeliveryStop);
        pdu.add(rsuSRMPayload);
        pdu.add(rsuSRMEnable);
        pdu.add(rsuSRMStatus);
        pdu.setType(PDU.SET);
        
        return pdu;
    }
}
