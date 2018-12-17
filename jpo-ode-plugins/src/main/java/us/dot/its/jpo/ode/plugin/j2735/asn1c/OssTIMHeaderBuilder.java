package us.dot.its.jpo.ode.plugin.j2735.asn1c;
//TODO open-ode
//import java.text.ParseException;
//import java.time.ZonedDateTime;
//
//import us.dot.its.jpo.ode.j2735.dsrc.DYear;
//import us.dot.its.jpo.ode.j2735.dsrc.MUTCDCode;
//import us.dot.its.jpo.ode.j2735.dsrc.MinuteOfTheYear;
//import us.dot.its.jpo.ode.j2735.dsrc.MinutesDuration;
//import us.dot.its.jpo.ode.j2735.dsrc.RoadSignID;
//import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
//import us.dot.its.jpo.ode.j2735.dsrc.SignPrority;
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.MsgId;
//import us.dot.its.jpo.ode.j2735.dsrc.TravelerInfoType;
//import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage;
//import us.dot.its.jpo.ode.plugin.j2735.TimFieldValidator;
//import us.dot.its.jpo.ode.util.DateTimeUtils;
//
//public class OssTIMHeaderBuilder {
//   private OssTIMHeaderBuilder() {
//      throw new UnsupportedOperationException();
//   }
//
//   public static TravelerDataFrame buildTimHeader(
//         J2735TravelerInformationMessage.DataFrame inputDataFrame,
//         TravelerDataFrame dataFrame) throws ParseException{
//      TimFieldValidator.validateHeaderIndex(inputDataFrame.getSspTimRights());
//      dataFrame.setSspTimRights(new SSPindex(inputDataFrame.getSspTimRights()));
//      dataFrame.setFrameType(TravelerInfoType.valueOf(inputDataFrame.getFrameType().ordinal()));
//      dataFrame.setMsgId(getMessageId(inputDataFrame));
//      dataFrame.setStartYear(new DYear(DateTimeUtils.isoDateTime(inputDataFrame.getStartDateTime()).getYear()));
//      dataFrame.setStartTime(new MinuteOfTheYear(getMinuteOfTheYear(inputDataFrame.getStartDateTime())));
//      TimFieldValidator.validateMinutesDuration(inputDataFrame.getDurationTime());
//      dataFrame.setDuratonTime(new MinutesDuration(inputDataFrame.getDurationTime()));
//      TimFieldValidator.validateSign(inputDataFrame.getPriority());
//      dataFrame.setPriority(new SignPrority(inputDataFrame.getPriority()));
//      return dataFrame;
//   }
//   
//   public static MsgId getMessageId(J2735TravelerInformationMessage.DataFrame dataFrame) {
//      MsgId msgId = new MsgId();
//      TimFieldValidator.validateMessageID(dataFrame.getMsgId());
//
//      us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage.DataFrame.MsgId msgId2 = dataFrame.getMsgId();
//      if (msgId2 != null) {
//         us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInformationMessage.DataFrame.RoadSignID roadSignID2 = msgId2.getRoadSignID();
//         if (roadSignID2 != null) {
//            msgId.setChosenFlag(MsgId.roadSignID_chosen);
//            RoadSignID roadSignID = new RoadSignID();
//            TimFieldValidator.validatePosition(roadSignID2.getPosition());
//            roadSignID.setPosition(OssPosition3D.position3D(roadSignID2.getPosition()));
//            TimFieldValidator.validateHeading(roadSignID2.getViewAngle());
//            roadSignID.setViewAngle(OssTravelerMessageBuilder.getHeadingSlice(roadSignID2.getViewAngle()));
//            roadSignID.setMutcdCode(MUTCDCode.valueOf(roadSignID2.getMutcdCode().ordinal()));
//            roadSignID.setCrc(OssTravelerMessageBuilder.getMsgCrc(roadSignID2.getCrc()));
//            msgId.setRoadSignID(roadSignID);
//         } else if (msgId2.getFurtherInfoID() != null) {
//            msgId.setChosenFlag(MsgId.furtherInfoID_chosen);
//            msgId.setFurtherInfoID(OssTravelerMessageBuilder.getMsgFurtherInfoID(msgId2.getFurtherInfoID()));
//         }
//      }
//      return msgId;
//   }
//   
//   public static long getMinuteOfTheYear(String timestamp) throws ParseException {
//      ZonedDateTime start = DateTimeUtils.isoDateTime(timestamp);
//      long diff = DateTimeUtils.difference(DateTimeUtils.isoDateTime(start.getYear() + "-01-01T00:00:00+00:00"), start);
//      long minutes = diff / 60000;
//      TimFieldValidator.validateStartTime(minutes);
//      return minutes;
//   }
//}
