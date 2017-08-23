package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.security.SecurityManager;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;

public class BsmDecoderPayloadHelper {

   private static final Logger logger = LoggerFactory.getLogger(BsmDecoderPayloadHelper.class);
   private final RawBsmMfSorter rawBsmMFSorterIn;

   public BsmDecoderPayloadHelper(RawBsmMfSorter rawBsmMFSorterIn) {
      this.rawBsmMFSorterIn = rawBsmMFSorterIn;
   }

   public OdeObject getBsmPayload(IEEE1609p2Message message) {
      try {
         SecurityManager.validateGenerationTime(message);
      } catch (SecurityManagerException e) {
         logger.error("Error validating message.", e);
      }

      return rawBsmMFSorterIn.decodeBsm(message.getPayload());
   }

}
