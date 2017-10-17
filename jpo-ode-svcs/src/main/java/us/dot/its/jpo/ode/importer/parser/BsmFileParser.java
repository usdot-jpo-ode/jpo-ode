package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.nio.ByteOrder;
import java.util.Arrays;

import us.dot.its.jpo.ode.importer.BsmSource;
import us.dot.its.jpo.ode.util.CodecUtils;

public class BsmFileParser extends LogFileParser {
   private static final int DIRECTION_LENGTH = 1;

   private BsmSource direction; // 0 for EV(Tx), 1 for RV(Rx)

   public BsmFileParser(long bundleId) {
      super(bundleId);
   }

   @Override
   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      try {
         super.parseFile(bis, fileName);

         // Step 1
         if (step == 1) {
            status = parseStep(bis, DIRECTION_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setDirection(BsmSource.values()[readBuffer[0]]);
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
         // Step 4
         if (step == 4) {
            if (getDirection() == BsmSource.EV_TX) {
               setValidSignature(true);
               step++;
            } else {
               status = parseStep(bis, VERIFICATION_STATUS_LENGTH);
               if (status != ParserStatus.COMPLETE)
                  return status;
               setValidSignature(readBuffer[0] == 0 ? false : true);
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

   public BsmSource getDirection() {
      return direction;
   }

   public BsmFileParser setDirection(BsmSource direction) {
      this.direction = direction;
      return this;
   }

}