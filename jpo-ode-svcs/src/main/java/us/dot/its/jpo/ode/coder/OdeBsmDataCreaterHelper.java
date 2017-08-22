package us.dot.its.jpo.ode.coder;

import java.time.ZonedDateTime;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OdeBsmDataCreaterHelper {

   public OdeBsmDataCreaterHelper() {
   }

   public OdeBsmData createOdeBsmData(J2735Bsm rawBsm, IEEE1609p2Message message, String fileName, SerialId serialId) {
      OdeBsmPayload payload = new OdeBsmPayload(rawBsm);

      OdeBsmMetadata metadata = new OdeBsmMetadata(payload);
      metadata.setSerialId(serialId);

      if (message != null) {
         ZonedDateTime generatedAt = DateTimeUtils.isoDateTime(message.getGenerationTime());
         metadata.setGeneratedAt(generatedAt.toString());

         metadata.setValidSignature(true);
      } else {
         /*
          * TODO Temporarily put in place for testing CV PEP. Should be removed after
          * testing is complete.
          */
         metadata.setGeneratedAt(metadata.getReceivedAt());
      }

      metadata.getSerialId().addRecordId(1);
      metadata.setLogFileName(fileName);
      return new OdeBsmData(metadata, payload);
   }
}
