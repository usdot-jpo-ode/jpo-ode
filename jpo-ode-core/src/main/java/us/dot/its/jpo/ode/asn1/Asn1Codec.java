package us.dot.its.jpo.ode.asn1;

import us.dot.its.jpo.ode.asn1.Asn1Codec.Asn1CodecException;
import us.dot.its.jpo.ode.asn1.j2735.AbstractData;
import us.dot.its.jpo.ode.asn1.j2735.Coder;
import us.dot.its.jpo.ode.asn1.j2735.DecodeFailedException;
import us.dot.its.jpo.ode.asn1.j2735.DecodeNotSupportedException;
import us.dot.its.jpo.ode.asn1.j2735.DecoderException;
import us.dot.its.jpo.ode.asn1.j2735.msg.ids.ConnectedVehicleMessageID;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Asn1Codec {

   public class Asn1CodecException extends Exception {

   }

   public static Asn1Object decode(byte[] message) throws Asn1CodecException {
      Asn1Object abstractData = null;
      for( int preambleSize = 0; abstractData == null && preambleSize <= 2; preambleSize++ ) {
         ConnectedVehicleMessageID msgID = getMessageID(message, preambleSize);
         try {
            abstractData = decode(coder, message, msgID);
         } catch( DecoderException ex ) {
         }
      }
      return abstractData;
   }
   
}
