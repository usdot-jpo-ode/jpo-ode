package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@JsonPropertyOrder({ "msgCnt", "timeStamp", "packetID", "urlB", "dataFrames" })
public class TravelerInformation extends Asn1Object {
   private static final long serialVersionUID = 1L;

   @JsonProperty("msgCnt")
   private String msgCnt;

   @JsonProperty("timeStamp")
   private int timeStamp;

   @JsonProperty("packetID")
   private String packetID;

   @JsonProperty("urlB")
   private String urlB;

   @JsonProperty("dataFrames")
   private DataFrame[] dataFrames;

   public int getTimeStamp() {
      return timeStamp;
   }

   public void setTimeStamp(int timeStamp) {
      this.timeStamp = timeStamp;
   }

   public String getUrlB() {
      return urlB;
   }

   public void setUrlB(String urlB) {
      this.urlB = urlB;
   }

   public String getPacketID() {
      return packetID;
   }

   public void setPacketID(String packetID) {
      this.packetID = packetID;
   }

   public DataFrame[] getDataFrames() {
      return dataFrames;
   }

   public void setDataFrames(DataFrame[] dataFrames) {
      this.dataFrames = dataFrames;
   }

   public String getMsgCnt() {
      return msgCnt;
   }

   public void setMsgCnt(String msgCnt) {
      this.msgCnt = msgCnt;
   }
}
