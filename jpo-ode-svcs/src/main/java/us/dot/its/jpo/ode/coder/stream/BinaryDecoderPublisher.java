package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class BinaryDecoderPublisher implements DecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);

   private SerialId serialId;

   private MessagePublisher publisher;

   private BsmDecoderHelper bsmDecoder;

   private static AtomicInteger bundleId = new AtomicInteger(1);

   public BinaryDecoderPublisher(MessagePublisher dataPub) {
      this.publisher = dataPub;

      this.serialId = new SerialId();
      this.serialId.setBundleId(bundleId.incrementAndGet());
      this.bsmDecoder = new BsmDecoderHelper();
      
   }

   @Override
   public void decodeAndPublish(BufferedInputStream is, String fileName) throws Exception {
      OdeData decoded = null;

      do {
         try {
            
            
            decoded = bsmDecoder.decode(is, fileName, this.serialId.setBundleId(bundleId.incrementAndGet()));
            if (decoded != null) {
               logger.debug("Decoded: {}", decoded);
               publisher.publish(decoded);
            } else {
               logger.debug("Failed to decode data");
            }
         } catch (Exception e) {
            logger.error("Error decoding and publishing data.", e);
         }
      } while (decoded != null);
   }

}
