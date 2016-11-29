package us.dot.its.jpo.ode.util;

import us.dot.its.jpo.ode.model.OdeGeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class OdeGeoUtils {

   public static boolean isPositionWithinRegion(J2735Position3D pos, OdeGeoRegion region) {
      if (pos == null || region == null)
         return false;
      
      J2735Position3D nw = region.getNwCorner();
      J2735Position3D se = region.getSeCorner();
      
      if (nw == null || nw.getLatitude() == null || pos == null || pos.getLatitude().doubleValue() > nw.getLatitude().doubleValue())
         return false;
      if (nw.getLongitude() == null || pos.getLongitude().doubleValue() < nw.getLongitude().doubleValue())
         return false;
      if (se == null || se.getLatitude() == null || pos.getLatitude().doubleValue() < se.getLatitude().doubleValue())
         return false;
      if (se.getLongitude() == null || pos.getLongitude().doubleValue() > se.getLongitude().doubleValue())
         return false;
      
      return true;
   }

}