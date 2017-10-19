package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.cv.security.cert.CertificateException;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import gov.usdot.cv.security.msg.MessageException;
import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.importer.parser.TimLogFileParser;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.*;
import us.dot.its.jpo.ode.model.*;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.plugin.j2735.oss.*;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssMessageFrame.OssMessageFrameException;
import us.dot.its.jpo.ode.security.SecurityManager;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class TimDecoderHelper {

   private static final Logger logger = LoggerFactory.getLogger(TimDecoderHelper.class);
   private static final int DSRC_MSG_ID_BSM = 20;
   private static final int DSRC_MSG_ID_TIM = 31;
   private PERUnalignedCoder asnDecoder;
   private Oss1609dot2Coder ieee1609dotCoder;
   private RawBsmMfSorter rawBsmMfSorter;

   public TimDecoderHelper() {
      this.asnDecoder = J2735.getPERUnalignedCoder();
      this.ieee1609dotCoder = new Oss1609dot2Coder();
      this.rawBsmMfSorter = new RawBsmMfSorter(new OssJ2735Coder());
   }

   public OdeData decode(TimLogFileParser fileParser, SerialId serialId) throws Exception {

      Ieee1609Dot2Data ieee1609dot2Data = ieee1609dotCoder.decodeIeee1609Dot2DataBytes(fileParser.getPayload());
      OdeData odeTimData = null;
      IEEE1609p2Message message = null;

      MessageFrame mf = new MessageFrame();

      if (ieee1609dot2Data != null) {
         logger.debug("Attempting to decode as Ieee1609Dot2Data.");
         try {
            message = IEEE1609p2Message.convert(ieee1609dot2Data);
            if (message != null) {
               try {
                  SecurityManager.validateGenerationTime(message);
               } catch (SecurityManagerException e) {
                  logger.error("Error validating message.", e);
               }

               asnDecoder.decode(ByteBuffer.wrap(message.getPayload()), mf);
            }
         } catch (MessageException | EncodeFailedException | CertificateException | EncodeNotSupportedException
               | CryptoException e) {
            logger.debug("Message does not have a valid signature. Assuming it is unsigned message...", e);
            byte[] unsecuredDataContent = Ieee1609ContentValidator.getUnsecuredData(ieee1609dot2Data.getContent());
            try {
               if (unsecuredDataContent != null) {
                  logger.debug("Extracted TIM: {}", HexUtils.toHexString(unsecuredDataContent));
                  asnDecoder.decode(ByteBuffer.wrap(unsecuredDataContent), mf);
               }
            } catch (DecodeNotSupportedException | DecodeFailedException e2) {
               logger.debug("Message failed to decode as TIM, trying BSM.");
               asnDecoder.decode(ByteBuffer.wrap(unsecuredDataContent), mf);
               rawBsmMfSorter.decodeBsm(unsecuredDataContent);

            }
         } catch (DecodeNotSupportedException | DecodeFailedException e) {
            logger.debug("Failed to decode message as TIM, trying BSM.", e);
            if (message != null) {
               asnDecoder.decode(ByteBuffer.wrap(message.getPayload()), mf);
            }
         }
      } else {
         asnDecoder.decode(ByteBuffer.wrap(fileParser.getPayload()), mf);
      }

      if (mf != null) {
         if (mf.getMessageId().intValue() == DSRC_MSG_ID_TIM) {
            
            TravelerInformation tim;
            if (mf.value.getDecodedValue() != null) {
               tim = (TravelerInformation) mf.value.getDecodedValue();
            } else if (mf.value.getEncodedValueAsStream() != null) {
               tim = new TravelerInformation();
               try {
                  asnDecoder.decode(mf.value.getEncodedValueAsStream(), tim);
               } catch (DecodeFailedException | DecodeNotSupportedException e) {
                  throw new OssMessageFrameException("Error decoding OpenType value", e);
               }
            } else {
               throw new OssMessageFrameException("No OpenType value");
            }

            OdeTimPayload timPayload = new OdeTimPayload(OssTravelerInformation.genericTim(tim));

            OdeTimMetadata timMetadata = new OdeTimMetadata(timPayload);

            timMetadata.setReceivedAt(DateTimeUtils.now());
            timMetadata.setSerialId(serialId);
            timMetadata.setLogFileName(fileParser.getFilename());
            timMetadata.setRecordType(fileParser.getRecordType().name());
            
            OdeTimSpecificMetadata timSpecificMetadata = new OdeTimSpecificMetadata(
                  new OdeTimSpecificMetadataLocation(
                        OssLatitude.genericLatitude(new Latitude(fileParser.getLocation().getLatitude())).toString(),
                        OssLongitude.genericLongitude(new Longitude(fileParser.getLocation().getLongitude())).toString(),
                        OssElevation.genericElevation(new Elevation(fileParser.getLocation().getElevation())).toString(),
                        OssSpeedOrVelocity.genericSpeed(new Speed(fileParser.getLocation().getSpeed())).toString(),
                        OssHeading.genericHeading(new Heading(fileParser.getLocation().getHeading())).toString()), null);
            
            if (fileParser instanceof RxMsgFileParser) {
               timSpecificMetadata.setRxSource( ((RxMsgFileParser) fileParser).getRxSource());
            } 
            
            timMetadata.setReceivedMessageDetails(timSpecificMetadata);
            
            ZonedDateTime generatedAt;
            if (message != null) {
               Date ieeeGenTime = message.getGenerationTime();

               logger.debug("Generated time: {}", ieeeGenTime);

               if (ieeeGenTime != null) {
                  generatedAt = DateTimeUtils.isoDateTime(ieeeGenTime);
               } else {
                  generatedAt = getGeneratedAt(fileParser);
               }
               timMetadata.setRecordGeneratedAt(generatedAt.toString());
               timMetadata.setValidSignature(true);
            } else {
               logger.debug("Message does not contain time");
               timMetadata.setRecordGeneratedAt(getGeneratedAt(fileParser).toString());
               timMetadata.setValidSignature(fileParser.isValidSignature());
            }
            timMetadata.setRecordGeneratedBy(GeneratedBy.OBU);

            odeTimData = new OdeTimData(timMetadata, timPayload);

         } else if (mf.getMessageId().intValue() == DSRC_MSG_ID_BSM) {
             logger.debug("We have a BSM: " + mf.messageId);

             BasicSafetyMessage bsm;
            if (mf.value.getDecodedValue() != null) {
               bsm = (BasicSafetyMessage) mf.value.getDecodedValue();
            } else if (mf.value.getEncodedValueAsStream() != null) {
               bsm = new BasicSafetyMessage();
               try {
                  asnDecoder.decode(mf.value.getEncodedValueAsStream(), bsm);
               } catch (DecodeFailedException | DecodeNotSupportedException e) {
                  throw new OssMessageFrameException("Error decoding OpenType value", e);
               }
            } else {
               throw new OssMessageFrameException("No OpenType value");
            }

            OdeBsmPayload bsmPayload = new OdeBsmPayload(OssBsm.genericBsm(bsm));

             OdeTimMetadata bsmMetadata = new OdeTimMetadata(bsmPayload);

             bsmMetadata.setReceivedAt(DateTimeUtils.now());
             bsmMetadata.setSerialId(serialId);
             bsmMetadata.setLogFileName(fileParser.getFilename());

             OdeTimSpecificMetadata bsmSpecificMetadata = new OdeTimSpecificMetadata(
                    new OdeTimSpecificMetadataLocation(
                            OssLatitude.genericLatitude(new Latitude(fileParser.getLocation().getLatitude())).toString(),
                            OssLongitude.genericLongitude(new Longitude(fileParser.getLocation().getLongitude())).toString(),
                            OssElevation.genericElevation(new Elevation(fileParser.getLocation().getElevation())).toString(),
                            OssSpeedOrVelocity.genericSpeed(new Speed(fileParser.getLocation().getSpeed())).toString(),
                            OssHeading.genericHeading(new Heading(fileParser.getLocation().getHeading())).toString()), null);

            if (fileParser instanceof RxMsgFileParser) {
                bsmSpecificMetadata.setRxSource( ((RxMsgFileParser) fileParser).getRxSource());
            }

             bsmMetadata.setReceivedMessageDetails(bsmSpecificMetadata);

            ZonedDateTime generatedAt;
            if (message != null) {
               Date ieeeGenTime = message.getGenerationTime();

               logger.debug("Generated time: {}", ieeeGenTime);

               if (ieeeGenTime != null) {
                  generatedAt = DateTimeUtils.isoDateTime(ieeeGenTime);
               } else {
                  generatedAt = getGeneratedAt(fileParser);
               }
                bsmMetadata.setRecordGeneratedAt(generatedAt.toString());
                bsmMetadata.setValidSignature(true);
            } else {
               logger.debug("Message does not contain time");
                bsmMetadata.setRecordGeneratedAt(getGeneratedAt(fileParser).toString());
                bsmMetadata.setValidSignature(fileParser.isValidSignature());
            }

            odeTimData = new OdeTimData(bsmMetadata, bsmPayload);
         } else {
            throw new IOException("Unknown message ID extracted: " + mf.messageId);
         }
      }
      return odeTimData;
   }

   private ZonedDateTime getGeneratedAt(TimLogFileParser fileParser) {
      return DateTimeUtils.isoDateTime(fileParser.getUtcTimeInSec() * 1000 + fileParser.getmSec());
   }
}
