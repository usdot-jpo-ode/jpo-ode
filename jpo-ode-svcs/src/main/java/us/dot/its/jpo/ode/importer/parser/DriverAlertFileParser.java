package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.nio.ByteOrder;
import java.util.Arrays;

import us.dot.its.jpo.ode.util.CodecUtils;

public class DriverAlertFileParser extends TimLogFileParser {

   private byte[] alert;

   public DriverAlertFileParser(long bundleId) {
      super(bundleId);
   }

   @Override
   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      try {
         status = super.parseFile(bis, fileName);

         if (status != ParserStatus.COMPLETE)
            return status;

         // Step 6 - parse utcTimeInSec
         if (getStep() == 6) {
            status = parseStep(bis, UTC_TIME_IN_SEC_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setUtcTimeInSec(CodecUtils.bytesToInt(readBuffer, 0, UTC_TIME_IN_SEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 7 - parse mSec
         if (getStep() == 7) {
            status = parseStep(bis, MSEC_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setmSec(CodecUtils.bytesToShort(readBuffer, 0, MSEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 8 - parse alert length
         if (getStep() == 8) {
            status = parseStep(bis, LENGTH_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setLength(CodecUtils.bytesToShort(readBuffer, 0, LENGTH_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 9 - copy alert
         if (getStep() == 9) {
            status = parseStep(bis, getLength());
            if (status != ParserStatus.COMPLETE)
               return status;
            setAlert(Arrays.copyOf(readBuffer, getLength()));
         }

      } catch (Exception e) {
         throw new FileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
      }

      setStep(0);
      status = ParserStatus.COMPLETE;

      return status;
   }

   public byte[] getAlert() {
      return alert;
   }

   public void setAlert(byte[] alert) {
      this.alert = alert;
   }

}
