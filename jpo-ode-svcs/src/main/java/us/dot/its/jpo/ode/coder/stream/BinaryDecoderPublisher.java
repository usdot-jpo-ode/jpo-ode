package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.OdeDataPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.BsmFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.model.OdeData;

public class BinaryDecoderPublisher extends AbstractDecoderPublisher {

   private static final String BSMS_FOR_EVENT_PREFIX = "bsmLogDuringEvent";
   private static final String RECEIVED_MESSAGES_PREFIX = "rxMsg";

   private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);
   private OdeDataPublisher timMessagePublisher;

   public BinaryDecoderPublisher(
      OdeDataPublisher bsmMessagePublisher, OdeDataPublisher timMessagePublisher) {
      super(bsmMessagePublisher);
      this.timMessagePublisher = timMessagePublisher;
   }

   @Override
   public void decodeAndPublish(BufferedInputStream bis, String fileName, ImporterFileType fileType) throws Exception {
      OdeData decoded = null;

      LogFileParser fileParser = null;

      do {
         try {

            // Step 1 - parse and decode
            ParserStatus status = ParserStatus.UNKNOWN;

            if (fileType == ImporterFileType.BSM_LOG_FILE) {
               if (fileName.startsWith(BSMS_FOR_EVENT_PREFIX)) {
                  logger.debug("Parsing as \"BSM For Event\" log file type.");
                  fileParser = new BsmFileParser(bundleId.incrementAndGet());
               } else if (fileName.startsWith(RECEIVED_MESSAGES_PREFIX)) {
                  logger.debug("Parsing as \"Received Messages\" log file type.");
                  fileParser = new RxMsgFileParser(bundleId.incrementAndGet());
               } else {
                  throw new IllegalArgumentException("Unknown log file prefix: " + fileName);
               }

               status = fileParser.parse(bis, fileName);
               if (status == ParserStatus.COMPLETE) {
                  try {
                     if (fileParser instanceof BsmFileParser) {
                        logger.debug("Attempting to decode log file message as a BSM...");
                        decoded = bsmDecoder.decode((BsmFileParser) fileParser,
                              this.serialId.setBundleId(bundleId.get()));
                     } else if (fileParser instanceof RxMsgFileParser) {
                        decoded = timDecoder.decode((RxMsgFileParser) fileParser,
                              this.serialId.setBundleId(bundleId.get()));
                     }
                  } catch (Exception e) {
                     logger.debug("Failed to decode log file message.", e);

                  }
               } else if (status == ParserStatus.EOF) {
                  return;
               }

            } else {
                decoded = bsmDecoder.decode(bis, fileName, 
                    this.serialId.setBundleId(bundleId.get()).addRecordId(1));
                status = ParserStatus.NA;
            }

            // Step 2 - publish
            if (decoded != null) {
               if (fileType == ImporterFileType.BSM_LOG_FILE) {
                  if (fileParser instanceof BsmFileParser) {
                     logger.debug("Decoded a bsm: {}", decoded);
                     bsmMessagePublisher.publish(decoded, bsmMessagePublisher.getOdeProperties().getKafkaTopicOdeBsmPojo());
                  } else if (fileType == ImporterFileType.BSM_LOG_FILE && fileParser instanceof RxMsgFileParser) {
                     logger.debug("Decoded a tim: {}", decoded);
                     timMessagePublisher.publish(decoded, bsmMessagePublisher.getOdeProperties().getKafkaTopicOdeTimPojo());
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