package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.Circle;
import us.dot.its.jpo.ode.plugin.j2735.J2735Circle;

public class OssCircle {

   private OssCircle() {
   }

   public static J2735Circle genericCircle(Circle circle) {
      J2735Circle c = new J2735Circle();
      
       c.center = OssPosition3D.geneticPosition3D(circle.getCenter());
       c.radius = circle.getRadius().intValue();
      return c;

   }

}
