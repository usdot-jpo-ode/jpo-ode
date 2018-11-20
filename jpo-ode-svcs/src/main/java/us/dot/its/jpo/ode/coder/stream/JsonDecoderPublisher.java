package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.OdeBsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeStringPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;

public class JsonDecoderPublisher implements DecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(JsonDecoderPublisher.class);

   private OdeStringPublisher publisher;
   private SerialId serialId;

   private static AtomicInteger bundleId = new AtomicInteger(1);

   public JsonDecoderPublisher(OdeStringPublisher stringPub) {
      this.publisher = stringPub;
      this.serialId = new SerialId();
      this.serialId.setBundleId(bundleId.incrementAndGet());
   }

   @Override
   public void decodeAndPublish(BufferedInputStream is, String fileName, ImporterFileType fileType) {
      String line = null;

      try (Scanner scanner = new Scanner(is)) {

         boolean empty = true;
         while (scanner.hasNextLine()) {
            empty = false;
            line = scanner.nextLine();

            J2735Bsm j2735Bsm = (J2735Bsm) JsonUtils.fromJson(line, J2735Bsm.class);
            OdeData odeBsm = OdeBsmDataCreatorHelper.createOdeBsmData(
               j2735Bsm, fileName);
            
            publisher.publish(odeBsm, publisher.getOdeProperties().getKafkaTopicOdeBsmJson());
         }
         if (empty) {
            throw new Exception("Empty file received");
         }
      } catch (Exception e) {
         logger.error("Error occurred while decoding message: {}", line, e);
      }
   }

}
