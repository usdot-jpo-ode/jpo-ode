package us.dot.its.jpo.ode.asn;

import com.bah.ode.asn.oss.dsrc.Circle;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeCircle extends OdeObject {

   private static final long serialVersionUID = 3009231304492798761L;

   public OdePosition3D center;
   public OdeRadius radius;

   public OdeCircle(Circle circle) {
      this.center = new OdePosition3D(circle.getCenter());
      this.radius = new OdeRadius(circle.getRaduis());
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