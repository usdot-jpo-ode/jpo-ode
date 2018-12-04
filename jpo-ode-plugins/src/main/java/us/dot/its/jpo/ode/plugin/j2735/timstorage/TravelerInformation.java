package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import java.util.Arrays;

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
   private TravelerDataFrame[] dataFrames;

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

   public TravelerDataFrame[] getDataFrames() {
    return dataFrames;
  }

  public void setDataFrames(TravelerDataFrame[] dataFrames) {
    this.dataFrames = dataFrames;
  }

  public String getMsgCnt() {
      return msgCnt;
   }

   public void setMsgCnt(String msgCnt) {
      this.msgCnt = msgCnt;
   }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(dataFrames);
    result = prime * result + ((msgCnt == null) ? 0 : msgCnt.hashCode());
    result = prime * result + ((packetID == null) ? 0 : packetID.hashCode());
    result = prime * result + timeStamp;
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
    TravelerInformation other = (TravelerInformation) obj;
    if (!Arrays.equals(dataFrames, other.dataFrames))
      return false;
    if (msgCnt == null) {
      if (other.msgCnt != null)
        return false;
    } else if (!msgCnt.equals(other.msgCnt))
      return false;
    if (packetID == null) {
      if (other.packetID != null)
        return false;
    } else if (!packetID.equals(other.packetID))
      return false;
    if (timeStamp != other.timeStamp)
      return false;
    if (urlB == null) {
      if (other.urlB != null)
        return false;
    } else if (!urlB.equals(other.urlB))
      return false;
    return true;
  }

}
