package us.dot.its.jpo.ode.pdm;

import java.text.ParseException;
import java.util.HashMap;

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import us.dot.its.jpo.ode.ManagerAndControllerServices;
import us.dot.its.jpo.ode.http.BadRequestException;
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.util.JsonUtils;

@Controller
public class PdmController {

    @ResponseBody
    @RequestMapping(value = "/pdm", method = RequestMethod.POST, produces = "application/json")
    public String pdmMessage(@RequestBody String jsonString) {
        if (null == jsonString) {
            String msg = "Endpoint received null request";
            ManagerAndControllerServices.log(false, msg, null);
            throw new BadRequestException(msg);
        }

        J2735PdmRequest pdm = (J2735PdmRequest) JsonUtils.fromJson(jsonString,
                J2735PdmRequest.class);
        
        HashMap<String, String> responseList = new HashMap<>();
        for (RSU curRsu : pdm.getRsuList()) {

            ResponseEvent response = null;
            try {
                response = sendToRsu(curRsu, pdm.getPdm());

                if (null == response || null == response.getResponse()) {
                    responseList.put(curRsu.getRsuTarget(),
                          ManagerAndControllerServices.log(false, "No response from RSU IP=" + curRsu.getRsuTarget(), null));
                } else if (0 == response.getResponse().getErrorStatus()) {
                    responseList.put(curRsu.getRsuTarget(), 
                          ManagerAndControllerServices.log(true, "SNMP deposit successful: " + response.getResponse(), null));
                } else {
                    responseList.put(curRsu.getRsuTarget(),
                          ManagerAndControllerServices.log(false,
                                    "Error, SNMP deposit failed, error code="
                                            + response.getResponse().getErrorStatus() + "("
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

    private ResponseEvent sendToRsu(RSU rsu, J2735ProbeDataManagment params) throws ParseException {
        Address addr = GenericAddress.parse(rsu.getRsuTarget() + "/161");

        // Populate the SnmpProperties object with SNMP preferences
        SnmpProperties testProps = new SnmpProperties(addr, rsu.getRsuUsername(), rsu.getRsuPassword(), rsu.getRsuRetries(),
                rsu.getRsuTimeout());

        return ManagerAndControllerServices.createAndSend(params, testProps);
    }
}
