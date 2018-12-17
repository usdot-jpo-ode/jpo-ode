package us.dot.its.jpo.ode.plugin.j2735.asn1c;
//TODO open-ode
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
//import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;
//import us.dot.its.jpo.ode.util.CodecUtils;
//import us.dot.its.jpo.ode.util.DateTimeUtils;
//
//public class OssTravelerInformation {
//   
//   private OssTravelerInformation() {
//      throw new UnsupportedOperationException();
//  }
//   
//   public static J2735TravelerInformationMessage genericTim(TravelerInformation asnTim) {
//      J2735TravelerInformationMessage genericTim = new J2735TravelerInformationMessage();
//      
//      genericTim.setMsgCnt(asnTim.getMsgCnt().intValue());
//      
//      // TODO - Pure J2735 TIMs only contain time offset from an unknown year
//      // Instead, time must be extracted from log file metadata
//      genericTim.setTimeStamp(DateTimeUtils.now()); 
//      
//      genericTim.setPacketID(CodecUtils.toHex(asnTim.getPacketID().byteArrayValue()));
//      
//      if (asnTim.getUrlB() != null) {
//         genericTim.setUrlB(asnTim.getUrlB().stringValue());
//      }
//      
//      // TODO - the rest of the message translation
////      genericTim.setAsnDataFrames(asnTim.getDataFrames());
//      
//      return genericTim;
//   }
//
//}
