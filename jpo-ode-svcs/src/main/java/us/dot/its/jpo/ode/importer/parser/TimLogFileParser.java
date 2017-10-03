package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.nio.ByteOrder;

import us.dot.its.jpo.ode.util.CodecUtils;

public abstract class TimLogFileParser extends LogFileParser {

   public static final int LOCATION_LAT_LENGTH = 4;
   public static final int LOCATION_LON_LENGTH = 4;
   public static final int LOCATION_ELEV_LENGTH = 4;
   public static final int LOCATION_SPEED_LENGTH = 2;
   public static final int LOCATION_HEADING_LENGTH = 2;

   protected TimLogLocation location;

   public TimLogFileParser(long bundleId) {
      super(bundleId);
   }

   @Override
   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

      status = super.parseFile(bis, fileName);

      this.location = new TimLogLocation();

      // Step 1 - parse location.latitude
      if (getStep() == 1) {
         status = parseStep(bis, LOCATION_LAT_LENGTH);
         if (status != ParserStatus.COMPLETE)
            return status;
         location.setLatitude(CodecUtils.bytesToInt(readBuffer, 0, LOCATION_LAT_LENGTH, ByteOrder.LITTLE_ENDIAN));
      }

      // Step 2 - parse location.longitude
      if (getStep() == 2) {
         status = parseStep(bis, LOCATION_LON_LENGTH);
         if (status != ParserStatus.COMPLETE)
            return status;
         location.setLongitude(CodecUtils.bytesToInt(readBuffer, 0, LOCATION_LON_LENGTH, ByteOrder.LITTLE_ENDIAN));
      }

      // Step 3 - parse location.elevation
      if (getStep() == 3) {
         status = parseStep(bis, LOCATION_ELEV_LENGTH);
         if (status != ParserStatus.COMPLETE)
            return status;
         location.setElevation(CodecUtils.bytesToInt(readBuffer, 0, LOCATION_ELEV_LENGTH, ByteOrder.LITTLE_ENDIAN));
      }

      // Step 4 - parse location.speed
      if (getStep() == 4) {
         status = parseStep(bis, LOCATION_SPEED_LENGTH);
         if (status != ParserStatus.COMPLETE)
            return status;
         location.setSpeed(CodecUtils.bytesToShort(readBuffer, 0, LOCATION_SPEED_LENGTH, ByteOrder.LITTLE_ENDIAN));
      }

      // Step 5 - parse location.heading
      if (getStep() == 5) {
         status = parseStep(bis, LOCATION_HEADING_LENGTH);
         if (status != ParserStatus.COMPLETE)
            return status;
         location.setHeading(CodecUtils.bytesToShort(readBuffer, 0, LOCATION_HEADING_LENGTH, ByteOrder.LITTLE_ENDIAN));
      }

      return status;
   }

   public TimLogLocation getLocation() {
      return location;
   }

   public void setLocation(TimLogLocation location) {
      this.location = location;
   }

}
