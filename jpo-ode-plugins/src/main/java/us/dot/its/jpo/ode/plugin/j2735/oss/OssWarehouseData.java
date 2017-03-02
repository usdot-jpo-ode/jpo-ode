package us.dot.its.jpo.ode.plugin.j2735.oss;


import us.dot.its.jpo.ode.j2735.semi.GeoRegion;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.TimeToLive;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735WarehouseData;
import us.dot.its.jpo.ode.plugin.j2735.J2735WarehouseData.OdeTimeToLive;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssWarehouseData {

   private OssWarehouseData() {
   }
   
   public static J2735WarehouseData genericWarehouseData(
         GroupID groupID,
         TimeToLive timeToLive,
         GeoRegion serviceRegion
         ) {
      
      J2735WarehouseData gwd = new J2735WarehouseData();
      if (groupID != null)
         gwd.setGroupId(CodecUtils.toHex(groupID.byteArrayValue()));

      if (timeToLive != null)
         gwd.setTimeToLive(OdeTimeToLive.valueOf(timeToLive.name()));
      
      if (serviceRegion != null) {
         J2735GeoRegion gsr = OssGeoRegion.genericGeoRegion(serviceRegion);
         gwd.setServiceRegion(gsr);
      
         gwd.setCenterPosition(gsr.getCenterPosition());
      }
      
      return gwd;
      
   }

}
