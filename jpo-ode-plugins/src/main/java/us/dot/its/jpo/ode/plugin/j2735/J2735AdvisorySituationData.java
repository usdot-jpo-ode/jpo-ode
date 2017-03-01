package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735AdvisorySituationData extends Asn1Object {

   private static final long serialVersionUID = -8570818421787304109L;

   // bitwise constants for setting distType by ORing
   public static final byte DIST_TYPE_NONE = 0;
   public static final byte DIST_TYPE_RSU = 1;
   public static final byte DIST_TYPE_IP = 2;

   protected J2735AdvisoryDetails advisoryMessage;

   public J2735AdvisorySituationData() {
      super();
   }

   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssAdvisorySituationData class
//   public OdeAdvisoryData() {
//      super();
//   }
//
//   public OdeAdvisoryData(String serialId, GroupID groupID,
//         TimeToLive timeToLive, GeoRegion serviceRegion) {
//      super(serialId, groupID, timeToLive, serviceRegion);
//   }
//
//   public OdeAdvisoryData(String streamId, long bundleId, long recordId) {
//      super(streamId, bundleId, recordId);
//   }
//
//   public OdeAdvisoryData(String serialId) {
//      super(serialId);
//   }
//
//   public OdeAdvisoryData(String serialId, AdvisorySituationData asd)
//         throws UnsupportedEncodingException {
//
//      super(serialId, asd.getGroupID(), asd.getTimeToLive(), asd.getServiceRegion());
//      
//      setAdvisoryMessage(new OdeAdvisoryDetails(asd.getAsdmDetails()));
//   }

   public J2735AdvisoryDetails getAdvisoryMessage() {
      return advisoryMessage;
   }

   public void setAdvisoryMessage(J2735AdvisoryDetails advisoryMessage) {
      this.advisoryMessage = advisoryMessage;
   }

}
