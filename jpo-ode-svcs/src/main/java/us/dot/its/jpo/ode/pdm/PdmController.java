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
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.j2735.pdm.J2735ProbeDataManagement;
import us.dot.its.jpo.ode.plugin.j2735.pdm.PDM;
import us.dot.its.jpo.ode.snmp.SnmpProperties;
import us.dot.its.jpo.ode.util.JsonUtils;

@Controller
public class PdmController {

    @ResponseBody
    @RequestMapping(value = "/pdm", method = RequestMethod.POST, produces = "application/json")
    public String pdmMessage(@RequestBody String jsonString) {
        if (null == jsonString) {
            String msg = "PDM CONTROLLER - Endpoint received null request";
            ManagerAndControllerServices.log(false, msg, null);
            throw new PdmException(msg);
        }

        J2735ProbeDataManagement pdm = (J2735ProbeDataManagement) JsonUtils.fromJson(jsonString,
                J2735ProbeDataManagement.class);
        
        HashMap<String, String> responseList = new HashMap<>();
        for (RSU curRsu : pdm.getRsuList()) {

            ResponseEvent response = null;
            try {
                response = sendToRsu(curRsu, pdm.getPdm());

                if (null == response || null == response.getResponse()) {
                    responseList.put(curRsu.getrsuTarget(),
                          ManagerAndControllerServices.log(false, "PDM CONTROLLER - No response from RSU IP=" + curRsu.getrsuTarget(), null));
                } else if (0 == response.getResponse().getErrorStatus()) {
                    responseList.put(curRsu.getrsuTarget(), ManagerAndControllerServices.log(true, "PDM CONTROLLER - SNMP deposit successful", null));
                } else {
                    responseList.put(curRsu.getrsuTarget(),
                          ManagerAndControllerServices.log(false,
                                    "PDM CONTROLLER - Error, SNMP deposit failed, error code="
                                            + response.getResponse().getErrorStatus() + "("
                                            + response.getResponse().getErrorStatusText() + ")",
                                    null));
                }

            } catch (ParseException e) {
                responseList.put(curRsu.getrsuTarget(),
                      ManagerAndControllerServices.log(false, "PDM CONTROLLER - Exception while sending message to RSU", e));
            }
        }

        return responseList.toString();
    }

    private ResponseEvent sendToRsu(RSU rsu, PDM params) throws ParseException {
        Address addr = GenericAddress.parse(rsu.getrsuTarget() + "/161");

        // Populate the SnmpProperties object with SNMP preferences
        SnmpProperties testProps = new SnmpProperties(addr, rsu.getrsuUsername(), rsu.getrsuPassword(), rsu.getrsuRetries(),
                rsu.getrsuTimeout());

        return ManagerAndControllerServices.createAndSend(params, testProps);
    }
}
