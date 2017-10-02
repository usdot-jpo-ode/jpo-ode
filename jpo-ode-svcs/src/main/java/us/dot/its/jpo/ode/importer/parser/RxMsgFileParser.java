package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.util.CodecUtils;

/*
typedef struct _receivedMsgRecord {
 location curLocation;
 uint32_t utctimeInSec;
 uint16_t mSec;
 rxSource rxFrom;
 int8_t verificationStatus;
 uint16_t length;
 uint8_t payload[MAX_PAYLOAD_SIZE]; //LEAR: RAW 1609.2 format of TIM
 } __attribute__((__packed__)) receivedMsgRecord;
 */
public class RxMsgFileParser implements LogFileParser {

   private static final int MAX_PAYLOAD_SIZE = 2302;

   private static final int LOCATION_TOTAL_LENGTH = 16;
   private static final int LOCATION_LAT_LENGTH = 4;
   private static final int LOCATION_LON_LENGTH = 4;
   private static final int LOCATION_ELEV_LENGTH = 4;
   private static final int LOCATION_SPEED_LENGTH = 2;
   private static final int LOCATION_HEADING_LENGTH = 2;

   private static final int UTC_TIME_IN_SEC_LENGTH = 4;
   private static final int MSEC_LENGTH = 2;
   private static final int RX_SOURCE_LENGTH = 4; // TODO - this is a C
                                                  // enumeration, size is
                                                  // compiler-dependent

   private static final int VERIFICATION_STATUS_LENGTH = 1;
   private static final int LENGTH_LENGTH = 2;
   public static final int MAX_INPUT_BUFFER_SIZE = LOCATION_TOTAL_LENGTH + LOCATION_LAT_LENGTH + LOCATION_LON_LENGTH
         + LOCATION_ELEV_LENGTH + LOCATION_SPEED_LENGTH + LOCATION_HEADING_LENGTH + UTC_TIME_IN_SEC_LENGTH + MSEC_LENGTH
         + RX_SOURCE_LENGTH + LENGTH_LENGTH + MAX_PAYLOAD_SIZE;

   private byte[] readBuffer = new byte[MAX_INPUT_BUFFER_SIZE];
   private int step = 0;

   private String filename;
   private int latitude;
   private int longitude;
   private int elevation;
   private short speed;
   private short heading;

   private long utcTimeInSec;
   private short mSec;
   private RxSource rxSource;
   private boolean validSignature;
   private short length;
   private byte[] payload;

   public ParserStatus parse(BufferedInputStream bis, String fileName) throws LogFileParserException {

      ParserStatus status = ParserStatus.INIT;

      try {
         if (getStep() == 0) {
            setFilename(fileName);
            setStep(getStep() + 1);
         }

         // Step 1 - parse location.latitude
         if (getStep() == 1) {
            status = parseStep(bis, LOCATION_LAT_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setLatitude(CodecUtils.bytesToInt(readBuffer, 0, LOCATION_LAT_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 2 - parse location.longitude
         if (getStep() == 2) {
            status = parseStep(bis, LOCATION_LON_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setLongitude(CodecUtils.bytesToInt(readBuffer, 0, LOCATION_LON_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 3 - parse location.elevation
         if (getStep() == 3) {
            status = parseStep(bis, LOCATION_ELEV_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setElevation(CodecUtils.bytesToInt(readBuffer, 0, LOCATION_ELEV_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 4 - parse location.speed
         if (getStep() == 4) {
            status = parseStep(bis, LOCATION_SPEED_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setSpeed(CodecUtils.bytesToShort(readBuffer, 0, LOCATION_SPEED_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 5 - parse location.heading
         if (getStep() == 5) {
            status = parseStep(bis, LOCATION_HEADING_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setHeading(CodecUtils.bytesToShort(readBuffer, 0, LOCATION_HEADING_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

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

         // Step 8 - parse rxSource
         if (getStep() == 8) {
            status = parseStep(bis, RX_SOURCE_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setRxSource(RxSource.values()[readBuffer[0]]);
         }

         // Step 9 - parse verification status
         if (getStep() == 9) {
            status = parseStep(bis, VERIFICATION_STATUS_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setValidSignature(readBuffer[0] == 0 ? false : true);
         }

         // Step 10 - parse payload length
         if (getStep() == 10) {
            status = parseStep(bis, LENGTH_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setLength(CodecUtils.bytesToShort(readBuffer, 0, LENGTH_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }
         // Step 11 - copy payload bytes
         if (getStep() == 11) {
            status = parseStep(bis, getLength());
            if (status != ParserStatus.COMPLETE)
               return status;
            setPayload(Arrays.copyOf(readBuffer, getLength()));
         }
      } catch (Exception e) {
         throw new LogFileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
      }

      setStep(0);
      status = ParserStatus.COMPLETE;

      return status;

   }

   private ParserStatus parseStep(BufferedInputStream bis, int length) throws LogFileParserException {
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
            setStep(getStep() + 1);
            return ParserStatus.COMPLETE;
         }
      } catch (Exception e) {
         throw new LogFileParserException("Error parsing step " + getStep(), e);
      }
   }

   public String getFilename() {
      return filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public int getLatitude() {
      return latitude;
   }

   public void setLatitude(int latitude) {
      this.latitude = latitude;
   }

   public int getLongitude() {
      return longitude;
   }

   public void setLongitude(int longitude) {
      this.longitude = longitude;
   }

   public int getElevation() {
      return elevation;
   }

   public void setElevation(int elevation) {
      this.elevation = elevation;
   }

   public short getSpeed() {
      return speed;
   }

   public void setSpeed(short speed) {
      this.speed = speed;
   }

   public short getHeading() {
      return heading;
   }

   public void setHeading(short heading) {
      this.heading = heading;
   }

   public long getUtcTimeInSec() {
      return utcTimeInSec;
   }

   public void setUtcTimeInSec(int utcTimeInSec) {
      this.utcTimeInSec = utcTimeInSec;
   }

   public short getmSec() {
      return mSec;
   }

   public void setmSec(short mSec) {
      this.mSec = mSec;
   }

   public RxSource getRxSource() {
      return rxSource;
   }

   public void setRxSource(RxSource rxSource) {
      this.rxSource = rxSource;
   }

   public boolean isValidSignature() {
      return validSignature;
   }

   public void setValidSignature(boolean validSignature) {
      this.validSignature = validSignature;
   }

   public short getLength() {
      return length;
   }

   public void setLength(short length) {
      this.length = length;
   }

   public byte[] getPayload() {
      return payload;
   }

   public void setPayload(byte[] payload) {
      this.payload = payload;
   }

   public int getStep() {
      return step;
   }

   public void setStep(int step) {
      this.step = step;
   }

}
