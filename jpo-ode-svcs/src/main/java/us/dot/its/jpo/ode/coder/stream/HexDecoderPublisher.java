package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class HexDecoderPublisher implements DecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(HexDecoderPublisher.class);
   private MessagePublisher publisher;
   private SerialId serialId;
   private BsmDecoderHelper bsmDecoder;
   private static AtomicInteger bundleId = new AtomicInteger(1);

   public HexDecoderPublisher(MessagePublisher dataPub) {
      this.publisher = dataPub;

      this.serialId = new SerialId();
      this.serialId.setBundleId(bundleId.incrementAndGet());
      this.bsmDecoder = new BsmDecoderHelper();
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

            decoded = bsmDecoder.decode(new BufferedInputStream(new ByteArrayInputStream(HexUtils.fromHexString(line))), fileName, this.serialId.setBundleId(bundleId.incrementAndGet()));
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
