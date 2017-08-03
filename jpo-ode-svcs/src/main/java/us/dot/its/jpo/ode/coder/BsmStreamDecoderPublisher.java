package us.dot.its.jpo.ode.coder;

import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.security.SecurityManager;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.J2735BsmSerializer;

public class BsmStreamDecoderPublisher extends AbstractStreamDecoderPublisher {

   private Path filePath;
   private SerialId serialId;
   private static AtomicInteger bundleId = new AtomicInteger(1);

   public BsmStreamDecoderPublisher(OdeProperties properties, SerialId serId, Path filePath) {
      super(properties, J2735BsmSerializer.class.getName());
      this.filePath = filePath;
      this.serialId = serId;
      this.serialId.setBundleId(bundleId.incrementAndGet());
   }

   @Override
   public OdeData decode(String hexEncodedData) throws Exception {
       return decode(CodecUtils.fromHex(hexEncodedData));
   }

   @Override
   public OdeData decode(InputStream is) throws Exception {
        Ieee1609Dot2Data ieee1609dot2Data = 
               ieee1609dotCoder.decodeIeee1609Dot2DataStream(is);

        OdeObject bsm = null;
        OdeData odeBsmData = null;
        IEEE1609p2Message message = null;
        if (ieee1609dot2Data != null) {
            try {
                message = IEEE1609p2Message.convert(ieee1609dot2Data);

                bsm = getBsmPayload(message);
            } catch (Exception e) {
                logger.debug("Message does not have a valid signature");
                bsm = decodeBsm(ieee1609dot2Data
                    .getContent()
                    .getSignedData()
                    .getTbsData()
                    .getPayload()
                    .getData()
                    .getContent()
                    .getUnsecuredData()
                    .byteArrayValue());
            }
        } else { // probably raw BSM or MessageFrame
            bsm = decodeBsm(is);
        }
        
        if (bsm != null) {
           odeBsmData = createOdeBsmData((J2735Bsm) bsm, message);
        }

        return odeBsmData;
   }

private OdeObject getBsmPayload(IEEE1609p2Message message) {
    try {
        SecurityManager.validateGenerationTime(message);
    } catch (SecurityManagerException e) {
        logger.error("Error validating message.",  e);
    }

    return decodeBsm(message.getPayload());
}

   @Override
   public OdeData decode(byte[] data) throws Exception{
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
            odeBsmData = createOdeBsmData((J2735Bsm) bsm, message);
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
        
        if (msg.getMetadata() != null && msg.getMetadata().getReceivedAt() != null)
        try {
            long latency = DateTimeUtils.difference(
                DateTimeUtils.isoDateTime(msg.getMetadata().getReceivedAt()), 
                ZonedDateTime.now());
            odeBsm.getMetadata().setLatency(latency);
        } catch (ParseException e) {
            logger.error("Error converting ISO timestamp", e);
        }
        
        logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicRawBsmPojo(), odeBsm.getPayload().getData());
        objectProducer.send(odeProperties.getKafkaTopicRawBsmPojo(), null, odeBsm.getPayload().getData());

        logger.debug("Publishing to {}: {}", odeProperties.getKafkaTopicOdeBsmPojo(), odeBsm.toJson());
        objectProducer.send(odeProperties.getKafkaTopicOdeBsmPojo(), null, odeBsm);
    }

   public OdeBsmData createOdeBsmData(J2735Bsm rawBsm, IEEE1609p2Message message) {
        OdeBsmPayload payload = new OdeBsmPayload(rawBsm);
        
        OdeBsmMetadata metadata = new OdeBsmMetadata(payload);
        metadata.setSerialId(serialId);
        
        if (message != null) {
            ZonedDateTime generatedAt = DateTimeUtils.isoDateTime(message.getGenerationTime());
            metadata.setGeneratedAt(generatedAt.toString());
            
            metadata.setValidSignature(true);
        }
        
        metadata.getSerialId().addRecordId(1);
        metadata.setLogFileName(filePath.getFileName().toString());
        OdeBsmData bsmData = new OdeBsmData(metadata, payload);
        
        return bsmData;
        
    }

@Override
   public void decodeJsonAndPublish(InputStream is) throws Exception {
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
            throw new Exception("Empty file received");
         }
      } catch (Exception e) {
         EventLogger.logger.info("Error occurred while decoding message: {}", line);
         throw new Exception("Error decoding data: " + line, e);
      }
   }

   @Override
   protected Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }

}
