package us.dot.its.jpo.ode.asn;

import us.dot.its.jpo.ode.j2735.dsrc.Circle;
import us.dot.its.jpo.ode.j2735.dsrc.Radius_B12;
import us.dot.its.jpo.ode.model.OdeObject;

public class OdeCircle extends OdeObject {

   private static final long serialVersionUID = 3009231304492798761L;

   public OdePosition3D center;
   public Radius_B12 radius;

   public OdeCircle(Circle circle) {
      this.center = new OdePosition3D(circle.getCenter());
      this.radius = circle.getRadius();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((center == null) ? 0 : center.hashCode());
      result = prime * result + ((radius == null) ? 0 : radius.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeCircle other = (OdeCircle) obj;
      if (center == null) {
         if (other.center != null)
            return false;
      } else if (!center.equals(other.center))
         return false;
      if (radius == null) {
         if (other.radius != null)
            return false;
      } else if (!radius.equals(other.radius))
         return false;
      return true;
   }

}