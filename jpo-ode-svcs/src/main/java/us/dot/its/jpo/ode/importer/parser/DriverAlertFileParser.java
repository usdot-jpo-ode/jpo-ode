package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

import us.dot.its.jpo.ode.util.CodecUtils;

public class DriverAlertFileParser extends LogFileParser {

   private static final int TIM_LOCATION_LAT_LENGTH = 4;
   private static final int TIM_LOCATION_LON_LENGTH = 4;
   private static final int TIM_LOCATION_ELEV_LENGTH = 4;
   private static final int TIM_LOCATION_SPEED_LENGTH = 2;
   private static final int TIM_LOCATION_HEADING_LENGTH = 2;

   private int latitude;
   private int longitude;
   private int elevation;
   private short speed;
   private short heading;

   private byte[] alert;

   public DriverAlertFileParser(long bundleId) {
      super(bundleId);
   }

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
         throw new LogFileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
      }
      
      setStep(0);
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
            setStep(getStep() + 1);
            return ParserStatus.COMPLETE;
         }
      } catch (Exception e) {
         throw new LogFileParserException("Error parsing step " + getStep(), e);
      }
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

   public byte[] getAlert() {
      return alert;
   }

   public void setAlert(byte[] alert) {
      this.alert = alert;
   }

}
