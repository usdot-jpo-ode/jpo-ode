package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.ValidRegion.Area;
import us.dot.its.jpo.ode.plugin.j2735.J2735Area;

public class OssArea {

   private OssArea() {
   }

   public static J2735Area genericArea(Area area) {
      J2735Area ga = new J2735Area();
       int flag = area.getChosenFlag();
       switch (flag) {
       case Area.circle_chosen:
          if (area.hasCircle())
             ga.setChosenField("circle_chosen", OssCircle.genericCircle(area.getCircle()));
          break;
       case Area.regionPointSet_chosen:
          if (area.hasRegionPointSet())
             ga.setChosenField("regionPointSet_chosen", 
                   OssRegionPointSet.genericRegionPointSet(area.getRegionPointSet()));
          break;
       case Area.shapePointSet_chosen:
          if (area.hasShapePointSet())
             ga.setChosenField("shapePointSet_chosen", 
                   OssShapePointSet.genericShapePointSet(area.getShapePointSet()));
          break;
       }
      return ga;
   }

}
