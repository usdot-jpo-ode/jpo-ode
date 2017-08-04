package us.dot.its.jpo.ode.coder.stream;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.DecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class ByteDecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(ByteDecoderPublisher.class);
   private MessagePublisher publisher;
   private DecoderHelper decoder;
   private SerialId serialId;

   private static AtomicInteger bundleId = new AtomicInteger(1);

   public ByteDecoderPublisher(MessagePublisher dataPub, DecoderHelper decoderHelper) {
      this.publisher = dataPub;
      this.decoder = decoderHelper;
      this.serialId = new SerialId();
      this.serialId.setBundleId(bundleId.incrementAndGet());
   }

   public void decodeAndPublish(byte[] bytes) throws Exception {
      OdeData decoded;

      try {
         // TODO - add serial ID functionality
         decoded = decoder.decode(bytes, null, this.serialId.setBundleId(bundleId.incrementAndGet()));
         if (decoded != null) {
            logger.debug("Decoded: {}", decoded);
            publisher.publish(decoded);
         }
      } catch (Exception e) {
         logger.error("Error decoding and publishing data.", e);
         throw new Exception("Error decoding data.", e);
      }
   }
}
