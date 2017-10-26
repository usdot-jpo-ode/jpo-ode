package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

public class Position3DBuilder {
   
   public static DsrcPosition3D dsrcPosition3D(JsonNode pos) {
      Long longitude = pos.get("longitude").asLong();
      Long latitude = pos.get("latitude").asLong();
      Long elevation = pos.get("elevation").asLong();

      return new DsrcPosition3D(longitude, latitude, elevation);

   }

   public static OdePosition3D odePosition3D(DsrcPosition3D dsrcPos) {
      return odePosition3D(dsrcPos.getLongitude(), dsrcPos.getLatitude(), dsrcPos.getElevation());
   }

   private static OdePosition3D odePosition3D(Long longitude, Long latitude, Long elevation) {
      OdePosition3D jpos = new OdePosition3D();

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

   public static OdePosition3D odePosition3D(JsonNode jpos) {

      JsonNode longitude = jpos.get("longitude");
      JsonNode latitude = jpos.get("latitude");
      JsonNode elevation = jpos.get("elevation");
      
      OdePosition3D dPos = new OdePosition3D(
            BigDecimal.valueOf(longitude.asDouble()),
            BigDecimal.valueOf(latitude.asDouble()),
            BigDecimal.valueOf(elevation.asDouble()));
      
      return dPos;
   }

   public static DsrcPosition3D dsrcPosition3D(BigDecimal longitude, BigDecimal latitude, BigDecimal elevation) {
      DsrcPosition3D dPos = new DsrcPosition3D();

      if (longitude != null) {
         dPos.setLongitude(longitude.scaleByPowerOfTen(7).longValue());
      }

      if (latitude != null) {
         dPos.setLatitude(latitude.scaleByPowerOfTen(7).longValue());
      }

      if (elevation != null) {
         dPos.setElevation(elevation.scaleByPowerOfTen(1).longValue());
      }

      return dPos;
   }

   public static DsrcPosition3D dsrcPosition3D(OdePosition3D odePosition3D) {
      return dsrcPosition3D(odePosition3D.getLatitude(), odePosition3D.getLatitude(), odePosition3D.getElevation());
   }

}
