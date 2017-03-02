package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.RoadSignID;
import us.dot.its.jpo.ode.plugin.j2735.J2735RoadSignId;
import us.dot.its.jpo.ode.plugin.j2735.J2735RoadSignId.J2735MUTCDCode;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssRoadSignId {

   private OssRoadSignId() {
   }

   public static J2735RoadSignId genericRoadSignId(RoadSignID roadSignID) {
      J2735RoadSignId rsid = new J2735RoadSignId();
      if (roadSignID.position != null)
         rsid.setPosition(OssPosition3D.geneticPosition3D(roadSignID.position));

      if (roadSignID.hasCrc())
         rsid.setCrc(CodecUtils.toHex(roadSignID.crc.byteArrayValue()));
      if (roadSignID.viewAngle != null)
         rsid.setViewAngle(OssHeadingSlice.genericHeadingSlice(roadSignID.viewAngle));

      if (roadSignID.hasMutcdCode())
         rsid.setMutcdCode(J2735MUTCDCode.valueOf(roadSignID.getMutcdCode().name()));
      return rsid;
   }

}
