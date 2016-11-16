package us.dot.its.jpo.ode.asn;

import java.util.List;

import com.bah.ode.asn.oss.dsrc.ShapePointSet;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeShapePointSet extends OdeObject {

   private static final long serialVersionUID = 196400689274442073L;

   public OdePosition3D anchor;
   public Integer laneWidth;
   public OdeDirectionOfUse directionality;
   public List<OdeLaneOffsets> nodeList;

   public OdeShapePointSet(ShapePointSet shapePointSet) {
      if (shapePointSet.hasAnchor())
         this.anchor = new OdePosition3D(shapePointSet.getAnchor());
      
      if (shapePointSet.hasLaneWidth())
         this.laneWidth = shapePointSet.getLaneWidth().intValue();
      
      if (shapePointSet.hasDirectionality())
         this.directionality = OdeDirectionOfUse.valueOf(shapePointSet.getDirectionality().name());
      
      if (shapePointSet.nodeList != null)
         this.nodeList = OdeLaneOffsets.createList(shapePointSet.nodeList);
   }

}