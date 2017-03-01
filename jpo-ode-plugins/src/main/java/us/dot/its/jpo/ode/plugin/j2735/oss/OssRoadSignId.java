package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.RoadSignID;
import us.dot.its.jpo.ode.plugin.j2735.J2735RoadSignId;

public class OssRoadSignId {

   private OssRoadSignId() {
   }

   public static J2735RoadSignId genericRoadSignId(RoadSignID roadSignID) {
      J2735RoadSignId rsid = new J2735RoadSignId();
      //TODO uncomment and fix
//      if (roadSignID.position != null)
//         rsid.setPosition(OssPosition(roadSignID.position));
//      if (roadSignID.viewAngle != null)
//         rsid.setViewAngles(OdeHeadingSlice.SliceMask.getHeadingSlices(roadSignID.viewAngle));
//
//      if (roadSignID.hasMutcdCode())
//         setMutcdCode(OdeMUTCDCode.valueOf(roadSignID.getMutcdCode().name()));
      return rsid;
   }

}
