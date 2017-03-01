package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TravelerInfo extends Asn1Object {

   private static final long serialVersionUID = 3570277012168173163L;

   private Integer dataFrameCount;
   private ArrayList<J2735AdvisoryDataFrame> dataFrames;
   private J2735DSRCmsgID msgID;
   private String packetID;
   private String urlB;

   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssMessageContent class
//   public J2735TravelerInfo(TravelerInformation tim) {
//      super();
//      if (tim.hasDataFrameCount())
//         setDataFrameCount(tim.getDataFrameCount().intValue());
//      if (tim.dataFrames != null)
//         setDataFrames2(tim.dataFrames);
//      if (tim.msgID != null)
//         setMsgID(J2735DSRCmsgID.valueOf(tim.msgID.name()));
//      if (tim.hasPacketID())
//         setPacketID(CodecUtils.toHex(tim.getPacketID().byteArrayValue()));
//      if (tim.hasUrlB())
//         setUrlB(tim.getUrlB().stringValue());
//   }
//
//   private void setDataFrames(DataFrames dataFrames2) {
//      dataFrames = new ArrayList<OdeAdvisoryDataFrame>();
//      ArrayList<Sequence_> elements = dataFrames2.elements;
//      
//      for (Sequence_ element : elements) {
//         if (element != null)
//            dataFrames.add(new OdeAdvisoryDataFrame(element));
//      }
//   }

   public Integer getDataFrameCount() {
      return dataFrameCount;
   }

   public J2735TravelerInfo setDataFrameCount(Integer dataFrameCount) {
      this.dataFrameCount = dataFrameCount;
      return this;
   }

   public ArrayList<J2735AdvisoryDataFrame> getDataFrames() {
      return dataFrames;
   }

   public J2735TravelerInfo setDataFrames(ArrayList<J2735AdvisoryDataFrame> dataFrames) {
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

   
}
