package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.OdeDataPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.importer.parser.DistressMsgFileParser;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.TimLogFileParser;
import us.dot.its.jpo.ode.model.OdeData;

public class BinaryDecoderPublisher extends AbstractDecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);
   private OdeDataPublisher timMessagePublisher;

   public BinaryDecoderPublisher(
      OdeDataPublisher bsmMessagePublisher, OdeDataPublisher timMessagePublisher) {
      super(bsmMessagePublisher);
      this.timMessagePublisher = timMessagePublisher;
   }

   @Override
   public void decodeAndPublish(BufferedInputStream bis, String fileName, ImporterFileType fileType) 
         throws Exception {
      OdeData decoded = null;

      LogFileParser fileParser = null;

      do {
         try {

            // Step 1 - parse and decode
            ParserStatus status = ParserStatus.UNKNOWN;

            if (fileType == ImporterFileType.OBU_LOG_FILE) {
               fileParser = LogFileParser.factory(fileName, bundleId.incrementAndGet());

               status = fileParser.parseFile(bis, fileName);
               if (status == ParserStatus.COMPLETE) {
                  try {
                     if (fileParser instanceof BsmLogFileParser) {
                        logger.debug("Attempting to decode log file message as a BSM...");
                        decoded = bsmDecoder.decode((BsmLogFileParser) fileParser,
                              this.serialId.setBundleId(bundleId.get()));
                     } else if (fileParser instanceof TimLogFileParser) {
                        logger.debug("Attempting to decode log file message as a rxTIM...");
                        decoded = timDecoder.decode((TimLogFileParser) fileParser,
                              this.serialId.setBundleId(bundleId.get()));
                     }
                  } catch (Exception e) {
                     logger.debug("Failed to decode log file message.", e);

                  }
               } else {
                  logger.debug("Status = {}", status);
                  return;
               }

            } else {
                decoded = bsmDecoder.decode(bis, fileName, 
                    this.serialId.setBundleId(bundleId.get()).addRecordId(1));
                status = ParserStatus.NA;
            }

            // Step 2 - publish
            if (decoded != null) {
               if (fileType == ImporterFileType.OBU_LOG_FILE) {
                  if (fileParser instanceof BsmLogFileParser) {
                     logger.debug("Decoded a bsm: {}", decoded);
                     bsmMessagePublisher.publish(decoded, 
                        bsmMessagePublisher.getOdeProperties().getKafkaTopicOdeBsmPojo());
                  } else if (fileParser instanceof DistressMsgFileParser) {
                     // this 'else if' should be before the next 'else if' 
                     logger.debug("Decoded a distressMsg: {}", decoded);
                     timMessagePublisher.publish(decoded, timMessagePublisher.getOdeProperties().getKafkaTopicOdeDNMsgPojo());
                  } else if (fileParser instanceof TimLogFileParser) {
                     logger.debug("Decoded a tim: {}", decoded);
                     timMessagePublisher.publish(decoded, timMessagePublisher.getOdeProperties().getKafkaTopicOdeTimPojo());
                  }
               }
            } else {
               // if parser returns PARTIAL record, we will go back and continue
               // parsing
               // but if it's UNKNOWN, it means that we could not parse the
               // header bytes
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