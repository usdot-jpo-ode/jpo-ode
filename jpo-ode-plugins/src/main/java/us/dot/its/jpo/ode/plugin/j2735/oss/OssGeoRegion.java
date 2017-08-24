package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.semi.GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;

public class OssGeoRegion {

   private OssGeoRegion() {
      throw new UnsupportedOperationException();
   }

   public static J2735GeoRegion genericGeoRegion(GeoRegion geoRegion) {

      return new J2735GeoRegion(OssPosition3D.genericPosition3D(geoRegion.getNwCorner()),
            OssPosition3D.genericPosition3D(geoRegion.getSeCorner()));

   }

   public static GeoRegion geoRegion(J2735GeoRegion geoRegion) {

      return new GeoRegion(OssPosition3D.position3D(geoRegion.getNwCorner()),
            OssPosition3D.position3D(geoRegion.getSeCorner()));

   }
}
