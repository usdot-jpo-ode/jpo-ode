package us.dot.its.jpo.ode.asn;

import java.util.ArrayList;

import com.bah.ode.asn.oss.dsrc.TravelerInformation;
import com.bah.ode.asn.oss.dsrc.TravelerInformation.DataFrames;
import com.bah.ode.asn.oss.dsrc.TravelerInformation.DataFrames.Sequence_;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OdeTravelerInfo extends OdeObject {

   private static final long serialVersionUID = 3570277012168173163L;

   private Integer dataFrameCount;
   private ArrayList<OdeAdvisoryDataFrame> dataFrames;
   private OdeDSRCmsgID msgID;
   private String packetID;
   private String urlB;

   public OdeTravelerInfo(TravelerInformation tim) {
      super();
      if (tim.hasDataFrameCount())
         setDataFrameCount(tim.getDataFrameCount().intValue());
      if (tim.dataFrames != null)
         setDataFrames2(tim.dataFrames);
      if (tim.msgID != null)
         setMsgID(OdeDSRCmsgID.valueOf(tim.msgID.name()));
      if (tim.hasPacketID())
         setPacketID(CodecUtils.toHex(tim.getPacketID().byteArrayValue()));
      if (tim.hasUrlB())
         setUrlB(tim.getUrlB().stringValue());
   }

   private void setDataFrames2(DataFrames dataFrames2) {
      dataFrames = new ArrayList<OdeAdvisoryDataFrame>();
      ArrayList<Sequence_> elements = dataFrames2.elements;
      
      for (Sequence_ element : elements) {
         if (element != null)
            dataFrames.add(new OdeAdvisoryDataFrame(element));
      }
   }

   public Integer getDataFrameCount() {
      return dataFrameCount;
   }

   public OdeTravelerInfo setDataFrameCount(Integer dataFrameCount) {
      this.dataFrameCount = dataFrameCount;
      return this;
   }

   public ArrayList<OdeAdvisoryDataFrame> getDataFrames() {
      return dataFrames;
   }

   public OdeTravelerInfo setDataFrames(ArrayList<OdeAdvisoryDataFrame> dataFrames) {
      this.dataFrames = dataFrames;
      return this;
   }

   public OdeDSRCmsgID getMsgID() {
      return msgID;
   }

   public OdeTravelerInfo setMsgID(OdeDSRCmsgID msgID) {
      this.msgID = msgID;
      return this;
   }

   public String getPacketID() {
      return packetID;
   }

   public OdeTravelerInfo setPacketID(String packetID) {
      this.packetID = packetID;
      return this;
   }

   public String getUrlB() {
      return urlB;
   }

   public OdeTravelerInfo setUrlB(String urlB) {
      this.urlB = urlB;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((dataFrameCount == null) ? 0 : dataFrameCount.hashCode());
      result = prime * result
            + ((dataFrames == null) ? 0 : dataFrames.hashCode());
      result = prime * result + ((msgID == null) ? 0 : msgID.hashCode());
      result = prime * result + ((packetID == null) ? 0 : packetID.hashCode());
      result = prime * result + ((urlB == null) ? 0 : urlB.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeTravelerInfo other = (OdeTravelerInfo) obj;
      if (dataFrameCount == null) {
         if (other.dataFrameCount != null)
            return false;
      } else if (!dataFrameCount.equals(other.dataFrameCount))
         return false;
      if (dataFrames == null) {
         if (other.dataFrames != null)
            return false;
      } else if (!dataFrames.equals(other.dataFrames))
         return false;
      if (msgID != other.msgID)
         return false;
      if (packetID == null) {
         if (other.packetID != null)
            return false;
      } else if (!packetID.equals(other.packetID))
         return false;
      if (urlB == null) {
         if (other.urlB != null)
            return false;
      } else if (!urlB.equals(other.urlB))
         return false;
      return true;
   }

   
}
