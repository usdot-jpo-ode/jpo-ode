package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.SerializationUtils;

public class BsmStreamDecoderPublisher extends AbstractStreamDecoderPublisher {

   public BsmStreamDecoderPublisher() {
      super();
   }

   public BsmStreamDecoderPublisher(OdeProperties properties) {
      super(properties);
   }

   @Override
   public Asn1Object decode(String line) {
      return asn1Coder.decodeUPERBsmHex(line);
   }

   @Override
   public Asn1Object decode(InputStream is) {
      return asn1Coder.decodeUPERBsmStream(is);
   }

   @Override
   public void publish(Asn1Object msg) {
      J2735Bsm bsm = (J2735Bsm) msg;
      SerializationUtils<J2735Bsm> serializer = new SerializationUtils<J2735Bsm>();
      publish(serializer.serialize(bsm));
   }

   @Override
   public void decodeJsonAndPublish(InputStream is) throws IOException {
      String line = null;
      Asn1Object decoded = null;

      try (Scanner scanner = new Scanner(is)) {

         boolean empty = true;
         while (scanner.hasNextLine()) {
            empty = false;
            line = scanner.nextLine();

            decoded = (Asn1Object) JsonUtils.fromJson(line, J2735Bsm.class);
            publish(decoded);
         }
         if (empty) {
            EventLogger.logger.info("Empty file received");
            throw new IOException("Empty file received");
         }
      } catch (IOException e) {
         EventLogger.logger.info("Error occurred while decoding message: {}", line);
         throw new IOException("Error decoding data: " + line, e);
      }
   }

   @Override
   public void publish(String msg) {
      logger.debug("Publishing: {}", msg);
      defaultProducer.send(odeProperties.getKafkaTopicBsmRawJson(), null, msg);
   }

   @Override
   public void publish(byte[] msg) {
      // MessageProducer<String, byte[]> producer =
      // messageProducerPool.checkOut();
      // producer.send(odeProperties.getKafkaTopicBsmSerializedPojo(), null,
      // msg);
      // messageProducerPool.checkIn(producer);
      logger.debug("Publishing byte message to {}", odeProperties.getKafkaTopicBsmSerializedPojo());
      byteArrayProducer.send(odeProperties.getKafkaTopicBsmSerializedPojo(), null, msg);
   }

   @Override
   protected Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }

}
