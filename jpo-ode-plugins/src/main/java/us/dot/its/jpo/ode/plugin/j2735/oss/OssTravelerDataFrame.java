package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.GeographicalPath;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerDataFrame;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerDataFrame.TravelerInfoType;

public class OssTravelerDataFrame {

   private OssTravelerDataFrame() {
   }

   public static J2735TravelerDataFrame genericTravelerDataFrame(TravelerDataFrame dataFrame) {
      J2735TravelerDataFrame gadf = new J2735TravelerDataFrame();
      
       if (dataFrame.content != null)
          gadf.setMessageContent(OssMessageContent.genericMessageContent(dataFrame.content));
       if (dataFrame.duratonTime != null)
          gadf.setDuratonTime(dataFrame.duratonTime.intValue());
       if (dataFrame.frameType != null)
          gadf.setFrameType(TravelerInfoType.valueOf(dataFrame.frameType.name()));
       if (dataFrame.msgId != null)
          gadf.setMessageId(OssMessageId.genericMessageId(dataFrame.msgId));
       if (dataFrame.priority != null)
          gadf.setPriority(dataFrame.priority.intValue());
       if (dataFrame.regions != null) {
          for (GeographicalPath region : dataFrame.regions.elements) {
             gadf.getRegions().add(OssGeographicalPath.genericGeographicalPath(region));
          }
       }
       if (dataFrame.startTime != null)
          gadf.setStartTime(dataFrame.startTime.intValue());
       if (dataFrame.hasStartYear())
          gadf.setStartYear(dataFrame.getStartYear().intValue());
       if (dataFrame.url != null)
          gadf.setUrl(dataFrame.url.stringValue());

       return gadf;
   }

}
