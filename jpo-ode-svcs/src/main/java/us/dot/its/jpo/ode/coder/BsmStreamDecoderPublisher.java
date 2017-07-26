package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.J2735BsmSerializer;

public class BsmStreamDecoderPublisher extends AbstractStreamDecoderPublisher {

   private Path filePath;
   private static final ZonedDateTime IEEE1609DOT2EPOC = 
           ZonedDateTime.of(2004, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
   

   public BsmStreamDecoderPublisher(OdeProperties properties, Path filePath) {
      super(properties, J2735BsmSerializer.class.getName());
      this.filePath = filePath;
   }

   @Override
   public OdeData decode(String hexEncodedData) {
        OdeData odeBsmData = null;
       
        Ieee1609Dot2Data ieee1609dot2Data = 
               ieee1609dotCoder.decodeIeee1609Dot2DataHex(hexEncodedData);
        
        OdeObject decoded;
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
            decoded = decodeBsm(CodecUtils.fromHex(hexEncodedData));
        }

        if (decoded != null) {
            odeBsmData = createOdeBsmData((J2735Bsm) decoded, ieee1609dot2Data);
        }

        return odeBsmData;
   }

   @Override
   public OdeData decode(InputStream is) {
       OdeData odeBsmData = null;
       
       Ieee1609Dot2Data ieee1609dot2Data = 
               ieee1609dotCoder.decodeIeee1609Dot2DataStream(is);
        
       OdeObject decoded;
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
        
       if (decoded != null) {
           odeBsmData = createOdeBsmData((J2735Bsm) decoded, ieee1609dot2Data);
       }

       return odeBsmData;
   }

   @Override
   public OdeData decode(byte[] data) {
       OdeData odeBsmData = null;
       
       Ieee1609Dot2Data ieee1609dot2Data = 
               ieee1609dotCoder.decodeIeee1609Dot2DataBytes(data);
        
       OdeObject decoded;
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
           decoded = decodeBsm(data);
       }
        
       if (decoded != null) {
           odeBsmData = createOdeBsmData((J2735Bsm) decoded, ieee1609dot2Data);
       }

       return odeBsmData;
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
    public void publish(OdeData msg) {
        OdeBsmData odeBsm = (OdeBsmData) msg;
        
        logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicRawBsmPojo(), odeBsm.getPayload().getData());
        objectProducer.send(odeProperties.getKafkaTopicRawBsmPojo(), null, odeBsm.getPayload().getData());

        logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicOdeBsmPojo(), odeBsm.toJson());
        objectProducer.send(odeProperties.getKafkaTopicOdeBsmPojo(), null, odeBsm);
    }

   public OdeBsmData createOdeBsmData(
       J2735Bsm rawBsm, Ieee1609Dot2Data ieee1609dot2Data) {
        OdeBsmPayload payload = new OdeBsmPayload(rawBsm);
        
        OdeBsmMetadata metadata = new OdeBsmMetadata(payload);
        
        if (ieee1609dot2Data != null) {
            long generationTimeMillis = ieee1609dot2Data
                    .getContent()
                    .getSignedData()
                    .getTbsData()
                    .getHeaderInfo()
                    .getGenerationTime().longValue()/1000;
            
            Instant generationInstant = 
                    IEEE1609DOT2EPOC.toInstant().plusMillis(generationTimeMillis);
            
            ZonedDateTime generatedAt = ZonedDateTime.ofInstant(generationInstant, ZoneId.of("UTC"));
            metadata.setGeneratedAt(generatedAt.toString());
        }
        
        metadata.setLogFileName(filePath.getFileName().toString());
        OdeBsmData bsmData = new OdeBsmData(metadata, payload);
        
        return bsmData;
        
    }

@Override
   public void decodeJsonAndPublish(InputStream is) throws IOException {
      String line = null;

      try (Scanner scanner = new Scanner(is)) {

         boolean empty = true;
         while (scanner.hasNextLine()) {
            empty = false;
            line = scanner.nextLine();

            J2735Bsm j2735Bsm = (J2735Bsm) JsonUtils.fromJson(line, J2735Bsm.class);
            OdeData odeBsm = createOdeBsmData(j2735Bsm, null);
            publish(odeBsm);
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
      logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicBsmRawJson(), msg);
      stringProducer.send(odeProperties.getKafkaTopicBsmRawJson(), null, msg);

   }

   @Override
   public void publish(byte[] msg) {
      logger.debug("Publishing byte array to {}", odeProperties.getKafkaTopicRawBsmPojo());

      byteArrayProducer.send(odeProperties.getKafkaTopicRawBsmPojo(), null, msg);
   }

   @Override
   protected Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }

}
