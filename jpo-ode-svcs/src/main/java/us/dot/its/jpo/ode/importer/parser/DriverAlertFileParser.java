package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

import us.dot.its.jpo.ode.util.CodecUtils;

public class DriverAlertFileParser implements LogFileParser {

   private static final int TIM_LOCATION_TOTAL_LENGTH = 16;
   private static final int TIM_LOCATION_LAT_LENGTH = 4;
   private static final int TIM_LOCATION_LON_LENGTH = 4;
   private static final int TIM_LOCATION_ELEV_LENGTH = 4;
   private static final int TIM_LOCATION_SPEED_LENGTH = 2;
   private static final int TIM_LOCATION_HEADING_LENGTH = 2;

   private static final int TIM_UTC_TIME_IN_SEC_LENGTH = 4;
   private static final int TIM_MSEC_LENGTH = 2;
   private static final int TIM_ALERT_LENGTH_LENGTH = 2;
   private static final int TIM_MAX_TIM_PAYLOAD_LENGTH = 255;

   private static final int TIM_MAX_INPUT_BUFFER_SIZE = TIM_LOCATION_TOTAL_LENGTH + TIM_LOCATION_LAT_LENGTH
         + TIM_LOCATION_LON_LENGTH + TIM_LOCATION_ELEV_LENGTH + TIM_LOCATION_SPEED_LENGTH + TIM_LOCATION_HEADING_LENGTH
         + TIM_UTC_TIME_IN_SEC_LENGTH + TIM_MSEC_LENGTH + TIM_ALERT_LENGTH_LENGTH + TIM_MAX_TIM_PAYLOAD_LENGTH;

   private byte[] readBuffer = new byte[TIM_MAX_INPUT_BUFFER_SIZE];
   private int step = 0;

   private String filename;
   private int latitude;
   private int longitude;
   private int elevation;
   private short speed;
   private short heading;

   private long utcTimeInSec;
   private short mSec;
   private short length;
   private byte[] alert;

   public ParserStatus parse(BufferedInputStream bis, String fileName) throws LogFileParserException {

      ParserStatus status = ParserStatus.INIT;

      try {
         if (getStep() == 0) {
            setFilename(fileName);
            setStep(getStep() + 1);
         }

         // Step 1 - parse location.latitude
         if (getStep() == 1) {
            status = parseStep(bis, TIM_LOCATION_LAT_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setLatitude(CodecUtils.bytesToInt(readBuffer, 0, TIM_LOCATION_LAT_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 2 - parse location.longitude
         if (getStep() == 2) {
            status = parseStep(bis, TIM_LOCATION_LON_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setLongitude(CodecUtils.bytesToInt(readBuffer, 0, TIM_LOCATION_LON_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 3 - parse location.elevation
         if (getStep() == 3) {
            status = parseStep(bis, TIM_LOCATION_ELEV_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setElevation(CodecUtils.bytesToInt(readBuffer, 0, TIM_LOCATION_ELEV_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 4 - parse location.speed
         if (getStep() == 4) {
            status = parseStep(bis, TIM_LOCATION_SPEED_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setSpeed(CodecUtils.bytesToShort(readBuffer, 0, TIM_LOCATION_SPEED_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 5 - parse location.heading
         if (getStep() == 5) {
            status = parseStep(bis, TIM_LOCATION_HEADING_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setHeading(CodecUtils.bytesToShort(readBuffer, 0, TIM_LOCATION_HEADING_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 6 - parse utcTimeInSec
         if (getStep() == 6) {
            status = parseStep(bis, TIM_UTC_TIME_IN_SEC_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setUtcTimeInSec(CodecUtils.bytesToInt(readBuffer, 0, TIM_UTC_TIME_IN_SEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 7 - parse mSec
         if (getStep() == 7) {
            status = parseStep(bis, TIM_MSEC_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setmSec(CodecUtils.bytesToShort(readBuffer, 0, TIM_MSEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 8 - parse alert length
         if (getStep() == 8) {
            status = parseStep(bis, TIM_ALERT_LENGTH_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            setLength(CodecUtils.bytesToShort(readBuffer, 0, TIM_ALERT_LENGTH_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }

         // Step 9 - copy alert
         if (getStep() == 9) {
            status = parseStep(bis, getLength());
            if (status != ParserStatus.COMPLETE)
               return status;
            setAlert(Arrays.copyOf(readBuffer, getLength()));
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

   public int getmSec() {
      return mSec;
   }

   public void setmSec(short mSec) {
      this.mSec = mSec;
   }

   public int getLength() {
      return length;
   }

   public void setLength(short length) {
      this.length = length;
   }

   public byte[] getAlert() {
      return alert;
   }

   public void setAlert(byte[] alert) {
      this.alert = alert;
   }

   public int getStep() {
      return step;
   }

   public void setStep(int step) {
      this.step = step;
   }
}
