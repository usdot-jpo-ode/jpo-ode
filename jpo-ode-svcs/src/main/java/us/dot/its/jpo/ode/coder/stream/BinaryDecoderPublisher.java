package us.dot.its.jpo.ode.coder.stream;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.DecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class BinaryDecoderPublisher implements DecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);

   private SerialId serialId;

   private MessagePublisher publisher;

   private static AtomicInteger bundleId = new AtomicInteger(1);

   private DecoderHelper decoder;

   public BinaryDecoderPublisher(MessagePublisher dataPub, DecoderHelper decoderHelper) {

      this.serialId = new SerialId();
      this.serialId.setBundleId(bundleId.incrementAndGet());

      this.publisher = dataPub;
      this.decoder = decoderHelper;
   }

   @Override
   public void decodeAndPublish(InputStream is, String fileName) throws Exception {
      OdeData decoded = null;

      do {
         try {
            decoded = decoder.decode(is, fileName, this.serialId.setBundleId(bundleId.incrementAndGet()));
            if (decoded != null) {
               logger.debug("Decoded: {}", decoded);
               publisher.publish(decoded);
            } else {
               logger.debug("Failed to decode, null.");
            }
         } catch (Exception e) {
            logger.error("Error decoding and publishing data.", e);
         }
      } while (decoded != null);
   }

}
