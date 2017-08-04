package us.dot.its.jpo.ode.coder.stream;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.DecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class BinaryDecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);

   private SerialId serialId;

   private MessagePublisher publisher;

   private static AtomicInteger bundleId = new AtomicInteger(1);
   
   private DecoderHelper decoder;

   public BinaryDecoderPublisher(SerialId serId, MessagePublisher dataPub, DecoderHelper decoderHelper) {
      this.serialId = serId;

      this.serialId = serId;
      this.serialId.setBundleId(bundleId.incrementAndGet());

      this.publisher = dataPub;
      
      this.decoder = decoderHelper;
   }

   public void decodeBinaryAndPublish(InputStream is, String fileName) throws Exception {
      OdeData decoded = null;

      do {
         try {
            decoded = decoder.decode(is, fileName, serialId);
            if (decoded != null) {
               logger.debug("Decoded: {}", decoded);
               publisher.publish(decoded);
            } else {
               logger.debug("Failed to decode, null.");
            }
         } catch (Exception e) {
            String msg = "Error decoding and publishing data.";
            EventLogger.logger.error(msg, e);
            logger.error(msg, e);
         }
      } while (decoded != null);
   }

   

}
