package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class ByteDecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(ByteDecoderPublisher.class);
   private MessagePublisher publisher;
   private SerialId serialId;

   private static AtomicInteger bundleId = new AtomicInteger(1);

   public ByteDecoderPublisher(MessagePublisher dataPub) {
      this.publisher = dataPub;
      this.serialId = new SerialId();
      this.serialId.setBundleId(bundleId.incrementAndGet());
   }

   public void decodeAndPublish(byte[] bytes) throws Exception {
      OdeData decoded;

      try {
         decoded = BsmDecoderHelper.decode(new BufferedInputStream(new ByteArrayInputStream(bytes)), null, this.serialId.setBundleId(bundleId.incrementAndGet()));
         if (decoded != null) {
            logger.debug("Decoded: {}", decoded);
            publisher.publish(decoded);
         }
      } catch (Exception e) {
         logger.error("Error decoding and publishing data.", e);
      }
   }
}
