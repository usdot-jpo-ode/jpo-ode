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
package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.util.JsonUtils;

public class Position3DBuilder {
   
   private static final String LATITUDE = "latitude";
   private static final String LONGITUDE = "longitude";
   private static final String ELEVATION = "elevation";

   private Position3DBuilder() {
      throw new UnsupportedOperationException();
   }
   
   public static DsrcPosition3D dsrcPosition3D(JsonNode pos) {
      Long latitude = pos.get("lat").asLong();
      Long longitude = pos.get("long").asLong();
      Long elevation = null;
      if (pos.get(ELEVATION) != null) {
         elevation = pos.get(ELEVATION).asLong();
      }

      return new DsrcPosition3D(latitude, longitude, elevation);

   }

   public static OdePosition3D odePosition3D(DsrcPosition3D dsrcPos) {
      return odePosition3D(dsrcPos.getLatitude(), dsrcPos.getLongitude(), dsrcPos.getElevation());
   }

   private static OdePosition3D odePosition3D(Long latitude, Long longitude, Long elevation) {
      OdePosition3D jpos = new OdePosition3D();

      if (latitude != null) {
         jpos.setLatitude(LatitudeBuilder.genericLatitude(latitude));
      }

      if (longitude != null) {
         jpos.setLongitude(LongitudeBuilder.genericLongitude(longitude));
      }

      if (elevation != null) {
         jpos.setElevation(ElevationBuilder.genericElevation(elevation));
      }

      return jpos;
   }

   public static OdePosition3D odePosition3D(JsonNode jpos) {

      BigDecimal latitude = null;
      if (jpos.has(LATITUDE)) {
        latitude = JsonUtils.decimalValue(jpos.get(LATITUDE));
      }
      
      BigDecimal longitude = null;
      if (jpos.has(LONGITUDE)) {
        longitude = JsonUtils.decimalValue(jpos.get(LONGITUDE));
      }
      
      BigDecimal elevation = null;
      if (jpos.has(ELEVATION)) {
        elevation = JsonUtils.decimalValue(jpos.get(ELEVATION));
      }

      return new OdePosition3D(latitude, longitude, elevation);
   }

   private static DsrcPosition3D dsrcPosition3D(BigDecimal latitude, BigDecimal longitude, BigDecimal elevation) {
      DsrcPosition3D dPos = new DsrcPosition3D();

      if (latitude != null) {
         dPos.setLatitude(LatitudeBuilder.j2735Latitude(latitude));
      }

      if (longitude != null) {
         dPos.setLongitude(LongitudeBuilder.j2735Longitude(longitude));
      }

      if (elevation != null) {
         dPos.setElevation(ElevationBuilder.j2735Elevation(elevation));
      }

      return dPos;
   }

   public static DsrcPosition3D dsrcPosition3D(OdePosition3D odePosition3D) {
      return dsrcPosition3D(odePosition3D.getLatitude(), odePosition3D.getLongitude(), odePosition3D.getElevation());
   }

}
