package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.ByteArrayPublisher;
import us.dot.its.jpo.ode.importer.parser.BsmFileParser;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;

public class BsmAsn1CodecPublisher extends AbstractAsn1CodecPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BsmAsn1CodecPublisher.class);

    protected BsmFileParser bsmFileParser;

    public BsmAsn1CodecPublisher(ByteArrayPublisher dataPub) {
        super(dataPub);
    }

   @Override
   public void publish(BufferedInputStream bis, String fileName) throws Exception {
      ParserStatus status = ParserStatus.UNKNOWN;
      this.bsmFileParser = new BsmFileParser(bundleId.incrementAndGet());
      do {
         try {
            status = bsmFileParser.parseFile(bis, fileName);

            if (status == ParserStatus.COMPLETE) {
               publisher.publish(bsmFileParser.getPayload(), publisher.getOdeProperties().getKafkaTopicEncodedBsmBytes());
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
      } while (status == ParserStatus.COMPLETE);
   }

}
