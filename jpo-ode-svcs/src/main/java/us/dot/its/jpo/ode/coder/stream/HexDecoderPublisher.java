package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.OdeDataPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.LogFileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeData;

public class HexDecoderPublisher extends AbstractDecoderPublisher  {

   private static final Logger logger = LoggerFactory.getLogger(HexDecoderPublisher.class);

   public HexDecoderPublisher(OdeDataPublisher dataPub) {
        super(dataPub);
   }

   @Override
   public void decodeAndPublish(BufferedInputStream bis, String fileName, ImporterFileType fileType ) throws Exception {
      super.decodeAndPublish(bis, fileName, fileType );
      String line = null;
      OdeData decoded = null;

      try (Scanner scanner = new Scanner(bis)) {

         boolean empty = true;
         while (scanner.hasNextLine()) {
            empty = false;
            line = scanner.nextLine();

            ParserStatus status = ParserStatus.UNKNOWN;
            if (fileType == ImporterFileType.BSM_LOG_FILE) {
                status = bsmFileParser.parse(new BufferedInputStream(
                   new ByteArrayInputStream(HexUtils.fromHexString(line))), fileName,
                   bundleId.get());
            } else {
                bsmFileParser.setPayload(HexUtils.fromHexString(line));
                status = ParserStatus.NA;
            }
            
            if (status == ParserStatus.COMPLETE) {
                decoded = bsmDecoder.decode(bsmFileParser, 
                    this.serialId.setBundleId(bundleId.get()));
            } else if (status == ParserStatus.EOF) {
                return;
            }
            if (decoded != null) {
               logger.debug("Decoded: {}", decoded);
               publisher.publish(decoded, publisher.getOdeProperties().getKafkaTopicOdeBsmPojo());
            } else {
                // if parser returns PARTIAL record, we will go back and continue parsing
                // but if it's UNKNOWN, it means that we could not parse the header bytes
                if (status == ParserStatus.INIT) {
                    logger.error("Failed to parse the header bytes: {}", line);
                } else {
                    logger.error("Failed to decode ASN.1 data: {}", line);
                }
            }
         }

         if (empty) {
            throw new Exception("Empty file received");
         }
      } catch (Exception e) {
         logger.error("Error decoding and publishing data: {}", line, e);
      }
   }
}
