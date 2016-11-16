package us.dot.its.jpo.ode.asn;

import com.bah.ode.asn.oss.dsrc.ValidRegion.Area;

public class OdeArea extends OdeChoice {

   private static final long serialVersionUID = -4164122642665529665L;

   public OdeCircle circle_chosen;
   public OdeRegionPointSet regionPointSet_chosen;
   public OdeShapePointSet shapePointSet_chosen;
   
   public OdeArea(Area area) {
      super();
      
      int flag = area.getChosenFlag();
      switch (flag) {
      case Area.circle_chosen:
         if (area.hasCircle())
            setChosenField("circle_chosen", new OdeCircle(area.getCircle()));
         break;
      case Area.regionPointSet_chosen:
         if (area.hasRegionPointSet())
            setChosenField("regionPointSet_chosen", new OdeRegionPointSet(area.getRegionPointSet()));
         break;
      case Area.shapePointSet_chosen:
         if (area.hasShapePointSet())
            setChosenField("shapePointSet_chosen", new OdeShapePointSet(area.getShapePointSet()));
         break;
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((circle_chosen == null) ? 0 : circle_chosen.hashCode());
      result = prime * result + ((regionPointSet_chosen == null) ? 0
            : regionPointSet_chosen.hashCode());
      result = prime * result + ((shapePointSet_chosen == null) ? 0
            : shapePointSet_chosen.hashCode());
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
      OdeArea other = (OdeArea) obj;
      if (circle_chosen == null) {
         if (other.circle_chosen != null)
            return false;
      } else if (!circle_chosen.equals(other.circle_chosen))
         return false;
      if (regionPointSet_chosen == null) {
         if (other.regionPointSet_chosen != null)
            return false;
      } else if (!regionPointSet_chosen.equals(other.regionPointSet_chosen))
         return false;
      if (shapePointSet_chosen == null) {
         if (other.shapePointSet_chosen != null)
            return false;
      } else if (!shapePointSet_chosen.equals(other.shapePointSet_chosen))
         return false;
      return true;
   }


   
}