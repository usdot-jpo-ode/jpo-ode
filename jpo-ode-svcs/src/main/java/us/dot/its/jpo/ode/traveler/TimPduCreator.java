package us.dot.its.jpo.ode.traveler;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import us.dot.its.jpo.ode.snmp.SNMP;


/**
 * This utility/service class is used to receive TIM SNMP parameters, as well as the
 * encoded TIM payload, and then send a request to the RSU.
 */
public class TimPduCreator {
   
   private static final Logger logger = LoggerFactory.getLogger(TimPduCreator.class);
    
    public static class TimPduCreatorException extends Exception {
      private static final long serialVersionUID = 1L;

      public TimPduCreatorException(String string, Exception e) {
         super(string, e);
      }

   }

   private TimPduCreator() {
       throw new UnsupportedOperationException();
    }
    
   /**
    * Assembles the various RSU elements of a TimParameters object into a usable
    * PDU.
    * 
    * @param index
    *           Storage index on the RSU
    * @param params
    *           TimParameters POJO that stores status, channel, payload, etc.
    * @return PDU
    * @throws TimPduCreatorException
    */
    public static ScopedPDU createPDU(SNMP snmp, String payload, int index) throws TimPduCreatorException {
      try {  
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
        
        VariableBinding rsuSRMPsid = new VariableBinding(new OID("1.0.15628.4.1.4.1.2.".concat(Integer.toString(index))), new OctetString(DatatypeConverter.parseHexBinary(snmp.getRsuid())));
        VariableBinding rsuSRMDsrcMsgId = new VariableBinding(new OID("1.0.15628.4.1.4.1.3.".concat(Integer.toString(index))), new Integer32(snmp.getMsgid()));
        VariableBinding rsuSRMTxMode = new VariableBinding(new OID("1.0.15628.4.1.4.1.4.".concat(Integer.toString(index))), new Integer32(snmp.getMode()));
        VariableBinding rsuSRMTxChannel = new VariableBinding(new OID("1.0.15628.4.1.4.1.5.".concat(Integer.toString(index))), new Integer32(snmp.getChannel()));
        VariableBinding rsuSRMTxInterval = new VariableBinding(new OID("1.0.15628.4.1.4.1.6.".concat(Integer.toString(index))), new Integer32(snmp.getInterval()));
        VariableBinding rsuSRMDeliveryStart = new VariableBinding(new OID("1.0.15628.4.1.4.1.7.".concat(Integer.toString(index))), new OctetString(DatatypeConverter.parseHexBinary(SNMP.snmpTimestampFromIso(snmp.getDeliverystart()))));
        VariableBinding rsuSRMDeliveryStop = new VariableBinding(new OID("1.0.15628.4.1.4.1.8.".concat(Integer.toString(index))), new OctetString(DatatypeConverter.parseHexBinary(SNMP.snmpTimestampFromIso(snmp.getDeliverystop()))));
        VariableBinding rsuSRMPayload = new VariableBinding(new OID("1.0.15628.4.1.4.1.9.".concat(Integer.toString(index))), new OctetString(DatatypeConverter.parseHexBinary(payload)));
        VariableBinding rsuSRMEnable = new VariableBinding(new OID("1.0.15628.4.1.4.1.10.".concat(Integer.toString(index))), new Integer32(snmp.getEnable()));
        VariableBinding rsuSRMStatus = new VariableBinding(new OID("1.0.15628.4.1.4.1.11.".concat(Integer.toString(index))), new Integer32(snmp.getStatus()));
        
        logger.info("PAYLOAD LENGTH: {}", new OctetString(DatatypeConverter.parseHexBinary(payload)).getBERPayloadLength());
        
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
      } catch (Exception e) {
         logger.error("Exception creating PDU.", e);
         throw new TimPduCreatorException("Error creating PDU", e);
      }
    }
}
