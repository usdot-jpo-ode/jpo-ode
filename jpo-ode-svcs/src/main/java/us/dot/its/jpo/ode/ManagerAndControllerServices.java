package us.dot.its.jpo.ode;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;

import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.j2735.pdm.PDM;
import us.dot.its.jpo.ode.snmp.PdmManagerService;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.snmp.TimManagerService;
import us.dot.its.jpo.ode.snmp.TimParameters;

public class ManagerAndControllerServices {
   private static Logger logger = LoggerFactory.getLogger(ManagerAndControllerServices.class);
   
   private ManagerAndControllerServices() {    
   }
   
   public static String log(boolean success, String msg, Throwable t) {
      if (success) {
         EventLogger.logger.info(msg);
         String myMsg = String.format("{success: true, message:\"%1$s\"}", msg);
         logger.info(myMsg);
         return myMsg;
      } else {
         if (Objects.nonNull(t)) {
            EventLogger.logger.error(msg, t);
            logger.error(msg, t);
         } else {
            EventLogger.logger.error(msg);
            logger.error(msg);
         }
         return "{success: false, message: \"" + msg + "\"}";
      }
   }
   
   public static ResponseEvent createAndSend(PDM params, SnmpProperties props) {

      if (null == params || null == props) {
          logger.error("PDM SERVICE - Received null object");
          return null;
      }
      // Initialize the SNMP session
      SnmpSession session = null;
      try {
          session = new SnmpSession(props);
      } catch (IOException e) {
          logger.error("PDM SERVICE - Failed to create SNMP session: {}", e);
          return null;
      }

      // Send the PDU
      ResponseEvent response = null;
      ScopedPDU pdu = PdmManagerService.createPDU(params);
      try {
          response = session.set(pdu, session.getSnmp(), session.getTransport(), session.getTarget());
      } catch (IOException | NullPointerException e) {
          logger.error("PDM SERVICE - Error while sending PDU: {}", e);
          return null;
      }
      return response;

  }
   
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
       ScopedPDU pdu = TimManagerService.createPDU(params);
       try {
           response = session.set(pdu, session.getSnmp(), session.getTransport(), session.getTarget());
       } catch (IOException | NullPointerException e) {
           logger.error("TIM SERVICE - Error while sending PDU: {}", e);
           return null;
       }
       return response;
       
   }
}
