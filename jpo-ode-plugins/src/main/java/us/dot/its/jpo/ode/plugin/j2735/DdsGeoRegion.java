package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class DdsGeoRegion extends Asn1Object {
   private static final long serialVersionUID = -8496515705143160656L;
   private DsrcPosition3D nwCorner;
   private DsrcPosition3D seCorner;
   
   public DsrcPosition3D getNwCorner() {
      return nwCorner;
   }
   public void setNwCorner(DsrcPosition3D nwCorner) {
      this.nwCorner = nwCorner;
   }
   public DsrcPosition3D getSeCorner() {
      return seCorner;
   }
   public void setSeCorner(DsrcPosition3D seCorner) {
      this.seCorner = seCorner;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((nwCorner == null) ? 0 : nwCorner.hashCode());
      result = prime * result + ((seCorner == null) ? 0 : seCorner.hashCode());
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
      DdsGeoRegion other = (DdsGeoRegion) obj;
      if (nwCorner == null) {
         if (other.nwCorner != null)
            return false;
      } else if (!nwCorner.equals(other.nwCorner))
         return false;
      if (seCorner == null) {
         if (other.seCorner != null)
            return false;
      } else if (!seCorner.equals(other.seCorner))
         return false;
      return true;
   }


}
