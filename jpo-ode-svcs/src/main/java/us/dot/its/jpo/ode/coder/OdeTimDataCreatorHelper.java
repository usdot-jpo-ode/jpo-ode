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
      metadata.remove(AppContext.ENCODINGS_STRING);
      
      JSONObject payload = timData.getJSONObject(AppContext.PAYLOAD_STRING);
      payload.put(AppContext.DATA_TYPE_STRING, "TravelerInformation");
      return timData;
   }
}
