package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;

public class BinaryDecoderPublisher extends AbstractDecoderPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);

    public BinaryDecoderPublisher(MessagePublisher dataPub) {
        super(dataPub);
    }

   @Override
   public void decodeAndPublish(BufferedInputStream bis, String fileName, boolean hasMetadataHeader) throws Exception {
      OdeData decoded = null;

      do {
         try {
            if (hasMetadataHeader) {
                bsmFileParser.parse(bis, fileName);
                decoded = bsmDecoder.decode(bsmFileParser, 
                    this.serialId.setBundleId(bundleId.incrementAndGet()));
            } else {
                //TODO ODE-512
//                decoded = bsmDecoder.decode(bis, 
//                    this.serialId.setBundleId(bundleId.incrementAndGet()));
            }
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
