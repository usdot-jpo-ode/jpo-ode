package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.ByteArrayPublisher;
import us.dot.its.jpo.ode.importer.BsmFileParser;
import us.dot.its.jpo.ode.importer.LogFileParser.ParserStatus;

public class BsmAsn1CodecPublisher extends AbstractAsn1CodecPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BsmAsn1CodecPublisher.class);

    protected BsmFileParser bsmFileParser;

    public BsmAsn1CodecPublisher(ByteArrayPublisher dataPub) {
        super(dataPub);
        /* 
         * CAUTION: bsmFileParser needs to be created here and should not be moved to the
         * constructor because only one DecoderPublisher exists per filetype/upload directory
         * and we need to have a new BsmFileParser per uploaded file. If we put this instantiation
         * in the constructor, all uploaded files will be using the same parser which may throw 
         * off the parsing.
         */
        this.bsmFileParser = new BsmFileParser();
    }

   @Override
   public void publish(BufferedInputStream bis, String fileName) throws Exception {
      ParserStatus status = ParserStatus.UNKNOWN;
      do {
         try {
            status = bsmFileParser.parse(bis, fileName, bundleId.incrementAndGet());

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
