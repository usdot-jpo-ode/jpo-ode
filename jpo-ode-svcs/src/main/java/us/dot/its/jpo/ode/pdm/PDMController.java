package us.dot.its.jpo.ode.pdm;

import java.text.ParseException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsDepositor;
import us.dot.its.jpo.ode.dds.DdsStatusMessage;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData.SNMP;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.snmp.TimManagerService;
import us.dot.its.jpo.ode.snmp.TimParameters;
import us.dot.its.jpo.ode.traveler.TimMessageException;
import us.dot.its.jpo.ode.traveler.TravelerMessageController;

@Controller
public class PDMController {
   private static Logger logger = LoggerFactory.getLogger(PDMController.class);

   private OdeProperties odeProperties;
   private String myString;

   @ResponseBody
   @RequestMapping(value = "/pdm", method = RequestMethod.POST, produces = "application/json")
   public String pdmMessage(@RequestBody String jsonString) {
      if (jsonString == null) {
         String msg = "TIM CONTROLLER - Endpoint received null request";
         log(false, msg, null);
         throw new TimMessageException(msg);
      }

      return null;
   }

   private String log(boolean success, String msg, Throwable t) {
      if (success) {
         EventLogger.logger.info(msg);
         msg = String.format("{success: true, message:\"%1$s\"}", msg);
         logger.info(msg);
         return msg;
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

   private ResponseEvent sendToRSU(RSU rsu, SNMP snmp, String payload) throws ParseException {
      Address addr = GenericAddress.parse(rsu.target + "/161");

      // Populate the SnmpProperties object with SNMP preferences
      SnmpProperties testProps = new SnmpProperties(addr, rsu.username, rsu.password, rsu.retries, rsu.timeout);

      // Populate the TimParameters object with OID values

      // Send the request out
      // ResponseEvent response = TimManagerService.createAndSend(testParams,
      // testProps);

      return null;
   }
}
