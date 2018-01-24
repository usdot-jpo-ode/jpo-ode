package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.util.CodecUtils;

public class BsmLogFileParser extends LogFileParser {
   private static final Logger logger = LoggerFactory.getLogger(BsmLogFileParser.class);

   private static final int DIRECTION_LENGTH = 1;

   private BsmSource bsmSource; // 0 for EV(Tx), 1 for RV(Rx)

   public BsmLogFileParser(long bundleId) {
      super(bundleId);
   }

   @Override
   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      try {
         super.parseFile(bis, fileName);
         status = ParserStatus.INIT;

         // Step 1
         if (step == 1) {
            status = parseStep(bis, DIRECTION_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setBsmSource(readBuffer[0]);
         }
         // Step 2
         if (step == 2) {
            status = parseStep(bis, UTC_TIME_IN_SEC_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setUtcTimeInSec(CodecUtils.bytesToInt(readBuffer, 0, UTC_TIME_IN_SEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }
         // Step 3
         if (step == 3) {
            status = parseStep(bis, MSEC_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setmSec(CodecUtils.bytesToShort(readBuffer, 0, MSEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }
         // Step 4 parse SecurityResultCode
         if (step == 4) {
            if (getBsmSource() == BsmSource.EV) {
               setSecurityResultCode(SecurityResultCode.unknown);
               step++;
            } else {
               status = parseStep(bis, VERIFICATION_STATUS_LENGTH);
               if (status != ParserStatus.COMPLETE)
                  return status;
               setSecurityResultCode(readBuffer[0]);
            }
         }
         // Step 5
         if (step == 5) {
            status = parseStep(bis, LENGTH_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setLength(CodecUtils.bytesToShort(readBuffer, 0, LENGTH_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }
         // Step 6
         if (step == 6) {
            status = parseStep(bis, getLength());
            if (status != ParserStatus.COMPLETE)
               return status;
            setPayload(Arrays.copyOf(readBuffer, getLength()));
         }
      } catch (Exception e) {
         throw new FileParserException("Error parsing " + fileName, e);
      }

      step = 0;
      status = ParserStatus.COMPLETE;

      return status;
   }

   public BsmSource getBsmSource() {
      return bsmSource;
   }

   public void setBsmSource(BsmSource bsmSource) {
      this.bsmSource = bsmSource;
   }

   public void setBsmSource(byte code) {
      try {
         setBsmSource(BsmSource.values()[code]);
      } catch (Exception e) {
         logger.error("Invalid BsmSource: {}. Valid values are {}-{} inclusive", 
            code, 0, BsmSource.values());
         setBsmSource(BsmSource.unknown);
      }
   }

}