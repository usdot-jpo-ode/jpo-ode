package us.dot.its.jpo.ode.asn;

import java.util.List;

import com.bah.ode.asn.oss.dsrc.RegionPointSet;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeRegionPointSet extends OdeObject {

   private static final long serialVersionUID = -2354156235911875831L;

   public OdePosition3D anchor;
   public List<OdeRegionOffsets> nodeList;

   public OdeRegionPointSet(RegionPointSet regionPointSet) {
      if (regionPointSet.hasAnchor())
         this.anchor = new OdePosition3D(regionPointSet.getAnchor());
      if (regionPointSet.nodeList != null)
         this.nodeList = OdeRegionOffsets.createList(regionPointSet.nodeList);
   }

}