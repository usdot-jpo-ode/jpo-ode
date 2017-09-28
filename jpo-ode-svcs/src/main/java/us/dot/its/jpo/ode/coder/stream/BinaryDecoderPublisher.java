package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.BsmMessagePublisher;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.importer.parser.BsmFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.model.OdeData;

public class BinaryDecoderPublisher extends AbstractDecoderPublisher {

   private static final String BSMS_FOR_EVENT_PREFIX = "bsmLogDuringEvent";
   private static final String RECEIVED_MESSAGES_PREFIX = "rxMsg";

   private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);
   private BsmMessagePublisher bsmMessagePublisher;
   private MessagePublisher timMessagePublisher;

   public BinaryDecoderPublisher(BsmMessagePublisher bsmMessagePublisher, MessagePublisher timMessagePublisher) {
      this.bsmMessagePublisher = bsmMessagePublisher;
      this.timMessagePublisher = timMessagePublisher;
   }

   @Override
   public void decodeAndPublish(BufferedInputStream bis, String fileName, boolean hasMetadataHeader) throws Exception {
      OdeData decoded = null;

      LogFileParser fileParser = null;

      do {
         try {

            // Step 1 - parse and decode
            ParserStatus status = ParserStatus.UNKNOWN;

            if (hasMetadataHeader) {
               if (fileName.startsWith(BSMS_FOR_EVENT_PREFIX)) {
                  logger.debug("Parsing as \"BSM For Event\" log file type.");
                  fileParser = new BsmFileParser();
               } else if (fileName.startsWith(RECEIVED_MESSAGES_PREFIX)) {
                  logger.debug("Parsing as \"Received Messages\" log file type.");
                  fileParser = new RxMsgFileParser();
               } else {
                  throw new IllegalArgumentException("Unknown log file prefix: " + fileName);
               }

               status = fileParser.parse(bis, fileName);
               if (status == ParserStatus.COMPLETE) {
                  try {
                     if (fileParser instanceof BsmFileParser) {
                        logger.debug("Attempting to decode log file message as a BSM...");
                        decoded = bsmDecoder.decode((BsmFileParser) fileParser,
                              this.serialId.setBundleId(bundleId.incrementAndGet()));
                     } else if (fileParser instanceof RxMsgFileParser) {
                        decoded = timDecoder.decode((RxMsgFileParser) fileParser,
                              this.serialId.setBundleId(bundleId.incrementAndGet()));
                     }
                  } catch (Exception e) {
                     logger.debug("Failed to decode log file message.", e);

                  }
               } else if (status == ParserStatus.EOF) {
                  return;
               }

            } else {
               decoded = bsmDecoder.decode(bis, fileName, this.serialId.setBundleId(bundleId.incrementAndGet()));
               status = ParserStatus.NA;
            }

            // Step 2 - publish
            if (decoded != null) {
               if (hasMetadataHeader && fileParser instanceof BsmFileParser) {
                  logger.debug("Decoded a bsm: {}", decoded);
                  bsmMessagePublisher.publish(decoded);
               } else if (hasMetadataHeader && fileParser instanceof RxMsgFileParser) {
                  logger.debug("Decoded a tim: {}", decoded);
                  timMessagePublisher.publish(decoded);
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