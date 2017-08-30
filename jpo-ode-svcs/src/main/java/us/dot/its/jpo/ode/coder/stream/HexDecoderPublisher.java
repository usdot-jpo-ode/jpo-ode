package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;

public class HexDecoderPublisher extends AbstractDecoderPublisher  {

   private static final Logger logger = LoggerFactory.getLogger(HexDecoderPublisher.class);

   public HexDecoderPublisher(MessagePublisher dataPub) {
        super(dataPub);
   }

   @Override
   public void decodeAndPublish(BufferedInputStream is, String fileName) throws Exception {
      String line = null;
      OdeData decoded = null;

      try (Scanner scanner = new Scanner(is)) {

         boolean empty = true;
         while (scanner.hasNextLine()) {
            empty = false;
            line = scanner.nextLine();

            bsmFileParser.parse(new BufferedInputStream(new ByteArrayInputStream(HexUtils.fromHexString(line))));
            decoded = bsmDecoder.decode(bsmFileParser, fileName, this.serialId.setBundleId(bundleId.incrementAndGet()));
            if (decoded != null) {
               logger.debug("Decoded: {}", decoded);
               publisher.publish(decoded);
            } else {
               logger.debug("Failed to decode {}.", line);
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
