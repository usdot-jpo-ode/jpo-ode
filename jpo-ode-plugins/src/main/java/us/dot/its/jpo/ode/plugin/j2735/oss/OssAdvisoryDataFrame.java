package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.MsgId;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageId;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssAdvisoryDataFrame {

   private OssAdvisoryDataFrame() {
   }

 public J2735MessageId genericMessageId(MsgId msgId) {
    J2735MessageId messageId = new J2735MessageId();
    
    int chosenFlag = msgId.getChosenFlag();
    switch (chosenFlag) {
    case MsgId.furtherInfoID_chosen:
       if (msgId.hasFurtherInfoID())
          messageId.setChosenField(
                "furtherInfoID_chosen", 
                CodecUtils.toHex(msgId.getFurtherInfoID().byteArrayValue()));
       break;
    case MsgId.roadSignID_chosen:
       if (msgId.hasRoadSignID())
          messageId.setChosenField("roadSignID_chosen",
                OssRoadSignId.genericRoadSignId(msgId.getRoadSignID()));
       break;
    }
   return messageId;
 }

}
