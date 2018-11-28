package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.nio.ByteOrder;

import us.dot.its.jpo.ode.util.CodecUtils;

public class LocationParser extends LogFileParser {

   public static final int LOCATION_LAT_LENGTH = 4;
   public static final int LOCATION_LON_LENGTH = 4;
   public static final int LOCATION_ELEV_LENGTH = 4;
   public static final int LOCATION_SPEED_LENGTH = 2;
   public static final int LOCATION_HEADING_LENGTH = 2;

   protected LogLocation location;

   public LocationParser() {
      super();
   }

   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      ParserStatus status = ParserStatus.INIT;

      try {
         this.location = new LogLocation();
   
         // Step 1 - parse location.latitude
         if (getStep() == 0) {
            status = parseStep(bis, LOCATION_LAT_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            location.setLatitude(CodecUtils.bytesToInt(readBuffer, 0, LOCATION_LAT_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }
   
         // Step 2 - parse location.longitude
         if (getStep() == 1) {
            status = parseStep(bis, LOCATION_LON_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            location.setLongitude(CodecUtils.bytesToInt(readBuffer, 0, LOCATION_LON_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }
   
         // Step 3 - parse location.elevation
         if (getStep() == 2) {
            status = parseStep(bis, LOCATION_ELEV_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            location.setElevation(CodecUtils.bytesToInt(readBuffer, 0, LOCATION_ELEV_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }
   
         // Step 4 - parse location.speed
         if (getStep() == 3) {
            status = parseStep(bis, LOCATION_SPEED_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            location.setSpeed(CodecUtils.bytesToShort(readBuffer, 0, LOCATION_SPEED_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }
   
         // Step 5 - parse location.heading
         if (getStep() == 4) {
            status = parseStep(bis, LOCATION_HEADING_LENGTH);
            if (status != ParserStatus.COMPLETE)
               return status;
            location.setHeading(CodecUtils.bytesToShort(readBuffer, 0, LOCATION_HEADING_LENGTH, ByteOrder.LITTLE_ENDIAN));
         }
   
         resetStep();
         status = ParserStatus.COMPLETE;
      } catch (Exception e) {
         throw new FileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
      }


      return status;
   }

   public LogLocation getLocation() {
      return location;
   }

   public LocationParser setLocation(LogLocation location) {
      this.location = location;
      return this;
   }

}
