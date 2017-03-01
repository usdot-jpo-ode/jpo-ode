package us.dot.its.jpo.ode.plugin.j2735;

import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RegionPointSet extends Asn1Object {

   private static final long serialVersionUID = -2354156235911875831L;

   public J2735Position3D anchor;
   public List<J2735RegionOffsets> nodeList;

   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssRegionPointSet class
//   public OdeRegionPointSet(RegionPointSet regionPointSet) {
//      if (regionPointSet.hasAnchor())
//         this.anchor = new J2735Position3D(regionPointSet.getAnchor());
//      if (regionPointSet.nodeList != null)
//         this.nodeList = OdeRegionOffsets.createList(regionPointSet.nodeList);
//   }

}