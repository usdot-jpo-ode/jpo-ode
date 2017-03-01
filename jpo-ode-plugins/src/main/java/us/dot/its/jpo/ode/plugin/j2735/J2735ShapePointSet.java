package us.dot.its.jpo.ode.plugin.j2735;

import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ShapePointSet extends Asn1Object {

   private static final long serialVersionUID = 196400689274442073L;

   public J2735Position3D anchor;
   public Integer laneWidth;
   public J2735DirectionOfUse directionality;
   public List<J2735LaneOffsets> nodeList;

   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssShapePointSet class
//   public OdeShapePointSet(ShapePointSet shapePointSet) {
//      if (shapePointSet.hasAnchor())
//         this.anchor = new J2735Position3D(shapePointSet.getAnchor());
//      
//      if (shapePointSet.hasLaneWidth())
//         this.laneWidth = shapePointSet.getLaneWidth().intValue();
//      
//      if (shapePointSet.hasDirectionality())
//         this.directionality = J2735DirectionOfUse.valueOf(shapePointSet.getDirectionality().name());
//      
//      if (shapePointSet.nodeList != null)
//         this.nodeList = OdeLaneOffsets.createList(shapePointSet.nodeList);
//   }

}