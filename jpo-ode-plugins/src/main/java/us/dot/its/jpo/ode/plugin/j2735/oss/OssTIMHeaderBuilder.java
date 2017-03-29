package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.ZonedDateTime;

import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.FurtherInfoID;
import us.dot.its.jpo.ode.j2735.dsrc.MUTCDCode;
import us.dot.its.jpo.ode.j2735.dsrc.MinuteOfTheYear;
import us.dot.its.jpo.ode.j2735.dsrc.MinutesDuration;
import us.dot.its.jpo.ode.j2735.dsrc.RoadSignID;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
import us.dot.its.jpo.ode.j2735.dsrc.SignPrority;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInfoType;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.MsgId;
import us.dot.its.jpo.ode.plugin.TimFieldValidator;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OssTIMHeaderBuilder {
   private OssTIMHeaderBuilder() {

   }

   public static TravelerDataFrame buildTimHeader(J2735TravelerInputData.DataFrame inputDataFrame,
         TravelerDataFrame dataFrame) throws ParseException{
      TimFieldValidator.validateHeaderIndex(inputDataFrame.getsspTimRights());
      dataFrame.setSspTimRights(new SSPindex(inputDataFrame.getsspTimRights()));
      TimFieldValidator.validateInfoType(inputDataFrame.getFrameType());
      dataFrame.setFrameType(TravelerInfoType.valueOf(inputDataFrame.getFrameType()));
      dataFrame.setMsgId(getMessageId(inputDataFrame));
      dataFrame.setStartYear(new DYear(DateTimeUtils.isoDateTime(inputDataFrame.getStartDateTime()).getYear()));
      dataFrame.setStartTime(new MinuteOfTheYear(getMinuteOfTheYear(inputDataFrame.getStartDateTime())));
      TimFieldValidator.validateMinutesDuration(inputDataFrame.getDurationTime());
      dataFrame.setDuratonTime(new MinutesDuration(inputDataFrame.getDurationTime()));
      TimFieldValidator.validateSign(inputDataFrame.getPriority());
      dataFrame.setPriority(new SignPrority(inputDataFrame.getPriority()));
      return dataFrame;
   }
   
   private static MsgId getMessageId(J2735TravelerInputData.DataFrame dataFrame) {
      MsgId msgId = new MsgId();
      TimFieldValidator.validateMessageID(dataFrame.getMsgID());

      if ("RoadSignID".equals(dataFrame.getMsgID())) {
         msgId.setChosenFlag(MsgId.roadSignID_chosen);
         RoadSignID roadSignID = new RoadSignID();
         TimFieldValidator.validatePosition(dataFrame.getPosition());
         roadSignID.setPosition(OssPosition3D.position3D(dataFrame.getPosition()));
         TimFieldValidator.validateHeading(dataFrame.getViewAngle());
         roadSignID.setViewAngle(TimFieldValidator.getHeadingSlice(dataFrame.getViewAngle()));
         TimFieldValidator.validateMUTCDCode(dataFrame.getMutcd());
         roadSignID.setMutcdCode(MUTCDCode.valueOf(dataFrame.getMutcd()));
         roadSignID.setCrc(TimFieldValidator.getMsgCrc(dataFrame.getCrc()));
         msgId.setRoadSignID(roadSignID);
      } else if ("FurtherInfoID".equals(dataFrame.getMsgID())) {
         msgId.setChosenFlag(MsgId.furtherInfoID_chosen);
         String info = dataFrame.getFurtherInfoID();
         if (info == null || info.length() == 0) {
            msgId.setFurtherInfoID(new FurtherInfoID(new byte[] { 0x00, 0x00 }));
         } else {
            short result = 0;
            for (int i = 0; i < 16; i++) {
               if (info.charAt(i) == '1') {
                  result |= 1;
               }
               result <<= 1;
            }
            msgId.setFurtherInfoID(new FurtherInfoID(ByteBuffer.allocate(2).putShort(result).array()));
         }
      }
      return msgId;
   }
   
   public static long getMinuteOfTheYear(String timestamp) throws ParseException {
      ZonedDateTime start = DateTimeUtils.isoDateTime(timestamp);
      long diff = DateTimeUtils.difference(DateTimeUtils.isoDateTime(start.getYear() + "-01-01T00:00:00+00:00"), start);
      long minutes = diff / 60000;
      TimFieldValidator.validateStartTime(minutes);
      return minutes;
   }
}
