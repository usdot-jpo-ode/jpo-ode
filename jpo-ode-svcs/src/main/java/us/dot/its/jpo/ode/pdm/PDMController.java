package us.dot.its.jpo.ode.pdm;

import java.text.ParseException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagement.PdmParameters;
import us.dot.its.jpo.ode.plugin.RoadSignUnit.RSU;
import us.dot.its.jpo.ode.snmp.PdmManagerService;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.traveler.TimMessageException;

@Controller
public class PDMController {
   private static Logger logger = LoggerFactory.getLogger(PDMController.class);

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

   private ResponseEvent sendToRSU(RSU rsu, PdmParameters params, String payload) throws ParseException {
      Address addr = GenericAddress.parse(rsu.getTarget() + "/161");

      // Populate the SnmpProperties object with SNMP preferences
      SnmpProperties testProps = new SnmpProperties(addr, rsu.getUsername(), rsu.getPassword(), rsu.getRetries(), rsu.getTimeout());

      return PdmManagerService.createAndSend(params,testProps);
   }
}
