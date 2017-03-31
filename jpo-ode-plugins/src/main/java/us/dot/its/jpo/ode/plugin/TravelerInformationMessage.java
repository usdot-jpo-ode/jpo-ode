package us.dot.its.jpo.ode.plugin;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData.DataFrame;

public class TravelerInformationMessage {
   public static class TIM extends OdeObject {

      private static final long serialVersionUID = -200529140190872305L;

      private int msgCnt;
      private String timeStamp;
      private int packetID;
      private String urlB;
      private DataFrame[] dataframes;

      public int getMsgCnt() {
         return msgCnt;
      }

      public void setMsgCnt(int msgCnt) {
         this.msgCnt = msgCnt;
      }

      public String getTimeStamp() {
         return timeStamp;
      }

      public void setTimeStamp(String timeStamp) {
         this.timeStamp = timeStamp;
      }

      public int getPacketID() {
         return packetID;
      }

      public void setPacketID(int packetID) {
         this.packetID = packetID;
      }

      public DataFrame[] getDataframes() {
         return dataframes;
      }

      public void setDataframes(DataFrame[] dataframes) {
         this.dataframes = dataframes;
      }

      public String getUrlB() {
         return urlB;
      }

      public void setUrlB(String urlB) {
         this.urlB = urlB;
      }
   }

   private TravelerInformationMessage() {
      throw new UnsupportedOperationException();
   }
}
