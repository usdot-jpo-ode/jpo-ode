package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;

public class ByteDecoderPublisher extends AbstractDecoderPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ByteDecoderPublisher.class);

    public ByteDecoderPublisher(MessagePublisher dataPub) {
        super(dataPub);
    }

   public void decodeAndPublish(byte[] bytes) throws Exception {
      OdeData decoded;

      try {
         bsmFileParser.parse(new BufferedInputStream(new ByteArrayInputStream(bytes)));
         decoded = bsmDecoder.decode(bsmFileParser, null,
             this.serialId.setBundleId(bundleId.incrementAndGet()));

         if (decoded != null) {
            logger.debug("Decoded: {}", decoded);
            publisher.publish(decoded);
         }
      } catch (Exception e) {
         logger.error("Error decoding and publishing data.", e);
      }
   }

    @Override
    public void decodeAndPublish(BufferedInputStream is, String fileName) throws Exception {
    }
}
