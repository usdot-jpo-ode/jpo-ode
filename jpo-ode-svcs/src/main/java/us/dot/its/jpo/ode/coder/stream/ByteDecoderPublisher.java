package us.dot.its.jpo.ode.coder.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.DecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;

public class ByteDecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(ByteDecoderPublisher.class);
   private MessagePublisher publisher;
   private DecoderHelper decoder;

   public ByteDecoderPublisher(MessagePublisher dataPub, DecoderHelper decoderHelper) {
      this.publisher = dataPub;
      this.decoder = decoderHelper;
   }

   public void decodeAndPublish(byte[] bytes) throws Exception {
      OdeData decoded;

      try {
         decoded = decoder.decode(bytes, null, null);
         if (decoded != null) {
            logger.debug("Decoded: {}", decoded);
            publisher.publish(decoded);
         }
      } catch (Exception e) {
         logger.error("Error decoding and publishing data.", e);
      }
   }
}
