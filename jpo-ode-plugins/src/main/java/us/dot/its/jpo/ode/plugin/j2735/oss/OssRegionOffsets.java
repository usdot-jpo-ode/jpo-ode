package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.j2735.dsrc.RegionList;
import us.dot.its.jpo.ode.j2735.dsrc.RegionOffsets;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionOffsets;

public class OssRegionOffsets {

   private OssRegionOffsets() {
   }

   public static J2735RegionOffsets genericRegionOffsets(RegionOffsets ofs) {
      J2735RegionOffsets gro = new J2735RegionOffsets();

      gro.setxOffsetCm(ofs.getXOffset().longValue());

      gro.setyOffsetCm(ofs.getYOffset().longValue());

      if (ofs.hasZOffset())
         gro.setzOffsetCm(ofs.getZOffset().longValue());
      return gro;
   }

   public static List<J2735RegionOffsets> createList(RegionList regionList) {
      if (regionList == null)
         return null;

      ArrayList<J2735RegionOffsets> nl = new ArrayList<J2735RegionOffsets>();

      for (RegionOffsets ofs : regionList.elements) {
         nl.add(OssRegionOffsets.genericRegionOffsets(ofs));
      }
      return nl;
   }

}
