package us.dot.its.jpo.ode.coder;

import org.json.JSONObject;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeTimPayload;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeTimDataCreatorHelper {

   private OdeTimDataCreatorHelper() {
   }

   public static JSONObject createOdeTimData(JSONObject timData) throws XmlUtilsException { 

      JSONObject metadata = timData.getJSONObject(AppContext.METADATA_STRING);
      metadata.put("payloadType", OdeTimPayload.class.getName());
      metadata.remove("encodings");
      
      JSONObject payload = timData.getJSONObject(AppContext.PAYLOAD_STRING);
      payload.put(AppContext.DATA_TYPE_STRING, "TravelerInformation");
      // Do other TIM specific mods before returning the data 
      // TODO FIX THIS
//      JSONObject tim = TravelerInformationFromAsnToHumanConverter.genericTim(
//         payload.getJSONObject(AppContext.DATA_STRING)
//            .getJSONObject("MessageFrame")
//            .getJSONObject("value")
//            .getJSONObject("TravelerInformation"));
//      payload.put(AppContext.DATA_STRING, tim);
      
      return timData;
   }
}
