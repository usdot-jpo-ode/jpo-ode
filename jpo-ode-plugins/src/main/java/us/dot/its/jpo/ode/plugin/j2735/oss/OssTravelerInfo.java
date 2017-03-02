package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.ArrayList;

import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerDataFrame;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInfo;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssTravelerInfo {

   private OssTravelerInfo() {
   }

   public static J2735TravelerInfo genericTravelerInfo(TravelerInformation tim) {
      J2735TravelerInfo gti = new J2735TravelerInfo();
      
       if (tim.dataFrames != null) {
          ArrayList<J2735TravelerDataFrame> dataFrames = gti.getDataFrames();
          
          for (TravelerDataFrame dataFrame : tim.dataFrames.elements) {
             dataFrames.add(OssTravelerDataFrame.genericTravelerDataFrame(dataFrame));
          }
       }
       if (tim.hasPacketID())
          gti.setPacketID(CodecUtils.toHex(tim.getPacketID().byteArrayValue()));
       if (tim.hasRegional())
          gti.setRegional(OssRegionalContent.genericRegionalContent(tim.getRegional()));
       if (tim.hasTimeStamp())
          gti.setTimeStamp(tim.getTimeStamp().intValue());
       if (tim.hasUrlB())
          gti.setUrlB(tim.getUrlB().stringValue());
       
      return gti;
 
   }

}
