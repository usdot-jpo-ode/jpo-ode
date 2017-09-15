package us.dot.its.jpo.ode.coder;

import java.nio.ByteBuffer;

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
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.MessageFrame;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.OdeTravelerInformationData;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.security.SecurityManager;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;

public class TimDecoderHelper {

   private static final Logger logger = LoggerFactory.getLogger(TimDecoderHelper.class);
   private PERUnalignedCoder asnDecoder;
   private Oss1609dot2Coder ieee1609dotCoder;
   private RawBsmMfSorter rawBsmMfSorter;

   public TimDecoderHelper() {
      this.asnDecoder = J2735.getPERUnalignedCoder();
      this.ieee1609dotCoder = new Oss1609dot2Coder();
      this.rawBsmMfSorter = new RawBsmMfSorter(new OssJ2735Coder());
   }

   public OdeData decode(RxMsgFileParser fileParser, SerialId serialId) throws Exception {

      Ieee1609Dot2Data ieee1609dot2Data = ieee1609dotCoder.decodeIeee1609Dot2DataBytes(fileParser.getPayload());
      OdeObject tim = null;
      OdeData odeTimData = null;
      IEEE1609p2Message message = null;

      TravelerInformation timMessage = new TravelerInformation();
      BasicSafetyMessage bsmMessage = new BasicSafetyMessage();
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
                  logger.info("THIS SHOULD BE A TIM: {}", HexUtils.toHexString(unsecuredDataContent));
                  asnDecoder.decode(ByteBuffer.wrap(unsecuredDataContent), mf);
               }
            } catch (DecodeNotSupportedException | DecodeFailedException e2) {
               logger.error("Message failed to decode as TIM, trying BSM.");
               asnDecoder.decode(ByteBuffer.wrap(unsecuredDataContent), mf);
               rawBsmMfSorter.decodeBsm(unsecuredDataContent);

            }
         } catch (DecodeNotSupportedException | DecodeFailedException e) {
            logger.debug("Failed to decode message as TIM, trying BSM.", e);
            asnDecoder.decode(ByteBuffer.wrap(message.getPayload()), mf);
         }
      } else {
         asnDecoder.decode(ByteBuffer.wrap(fileParser.getPayload()), mf);
      }

      logger.debug("Extracted a TIM: " + timMessage);

      // TODO - convert the tim message
      tim = new J2735TravelerInformationMessage();

      if (tim != null) {
         logger.debug("Decoded TIM successfully, creating OdeTimData object.");

         OdeMsgPayload timDataPayload = new OdeMsgPayload(tim);
         OdeMsgMetadata timMetadata = new OdeMsgMetadata(timDataPayload); // TODO
                                                                          // create
                                                                          // and
                                                                          // populate
                                                                          // tim
                                                                          // metdata
         odeTimData = new OdeTravelerInformationData(timMetadata, timDataPayload);

      } else {
         logger.debug("Failed to decode TIM.");
      }
      return odeTimData;
   }
}
