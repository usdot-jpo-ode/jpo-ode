package us.dot.its.jpo.ode.newcoder;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.security.SecurityManager;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;

public class HexDecoderPublisher implements DecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(HexDecoderPublisher.class);
   private MessagePublisher publisher;
   private OssJ2735Coder j2735Coder;
   private SerialId serialId;

   public HexDecoderPublisher(MessagePublisher dataPub, OssJ2735Coder j2735plugin,
         SerialId serId, AtomicInteger bunId) {
      this.publisher = dataPub;
      this.j2735Coder = j2735plugin;

      this.serialId = serId;
      this.serialId.setBundleId(bunId.incrementAndGet());
   }

   @Override
   public void decodeAndPublish(InputStream is, String fileName) throws Exception {
      String line = null;
      OdeData decoded = null;

      try (Scanner scanner = new Scanner(is)) {

         boolean empty = true;
         while (scanner.hasNextLine()) {
            empty = false;
            line = scanner.nextLine();

            decoded = decode(HexUtils.fromHexString(line), fileName);
            if (decoded != null) {
               logger.debug("Decoded: {}", decoded);
               publisher.publish(decoded);
            } else {
               logger.debug("Failed to decode, null.");
            }
         }

         if (empty) {
            throw new Exception("Empty file received");
         }
      } catch (Exception e) {
         logger.error("Error decoding and publishing data: {}", line, e);
      }
   }

   private OdeObject getBsmPayload(IEEE1609p2Message message) {
      try {
         SecurityManager.validateGenerationTime(message);
      } catch (SecurityManagerException e) {
         // TODO handle error
         logger.error("Error validating message.", e);
      }

      return decodeBsm(message.getPayload());
   }

   private OdeObject decodeBsm(byte[] bytes) {
      J2735MessageFrame mf = (J2735MessageFrame) j2735Coder.decodeUPERMessageFrameBytes(bytes);
      if (mf != null) {
         return mf.getValue();
      } else {
         return j2735Coder.decodeUPERBsmBytes(bytes);
      }
   }

   public OdeData decode(byte[] data, String fileName) throws Exception {
      IEEE1609p2Message message = null;

      OdeData odeBsmData = null;
      OdeObject bsm = null;
      try {
         message = SecurityManager.decodeSignedMessage(data);
         bsm = getBsmPayload(message);
      } catch (Exception e) {
         logger.debug("Message does not have a valid signature");
      }

      if (bsm != null && message != null) {
         odeBsmData = DecoderPublisherUtils.createOdeBsmData((J2735Bsm) bsm, message, fileName, serialId);
      }

      return odeBsmData;
   }

}
