package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class Position3DBuilder {
   
   public static J2735Position3D genericPosition3D(JsonNode position3D) {
      J2735Position3D jpos = new J2735Position3D();

         if (position3D._long != null) {
             if (position3D._long.longValue() == 1800000001) {
                 jpos.setLongitude(null);
             } else {
                jpos.setLongitude(BigDecimal.valueOf(position3D._long.longValue(), 7));
             }
         }

         if (position3D.lat != null) {
             if (position3D.lat.longValue() == 900000001) {
                jpos.setLatitude(null);
             } else {
                jpos.setLatitude(BigDecimal.valueOf(position3D.lat.longValue(), 7));
             }
         } else {
            jpos.setLatitude(null);
         }

         if (position3D.elevation != null) {
             if (position3D.elevation.longValue() == -4096) {
                jpos.setElevation(null);
             } else {
                jpos.setElevation(BigDecimal.valueOf(position3D.elevation.longValue(), 1));
             }
         } else {
            jpos.setElevation(null);
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
