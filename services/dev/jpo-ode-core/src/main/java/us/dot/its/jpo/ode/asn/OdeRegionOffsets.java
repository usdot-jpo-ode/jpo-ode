package us.dot.its.jpo.ode.asn;

import java.util.ArrayList;
import java.util.List;

import com.bah.ode.asn.oss.dsrc.RegionList;
import com.bah.ode.asn.oss.dsrc.RegionOffsets;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeRegionOffsets extends OdeObject {

   private static final long serialVersionUID = 2694144791459199713L;

   protected Long xOffsetCm;
   protected Long yOffsetCm;
   protected Long zOffsetCm;

   public OdeRegionOffsets() {
      super();
   }

   public OdeRegionOffsets(Long xOffsetCm, Long yOffsetCm, Long zOffsetCm) {
      this.xOffsetCm = xOffsetCm;
      this.yOffsetCm = yOffsetCm;
      this.zOffsetCm = zOffsetCm;
   }

   public OdeRegionOffsets(RegionOffsets ofs) {
      this.xOffsetCm = ofs.getXOffset();
      
      this.yOffsetCm = ofs.getYOffset();
      
      if (ofs.hasZOffset())
         this.zOffsetCm = ofs.getZOffset();
   }

   public Long getxOffsetCm() {
   	return xOffsetCm;
   }

   public OdeRegionOffsets setxOffsetCm(Long xOffsetCm) {
   	this.xOffsetCm = xOffsetCm;
   	return this;
   }

   public Long getyOffsetCm() {
   	return yOffsetCm;
   }

   public OdeRegionOffsets setyOffsetCm(Long yOffsetCm) {
   	this.yOffsetCm = yOffsetCm;
   	return this;
   }

   public Long getzOffsetCm() {
   	return zOffsetCm;
   }

   public OdeRegionOffsets setzOffsetCm(Long zOffsetCm) {
   	this.zOffsetCm = zOffsetCm;
   	return this;
   }

   public static List<OdeRegionOffsets> createList(RegionList regionList) {
      if (regionList == null)
         return null;
      
      ArrayList<OdeRegionOffsets> nl = new ArrayList<OdeRegionOffsets>();

      for (RegionOffsets ofs : regionList.elements) {
         nl.add(new OdeRegionOffsets(ofs));
      }
      return nl;
   }

}