package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.OdeDataPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.LogFileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeData;

public class BinaryDecoderPublisher extends AbstractDecoderPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);

    public BinaryDecoderPublisher(OdeDataPublisher dataPub) {
        super(dataPub);
    }

   @Override
   public void decodeAndPublish(BufferedInputStream bis, String fileName, ImporterFileType fileType) throws Exception {
      super.decodeAndPublish(bis, fileName, fileType);
      OdeData decoded = null;

      do {
         try {
            ParserStatus status = ParserStatus.UNKNOWN;
            if (fileType == ImporterFileType.BSM_LOG_FILE) {
                status = bsmFileParser.parse(bis, fileName, bundleId.get());
                if (status == ParserStatus.COMPLETE) {
                    decoded = bsmDecoder.decode(bsmFileParser, 
                        this.serialId.setBundleId(bundleId.get()));
                } else if (status == ParserStatus.EOF) {
                    return;
                }
            } else {
                decoded = bsmDecoder.decode(bis, fileName, 
                    this.serialId.setBundleId(bundleId.get()));
                status = ParserStatus.NA;
            }
            if (decoded != null) {
               logger.debug("Decoded: {}", decoded);
               publisher.publish(decoded, publisher.getOdeProperties().getKafkaTopicOdeBsmPojo());
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
