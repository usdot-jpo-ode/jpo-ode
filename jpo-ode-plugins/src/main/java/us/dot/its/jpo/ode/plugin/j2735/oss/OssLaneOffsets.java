package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.plugin.j2735.J2735LaneOffsets;
import us.dot.its.jpo.ode.plugin.j2735.J2735OffsetSystem;

public class OssLaneOffsets {

   private OssLaneOffsets() {
   }

 public static J2735LaneOffsets genericLaneOffsets(J2735OffsetSystem ofs) {
    J2735LaneOffsets gofs = new J2735LaneOffsets();
    //TODO uncomment and fix
//    ByteBuffer bb = ByteBuffer.wrap(ofs.byteArrayValue()).order(ByteOrder.BIG_ENDIAN);
//    
//    if (ofs.byteArrayValue().length >= 2)
//       setxOffsetCm((long) bb.getShort());
//
//    if (ofs.byteArrayValue().length >= 4)
//       setyOffsetCm((long) bb.getShort());
//
//    if (ofs.byteArrayValue().length >= 6)
//       setzOffsetCm((long) bb.getShort());
//
//    if (ofs.byteArrayValue().length >= 8)
//       setLaneWidthCm((int) bb.getShort());
   return gofs;
 }
}
