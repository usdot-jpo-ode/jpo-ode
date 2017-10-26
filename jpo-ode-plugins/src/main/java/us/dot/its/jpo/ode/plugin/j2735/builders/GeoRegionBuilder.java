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
      // TODO Auto-generated method stub
      return null;
   }
}
