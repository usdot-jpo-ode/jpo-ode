package us.dot.its.jpo.ode.pdm;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import us.dot.its.jpo.ode.ManagerAndControllerServices;
import us.dot.its.jpo.ode.http.BadRequestException;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData;
import us.dot.its.jpo.ode.snmp.PdmManagerService;
import us.dot.its.jpo.ode.snmp.SnmpSession;
import us.dot.its.jpo.ode.util.JsonUtils;

@Controller
public class PdmController {

   private static Logger logger = LoggerFactory.getLogger(PdmController.class);

   @ResponseBody
   @RequestMapping(value = "/pdm", method = RequestMethod.POST, produces = "application/json")
   public String pdmMessage(@RequestBody String jsonString) {
      if (null == jsonString) {
         String msg = "Endpoint received null request";
         ManagerAndControllerServices.log(false, msg, null);
         throw new BadRequestException(msg);
      }

      J2735PdmRequest pdmRequest = null;
      try {
         pdmRequest = (J2735PdmRequest) JsonUtils.fromJson(jsonString, J2735TravelerInputData.class);

         logger.debug("J2735PdmRequest: {}", pdmRequest.toJson(true));

      } catch (Exception e) {
         ManagerAndControllerServices.log(false, "Error Deserializing TravelerInputData", e);
         throw new BadRequestException(e);
      }

      HashMap<String, String> responseList = new HashMap<>();
      for (RSU curRsu : pdmRequest.getRsuList()) {

         ResponseEvent response = null;
         try {
            response = createAndSend(pdmRequest.getPdm(), curRsu);

            if (null == response || null == response.getResponse()) {
               responseList.put(curRsu.getRsuTarget(),
                     ManagerAndControllerServices.log(false, "No response from RSU IP=" + curRsu.getRsuTarget(), null));
            } else if (0 == response.getResponse().getErrorStatus()) {
               responseList.put(curRsu.getRsuTarget(), ManagerAndControllerServices.log(true,
                     "SNMP deposit successful: " + response.getResponse(), null));
            } else {
               responseList.put(curRsu.getRsuTarget(),
                     ManagerAndControllerServices.log(false,
                           "Error, SNMP deposit failed, error code=" + response.getResponse().getErrorStatus() + "("
                                 + response.getResponse().getErrorStatusText() + ")",
                           null));
            }

         } catch (Exception e) {
            responseList.put(curRsu.getRsuTarget(),
                  ManagerAndControllerServices.log(false, "Exception while sending message to RSU", e));
         }
      }

      return responseList.toString();
   }

   public static ResponseEvent createAndSend(J2735ProbeDataManagment pdm, RSU rsu) {
      SnmpSession session = null;
      if (pdm != null)
         session = ManagerAndControllerServices.createSnmpSession(rsu);

      if (session == null)
         return null;

      // Send the PDU
      ResponseEvent response = null;
      ScopedPDU pdu = PdmManagerService.createPDU(pdm);
      try {
         response = session.set(pdu, session.getSnmp(), session.getTransport(), session.getTarget());
      } catch (IOException | NullPointerException e) {
         logger.error("PDM SERVICE - Error while sending PDU: {}", e);
         return null;
      }
      return response;
   }

}
