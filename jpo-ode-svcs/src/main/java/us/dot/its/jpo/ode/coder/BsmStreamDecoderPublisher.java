package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.J2735BsmSerializer;

public class BsmStreamDecoderPublisher extends AbstractStreamDecoderPublisher {

   private Path filePath;

   public BsmStreamDecoderPublisher(OdeProperties properties, Path filePath) {
      super(properties, J2735BsmSerializer.class.getName());
      this.filePath = filePath;
   }

   @Override
   public OdeObject decode(String line) {
       OdeObject decoded;
       
       Ieee1609Dot2Data ieee1609dot2Data = 
               ieee1609dotCoder.decodeIeee1609Dot2DataHex(line);
        
        if (ieee1609dot2Data != null) { // message is signed
           byte[] unsecureData = ieee1609dot2Data
                   .getContent()
                   .getSignedData()
                   .getTbsData()
                   .getPayload()
                   .getData()
                   .getContent()
                   .getUnsecuredData().byteArrayValue();
           decoded = decodeBsm(unsecureData);
        } else {// message not signed
            logger.debug("Message not signed");
            decoded = decodeBsm(CodecUtils.fromHex(line));
        }

       return decoded;
   }

   @Override
   public OdeObject decode(InputStream is) {
       OdeObject decoded;
       
       Ieee1609Dot2Data ieee1609dot2Data = 
               ieee1609dotCoder.decodeIeee1609Dot2DataStream(is);
        
        if (ieee1609dot2Data != null) { // message is signed
           byte[] unsecureData = ieee1609dot2Data
                   .getContent()
                   .getSignedData()
                   .getTbsData()
                   .getPayload()
                   .getData()
                   .getContent()
                   .getUnsecuredData().byteArrayValue();
           decoded = decodeBsm(unsecureData);
        } else {// message not signed
            logger.debug("Message not signed");
            decoded = decodeBsm(is);
        }

       return decoded;
   }

    private OdeObject decodeBsm(byte[] bytes) {
        J2735MessageFrame mf = 
                (J2735MessageFrame) j2735Coder.decodeUPERMessageFrameBytes(bytes);
        if (mf != null) {
            return mf.getValue();
        } else {
            return j2735Coder.decodeUPERBsmBytes(bytes);
        }
    }

    private OdeObject decodeBsm(InputStream is) {
        J2735MessageFrame mf = 
                (J2735MessageFrame) j2735Coder.decodeUPERMessageFrameStream(is);
        if (mf != null) {
            return mf.getValue();
        } else {
            return j2735Coder.decodeUPERBsmStream(is);
        }
    }

    @Override
   public void publish(OdeObject msg, Ieee1609Dot2Data ieee1609dot2Data) {
      logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicBsmSerializedPojo(), msg.toJson());
      objectProducer.send(odeProperties.getKafkaTopicBsmSerializedPojo(), null, msg);
      
      OdeBsmData odeBsmData = 
              createOdeBsmData((J2735Bsm) msg, ieee1609dot2Data);
      
      logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicOdeBsmPojo(), odeBsmData.toJson());
      objectProducer.send(odeProperties.getKafkaTopicOdeBsmPojo(), null, odeBsmData);
      
   }

   public OdeBsmData createOdeBsmData(
       J2735Bsm msg, Ieee1609Dot2Data ieee1609dot2Data) {
        OdeBsmPayload payload = new OdeBsmPayload(msg);
        long generationTime = ieee1609dot2Data
                .getContent()
                .getSignedData()
                .getTbsData()
                .getHeaderInfo()
                .getGenerationTime().longValue();
        
        Instant ieee1609Epoc = Instant.EPOCH.plus((2004-1970), ChronoUnit.YEARS);
        Instant generationInstant = ieee1609Epoc.plusMillis(generationTime/1000); 
                
        ZonedDateTime generatedAt = ZonedDateTime.ofInstant(generationInstant, ZoneId.of("UTC"));
        OdeBsmMetadata metadata = new OdeBsmMetadata(
            payload, 
            generatedAt.toString());
        
        metadata.setLogFileName(filePath.getFileName().toString());
        OdeBsmData bsmData = new OdeBsmData(metadata, payload);
        
        return bsmData;
        
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
            publish(decoded, ieee1609dot2Data);
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
   public void publish(String msg, Ieee1609Dot2Data ieee1609dot2Data) {
      logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicBsmRawJson(), msg);
      stringProducer.send(odeProperties.getKafkaTopicBsmRawJson(), null, msg);

   }

   @Override
   public void publish(byte[] msg, Ieee1609Dot2Data ieee1609dot2Data) {
      logger.debug("Publishing byte array to {}", odeProperties.getKafkaTopicBsmSerializedPojo());

      byteArrayProducer.send(odeProperties.getKafkaTopicBsmSerializedPojo(), null, msg);
   }

   @Override
   protected Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }

}
