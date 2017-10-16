package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.j2735.semi.GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;

public class GeoRegionBuilder {

   private GeoRegionBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735GeoRegion genericGeoRegion(JsonNode geoRegion) {

      return new J2735GeoRegion(Position3DBuilder.genericPosition3D(geoRegion.get("nwCorner")),
            Position3DBuilder.genericPosition3D(geoRegion.get("seCorner")));

   }

   public static GeoRegion geoRegion(J2735GeoRegion geoRegion) {

      return new GeoRegion(Position3DBuilder.position3D(geoRegion.getNwCorner()),
            Position3DBuilder.position3D(geoRegion.getSeCorner()));

   }
}
