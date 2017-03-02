package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.semi.AdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.J2735AdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.J2735WarehouseData;

public class OssAdvisorySituationData {

   private OssAdvisorySituationData() {
   }

   public static J2735AdvisorySituationData genericAdvisorySituationData(
         AdvisorySituationData asd) {
      
      J2735AdvisorySituationData gasd = new J2735AdvisorySituationData();
      J2735WarehouseData gwd = 
            OssWarehouseData.genericWarehouseData(
                  asd.groupID, asd.timeToLive, asd.serviceRegion);
      gasd.setCenterPosition(gwd.getCenterPosition());
      gasd.setGroupId(gwd.getGroupId());
      gasd.setServiceRegion(gwd.getServiceRegion());
      gasd.setTimeToLive(gwd.getTimeToLive());
      gasd.setAdvisoryDetails(OssAdvisoryDetails.genericAdvisoryDetails(asd.asdmDetails));

      return gasd;
      
   }

}
