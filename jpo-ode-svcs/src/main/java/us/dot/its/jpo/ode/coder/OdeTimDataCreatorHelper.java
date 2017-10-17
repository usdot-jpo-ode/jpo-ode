package us.dot.its.jpo.ode.coder;

import org.json.JSONObject;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeTimPayload;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerInformationFromAsnToHumanConverter;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeTimDataCreatorHelper {

   private OdeTimDataCreatorHelper() {
   }

   public static JSONObject createOdeTimData(String consumedData) throws XmlUtilsException { 

      JSONObject timData = XmlUtils.toJSONObject(consumedData)
            .getJSONObject(OdeAsn1Data.class.getSimpleName());

      JSONObject metadata = timData.getJSONObject(AppContext.METADATA_STRING);
      metadata.put("payloadType", OdeTimPayload.class.getName());
      metadata.remove("encodings");
      
      JSONObject payload = timData.getJSONObject(AppContext.PAYLOAD_STRING);
      payload.put(AppContext.DATA_TYPE_STRING, "TravelerInformation");
      // Do other TIM specific mods before returning the data 
      // TODO FIX THIS
      JSONObject tim = TravelerInformationFromAsnToHumanConverter.genericTim(
         payload.getJSONObject(AppContext.DATA_STRING)
            .getJSONObject("MessageFrame")
            .getJSONObject("value")
            .getJSONObject("TravelerInformation"));
      payload.put(AppContext.DATA_STRING, tim);
      
      return timData;
   }
}
