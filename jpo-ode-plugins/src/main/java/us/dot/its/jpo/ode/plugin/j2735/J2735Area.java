package us.dot.its.jpo.ode.plugin.j2735;

public class J2735Area extends J2735Choice {

   private static final long serialVersionUID = -4164122642665529665L;

   public J2735Circle circle_chosen;
   public J2735RegionPointSet regionPointSet_chosen;
   public J2735ShapePointSet shapePointSet_chosen;
   
   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssArea class
//   public OdeArea(Area area) {
//      super();
//      
//      int flag = area.getChosenFlag();
//      switch (flag) {
//      case Area.circle_chosen:
//         if (area.hasCircle())
//            setChosenField("circle_chosen", new OdeCircle(area.getCircle()));
//         break;
//      case Area.regionPointSet_chosen:
//         if (area.hasRegionPointSet())
//            setChosenField("regionPointSet_chosen", new OdeRegionPointSet(area.getRegionPointSet()));
//         break;
//      case Area.shapePointSet_chosen:
//         if (area.hasShapePointSet())
//            setChosenField("shapePointSet_chosen", new OdeShapePointSet(area.getShapePointSet()));
//         break;
//      }
//   }

   
}