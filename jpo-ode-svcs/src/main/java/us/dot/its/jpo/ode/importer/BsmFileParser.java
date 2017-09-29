package us.dot.its.jpo.ode.importer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

import us.dot.its.jpo.ode.util.CodecUtils;

public class BsmFileParser implements LogFileParser {
   public static final int MAX_PAYLOAD_SIZE = 2048;

   private static final int DIRECTION_LENGTH = 1;
   private static final int UTC_TIME_IN_SEC_LENGTH = 4;
   private static final int MSEC_LENGTH = 2;
   private static final int VERIFICATION_STATUS_LENGTH = 1;
   private static final int LENGTH_LENGTH = 2;
   public static final int MAX_INPUT_BUFFER_SIZE = 
           MAX_PAYLOAD_SIZE + DIRECTION_LENGTH + UTC_TIME_IN_SEC_LENGTH
         + MSEC_LENGTH + VERIFICATION_STATUS_LENGTH + LENGTH_LENGTH;

   private byte[] readBuffer = new byte[MAX_INPUT_BUFFER_SIZE];
   private int step = 0;

   private int bundleId;
   private String filename;
   private BsmSource direction; // 0 for EV(Tx), 1 for RV(Rx)
   private long utctimeInSec;
   private short mSec;
   private boolean validSignature;
   private short length;
   private byte[] payload;

   public ParserStatus parse(BufferedInputStream bis, String fileName, int bundleId) throws LogFileParserException {
      ParserStatus status = ParserStatus.INIT;
      
      try {
         if (step == 0) {
            setFilename(fileName);
            setBundleId(bundleId);
            step++;
         }

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
            setUtctimeInSec(CodecUtils.bytesToInt(readBuffer, 0, UTC_TIME_IN_SEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
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
         throw new LogFileParserException("Error parsing " + fileName, e);
      }

      step = 0;
      status = ParserStatus.COMPLETE;

      return status;
   }

   public ParserStatus parseStep(BufferedInputStream bis, int length) throws LogFileParserException {
      try {
         int numBytes;
         if (bis.markSupported()) {
            bis.mark(length);
         }
         numBytes = bis.read(readBuffer, 0, length);
         if (numBytes < 0) {
            return ParserStatus.EOF;
         } else if (numBytes < length) {
            if (bis.markSupported()) {
               try {
                  bis.reset();
               } catch (IOException ioe) {
                  throw new LogFileParserException("Error reseting Input Stream to marked position", ioe);
               }
            }
            return ParserStatus.PARTIAL;
         } else {
            step++;
            return ParserStatus.COMPLETE;
         }
      } catch (Exception e) {
         throw new LogFileParserException("Error parsing step " + step, e);
      }
   }

   public int getBundleId() {
      return bundleId;
   }

   public void setBundleId(int bundleId) {
      this.bundleId = bundleId;
   }

   public int getStep() {
      return step;
   }

   public void setStep(int step) {
      this.step = step;
   }

   public String getFilename() {
      return filename;
   }

   public BsmFileParser setFilename(String filename) {
      this.filename = filename;
      return this;
   }

   public BsmSource getDirection() {
      return direction;
   }

   public BsmFileParser setDirection(BsmSource direction) {
      this.direction = direction;
      return this;
   }

   public long getUtctimeInSec() {
      return utctimeInSec;
   }

   public BsmFileParser setUtctimeInSec(long utctimeInSec) {
      this.utctimeInSec = utctimeInSec;
      return this;
   }

   public short getmSec() {
      return mSec;
   }

   public BsmFileParser setmSec(short mSec) {
      this.mSec = mSec;
      return this;
   }

   public boolean isValidSignature() {
      return validSignature;
   }

   public BsmFileParser setValidSignature(boolean validSignature) {
      this.validSignature = validSignature;
      return this;
   }

   public short getLength() {
      return length;
   }

   public BsmFileParser setLength(short length) {
      this.length = length;
      return this;
   }

   public byte[] getPayload() {
      return payload;
   }

   public BsmFileParser setPayload(byte[] payload) {
      this.payload = payload;
      return this;
   }
}
