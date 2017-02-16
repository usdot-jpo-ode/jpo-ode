package us.dot.its.jpo.ode.snmp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;

/**
 * This service class is used to receive TIM SNMP parameters, as well as the
 * encoded TIM payload
 */
public class TimManagerService {
    
    private Logger logger = LoggerFactory.getLogger(TimManagerService.class);

    private TimManagerService() {}
    
    public ResponseEvent createAndSend(SnmpProperties props, String payload) {
        
        SnmpConnection connect;
        try {
            connect = new SnmpConnection(props);
        } catch (IOException e) {
            logger.error("TIM SERVICE - Failed to create SNMP session: {}", e);
        }
        
        PDU pdu = createPdu(payload);
        
        connect.send(pdu)
        
        
    }
    
    private PDU createPdu(String payload) {
        
        PDU pdu = new PDU();
        
        return pdu;
        
    }
    
    

}
