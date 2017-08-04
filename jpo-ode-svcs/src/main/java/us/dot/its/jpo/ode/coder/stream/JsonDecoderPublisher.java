package us.dot.its.jpo.ode.coder.stream;

import java.io.InputStream;
import java.util.Scanner;

import us.dot.its.jpo.ode.coder.DecoderPublisherUtils;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;

public class JsonDecoderPublisher implements DecoderPublisher {

   private MessagePublisher publisher;
   private SerialId serialId;

   public JsonDecoderPublisher(MessagePublisher dataPub, SerialId serId) {
      this.publisher = dataPub;
      this.serialId = serId;
   }

   @Override
   public void decodeAndPublish(InputStream is, String fileName) throws Exception {
      String line = null;

      try (Scanner scanner = new Scanner(is)) {

         boolean empty = true;
         while (scanner.hasNextLine()) {
            empty = false;
            line = scanner.nextLine();

            J2735Bsm j2735Bsm = (J2735Bsm) JsonUtils.fromJson(line, J2735Bsm.class);
            OdeData odeBsm = DecoderPublisherUtils.createOdeBsmData(j2735Bsm, null, fileName, serialId);
            publisher.publish(odeBsm);
         }
         if (empty) {
            EventLogger.logger.info("Empty file received");
            throw new Exception("Empty file received");
         }
      } catch (Exception e) {
         EventLogger.logger.info("Error occurred while decoding message: {}", line);
         throw new Exception("Error decoding data: " + line, e);
      }
   }

}
