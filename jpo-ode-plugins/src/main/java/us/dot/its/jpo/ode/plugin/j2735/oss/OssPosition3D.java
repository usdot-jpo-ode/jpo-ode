package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class OssPosition3D {
   
   public static J2735Position3D geneticPosition3D(Position3D pos) {
      return new J2735Position3D(
            pos.lat.longValue(), 
            pos._long.longValue(), 
            pos.elevation.longValue());
      
   }

}
