package us.dot.its.jpo.ode.coder;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Content;

public class Iee1609ContentValidator {

   public Iee1609ContentValidator() {
   }

   public boolean contentHadUnsecureData(Ieee1609Dot2Content ieee1609dot2ContentIn) {

      if (     ieee1609dot2ContentIn != null
            && ieee1609dot2ContentIn.getSignedData() != null
            && ieee1609dot2ContentIn.getSignedData().getTbsData() != null
            && ieee1609dot2ContentIn.getSignedData().getTbsData().getPayload() != null
            && ieee1609dot2ContentIn.getSignedData().getTbsData().getPayload().getData() != null
            && ieee1609dot2ContentIn.getSignedData().getTbsData().getPayload().getData().getContent() != null
            && ieee1609dot2ContentIn.getSignedData().getTbsData().getPayload().getData().getContent().getUnsecuredData() != null) {
         return true;
      } else {
         return false;
      }
      
//      try {
//         
//      }catch(NullPointerException e) {
//         
//      }
      
   }
}
