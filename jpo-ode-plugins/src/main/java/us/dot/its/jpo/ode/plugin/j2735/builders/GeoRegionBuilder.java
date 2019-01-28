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

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.DdsGeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.OdeGeoRegion;

public class GeoRegionBuilder {

   private GeoRegionBuilder() {
      throw new UnsupportedOperationException();
   }

   public static OdeGeoRegion genericGeoRegion(JsonNode geoRegion) {

      return new OdeGeoRegion(
            Position3DBuilder.odePosition3D(geoRegion.get("nwCorner")),
            Position3DBuilder.odePosition3D(geoRegion.get("seCorner")));

   }

   public static DdsGeoRegion ddsGeoRegion(OdeGeoRegion serviceRegion) {
      DdsGeoRegion ddsRegion = new DdsGeoRegion();
      ddsRegion.setNwCorner(Position3DBuilder.dsrcPosition3D(serviceRegion.getNwCorner()));
      ddsRegion.setSeCorner(Position3DBuilder.dsrcPosition3D(serviceRegion.getSeCorner()));
      return ddsRegion ;
   }
}
