package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class OssPosition3D {
   
   public static J2735Position3D geneticPosition3D(Position3D pos) {
      J2735Position3D jpos = new J2735Position3D();
      
         // private OdePosition3D position;
         // Position3D ::= SEQUENCE {
         // lat Latitude, -- in 1/10th micro degrees
         // long Longitude, -- in 1/10th micro degrees
         // elevation Elevation OPTIONAL
         // }
         // Latitude ::= INTEGER (-900000000..900000001)
         // -- LSB = 1/10 micro degree
         // -- Providing a range of plus-minus 90 degrees
         // Longitude ::= INTEGER (-1800000000..1800000001)
         // -- LSB = 1/10 micro degree
         // -- Providing a range of plus-minus 180 degrees
         //Elevation ::= INTEGER (-4096..61439)
         // -- In units of 10 cm steps above or below the reference ellipsoid
         // -- Providing a range of -409.5 to + 6143.9 meters
         // -- The value -4096 shall be used when Unknown is to be sent

         if (pos._long != null) {
             if (pos._long.longValue() == 1800000001) {
                 jpos.setLongitude(null);
             } else {
                jpos.setLongitude(BigDecimal.valueOf(pos._long.longValue(), 7));
             }
         }

         if (pos.lat != null) {
             if (pos.lat.longValue() == 900000001) {
                jpos.setLatitude(null);
             } else {
                jpos.setLatitude(BigDecimal.valueOf(pos.lat.longValue(), 7));
             }
         } else {
            jpos.setLatitude(null);
         }

         if (pos.elevation != null) {
             if (pos.elevation.longValue() == -4096) {
                jpos.setElevation(null);
             } else {
                jpos.setElevation(BigDecimal.valueOf(pos.elevation.longValue(), 1));
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
