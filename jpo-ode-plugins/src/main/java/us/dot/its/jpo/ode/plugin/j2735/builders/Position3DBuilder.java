package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.util.JsonUtils;

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

   public static JsonNode position3D(JsonNode jpos) {

      ObjectNode posNode = JsonUtils.newNode();

      JsonNode latitude = jpos.get("latitude");
      if (latitude != null) {
         JsonUtils.addNode(posNode, "lat", BigDecimal.valueOf(latitude.asLong()).scaleByPowerOfTen(7).longValue());
      }

      JsonNode longitude = jpos.get("longitude");
      if (longitude != null) {
         JsonUtils.addNode(posNode, "long", BigDecimal.valueOf(longitude.asLong()).scaleByPowerOfTen(7).longValue());
      }

      JsonNode elevation = jpos.get("elevation");
      if (elevation != null) {
         JsonUtils.addNode(posNode, "elevation",
               BigDecimal.valueOf(elevation.asLong()).scaleByPowerOfTen(1).longValue());
      }

      return posNode;
   }

}
