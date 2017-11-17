package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

public class Position3DBuilder {
   
   public static DsrcPosition3D dsrcPosition3D(JsonNode pos) {
      Long latitude = pos.get("lat").asLong();
      Long longitude = pos.get("long").asLong();
      Long elevation = pos.get("elevation").asLong();

      return new DsrcPosition3D(longitude, latitude, elevation);

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
      if (jpos.get("latitude") != null) {
         latitude = jpos.get("latitude").decimalValue();
      }
      
      BigDecimal longitude = null;
      if (jpos.get("longitude") != null) {
         longitude = jpos.get("longitude").decimalValue();
      }
      
      BigDecimal elevation = null;
      if (jpos.get("elevation") != null) {
         elevation = jpos.get("elevation").decimalValue();
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
