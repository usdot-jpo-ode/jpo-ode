package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.importer.LogFileParser.LogFileParserException;
import us.dot.its.jpo.ode.importer.LogFileParser.MessageType;
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
               
               int maxMessageSize = 2312;
               // We know it's a log file
               
               // Read the upper bound number bytes of the next message into a byte array
               // TIM <= 279 bytes
               // BSM <= 2312 bytes (TX), 2313 bytes (RX)
               int numBytes;
               if (bis.markSupported()) {
                  bis.mark(maxMessageSize);
               }
               byte[] readBuffer = new byte[maxMessageSize];
               numBytes = bis.read(readBuffer, 0, maxMessageSize);
               bis.reset();
               if (numBytes < 0) {
                  throw new IOException("Failed to read log file InputStream.");
               //} else if (numBytes < maxMessageSize) {
               //   throw new IOException("Failed to read log file InputStream, too few bytes remaining.");
               } else {
                  BufferedInputStream timBufferedInputStream = new BufferedInputStream(
                        new ByteArrayInputStream(readBuffer));
                  BufferedInputStream bsmBufferedInputStream = new BufferedInputStream(
                        new ByteArrayInputStream(readBuffer));

                  // Try to decode as a TIM
                  status = timFileParser.parse(timBufferedInputStream, fileName);
                  if (status == ParserStatus.COMPLETE) {
                     // TODO handle tim message
                     // ie. timDecoder.decode();
                     logger.debug("Bytes read from tim: {}", timFileParser.getBytesReadSoFar());
                     logger.debug("Extracted a TIM message: {}", HexUtils.toHexString(timFileParser.getAlert()));
                     if (bis.skip(timFileParser.getBytesReadSoFar()) < timFileParser.getBytesReadSoFar()) {
                        throw new IOException("Bytes skipped < bytes read");
                     }
                  } else {

                     // Failed to decode as a TIM, try BSM
                     status = bsmFileParser.parse(bsmBufferedInputStream, fileName);
                     if (status == ParserStatus.COMPLETE) {
                        decoded = bsmDecoder.decode(bsmFileParser,
                              this.serialId.setBundleId(bundleId.incrementAndGet()));
                        
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
                     } else if (status == ParserStatus.EOF) {
                        return;
                     }
                  }
               }
               
            } else {
                decoded = bsmDecoder.decode(bis, fileName, 
                    this.serialId.setBundleId(bundleId.incrementAndGet()));
                status = ParserStatus.NA;
                
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
            }
            
         } catch (Exception e) {
            logger.error("Error decoding and publishing data.", e);
         }
      } while (decoded != null);
   }

}
