/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
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

   @Override
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

  @Override
  public void writeTo(OutputStream os) throws IOException {
    os.write(CodecUtils.intToBytes(location.getLatitude(), ByteOrder.LITTLE_ENDIAN));
    os.write(CodecUtils.intToBytes(location.getLongitude(), ByteOrder.LITTLE_ENDIAN));
    os.write(CodecUtils.intToBytes(location.getElevation(), ByteOrder.LITTLE_ENDIAN));
    os.write(CodecUtils.shortToBytes(location.getSpeed(), ByteOrder.LITTLE_ENDIAN));
    os.write(CodecUtils.shortToBytes(location.getHeading(), ByteOrder.LITTLE_ENDIAN));
  }

}
