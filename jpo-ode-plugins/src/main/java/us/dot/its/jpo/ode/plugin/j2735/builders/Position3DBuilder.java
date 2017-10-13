package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class Position3DBuilder {
   
   public static J2735Position3D genericPosition3D(JsonNode pos) {
      J2735Position3D jpos = new J2735Position3D();

      Long longitude = pos.get("long").asLong();
      Long latitude = pos.get("lat").asLong();
      Long elevation = pos.get("elevation").asLong();

      if (longitude != null) {
         if (longitude == 1800000001) {
            jpos.setLongitude(null);
         } else {
            jpos.setLongitude(BigDecimal.valueOf(longitude, 7));
         }
      }

      if (latitude != null) {
         if (latitude == 900000001) {
            jpos.setLatitude(null);
         } else {
            jpos.setLatitude(BigDecimal.valueOf(latitude, 7));
         }
      }

      if (elevation != null) {
         if (elevation == -4096) {
            jpos.setElevation(null);
         } else {
            jpos.setElevation(BigDecimal.valueOf(elevation, 1));
         }
      }

      return jpos;

   }

   public static Position3D position3D(J2735Position3D jpos) {
      Position3D pos = new Position3D();
      
      if (jpos.getLongitude() != null) {
         pos.set_long(new Longitude(jpos.getLongitude().scaleByPowerOfTen(7).longValue()));
      }

      if (jpos.getLatitude() != null) {
         pos.setLat(new Latitude(jpos.getLatitude().scaleByPowerOfTen(7).longValue()));
      }

     if (jpos.getElevation() != null) {
        pos.setElevation(new Elevation(jpos.getElevation().scaleByPowerOfTen(1).longValue()));
     }

      return pos;
   }

}
