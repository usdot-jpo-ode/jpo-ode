package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TravelerInfo extends Asn1Object {

   private static final long serialVersionUID = 3570277012168173163L;

   private ArrayList<J2735TravelerDataFrame> dataFrames;
   private J2735DSRCmsgID msgID;
   private String packetID;
   private String urlB;
   private ArrayList<J2735RegionalContent> regional;
   private Integer timeStamp;


   public ArrayList<J2735TravelerDataFrame> getDataFrames() {
      return dataFrames;
   }

   public J2735TravelerInfo setDataFrames(ArrayList<J2735TravelerDataFrame> dataFrames) {
      this.dataFrames = dataFrames;
      return this;
   }

   public J2735DSRCmsgID getMsgID() {
      return msgID;
   }

   public J2735TravelerInfo setMsgID(J2735DSRCmsgID msgID) {
      this.msgID = msgID;
      return this;
   }

   public String getPacketID() {
      return packetID;
   }

   public J2735TravelerInfo setPacketID(String packetID) {
      this.packetID = packetID;
      return this;
   }

   public String getUrlB() {
      return urlB;
   }

   public J2735TravelerInfo setUrlB(String urlB) {
      this.urlB = urlB;
      return this;
   }

   public ArrayList<J2735RegionalContent> getRegional() {
      return regional;
   }

   public J2735TravelerInfo setRegional(ArrayList<J2735RegionalContent> regional) {
      this.regional = regional;
      return this;
   }

   public Integer getTimeStamp() {
      return timeStamp;
   }

   public void setTimeStamp(Integer timeStamp) {
      this.timeStamp = timeStamp;
   }

   
}
