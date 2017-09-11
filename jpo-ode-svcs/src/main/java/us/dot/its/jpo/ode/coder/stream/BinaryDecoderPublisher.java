package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.importer.LogFileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeData;

public class BinaryDecoderPublisher extends AbstractDecoderPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);

    public BinaryDecoderPublisher(MessagePublisher dataPub) {
        super(dataPub);
    }

   @Override
   public void decodeAndPublish(BufferedInputStream bis, String fileName, boolean hasMetadataHeader) throws Exception {
      super.decodeAndPublish(bis, fileName, hasMetadataHeader);
      OdeData decoded = null;

      do {
         try {
            ParserStatus status = ParserStatus.UNKNOWN;
            if (hasMetadataHeader) {
                status = bsmFileParser.parse(bis, fileName);
                if (status == ParserStatus.COMPLETE) {
                    decoded = bsmDecoder.decode(bsmFileParser, 
                        this.serialId.setBundleId(bundleId.incrementAndGet()));
                } else if (status == ParserStatus.EOF) {
                    return;
                }
            } else {
                decoded = bsmDecoder.decode(bis, fileName, 
                    this.serialId.setBundleId(bundleId.incrementAndGet()));
                status = ParserStatus.NA;
            }
            if (decoded != null) {
               logger.debug("Decoded: {}", decoded);
               publisher.publish(decoded);
            } else {
                // if parser returns PARTIAL record, we will go back and continue parsing
                // but if it's UNKNOWN, it means that we could not parse the header bytes
                if (status == ParserStatus.INIT) {
                    logger.error("Failed to parse the header bytes.");
                } else {
                    logger.error("Failed to decode ASN.1 data");
                }
            }
         } catch (Exception e) {
            logger.error("Error decoding and publishing data.", e);
         }
      } while (decoded != null);
   }

}
