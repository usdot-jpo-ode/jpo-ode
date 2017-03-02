package us.dot.its.jpo.ode.plugin.j2735;

public class J2735AdvisorySituationData extends J2735WarehouseData {

   private static final long serialVersionUID = -8570818421787304109L;

   // bitwise constants for setting distType by ORing
   public static final byte DIST_TYPE_NONE = 0;
   public static final byte DIST_TYPE_RSU = 1;
   public static final byte DIST_TYPE_IP = 2;

   protected J2735AdvisoryDetails advisoryDetails;

   public J2735AdvisorySituationData() {
      super();
   }

   public J2735AdvisoryDetails AdvisoryDetails() {
      return advisoryDetails;
   }

   public void setAdvisoryDetails(J2735AdvisoryDetails advisoryDetails) {
      this.advisoryDetails = advisoryDetails;
   }

}
