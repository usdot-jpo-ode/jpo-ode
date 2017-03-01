/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.plugin.j2735;

//   -- xOffset  INTEGER (-32767..32767), 
//   -- yOffset  INTEGER (-32767..32767),
//   -- if 6 or 8 bytes in length:
//   -- zOffset  INTEGER (-32767..32767) OPTIONAL,
//            -- all above in signed values where 
//            -- the LSB is in units of 1.0 cm   
//  
//   -- if 8 bytes in length:
//   -- width    LaneWidth               OPTIONAL
//   -- a length of 7 bytes is never used
public class J2735LaneOffsets extends J2735RegionOffsets {
   private static final long serialVersionUID = -4812679580897977812L;
   private Integer LaneWidthCm;

   public J2735LaneOffsets() {
      super();
   }

   public J2735LaneOffsets(Integer xOffsetCm, Integer yOffsetCm,
         Integer zOffsetCm, Integer laneWidthCm) {
      super(xOffsetCm.longValue(), yOffsetCm.longValue(), zOffsetCm.longValue());

      LaneWidthCm = laneWidthCm;
   }

   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssOffsets class
//   public OdeLaneOffsets(Offsets ofs) {
//      ByteBuffer bb = ByteBuffer.wrap(ofs.byteArrayValue()).order(ByteOrder.BIG_ENDIAN);
//      
//      if (ofs.byteArrayValue().length >= 2)
//         setxOffsetCm((long) bb.getShort());
//
//      if (ofs.byteArrayValue().length >= 4)
//         setyOffsetCm((long) bb.getShort());
//
//      if (ofs.byteArrayValue().length >= 6)
//         setzOffsetCm((long) bb.getShort());
//
//      if (ofs.byteArrayValue().length >= 8)
//         setLaneWidthCm((int) bb.getShort());
//   }

   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssNodeList class
//   public static List<OdeLaneOffsets> createList(NodeList nodeList) {
//      if (nodeList == null)
//         return null;
//
//      ArrayList<OdeLaneOffsets> nl = new ArrayList<OdeLaneOffsets>();
//
//      for (Offsets ofs : nodeList.elements) {
//         nl.add(new OdeLaneOffsets(ofs));
//      }
//      return nl;
//   }

   public Integer getLaneWidthCm() {
      return LaneWidthCm;
   }

   public J2735LaneOffsets setLaneWidthCm(Integer laneWidthCm) {
      LaneWidthCm = laneWidthCm;
      return this;
   }

}
