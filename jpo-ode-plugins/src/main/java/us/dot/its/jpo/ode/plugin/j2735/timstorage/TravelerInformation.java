/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.plugin.j2735.timstorage;

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
   private DataFrames dataFrames;

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


   public DataFrames getDataFrames() {
     return dataFrames;
   }

   public void setDataFrames(DataFrames dataFrames) {
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
    result = prime * result + ((dataFrames == null) ? 0 : dataFrames.hashCode());
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
    if (dataFrames == null) {
      if (other.dataFrames != null)
        return false;
    } else if (!dataFrames.equals(other.dataFrames))
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
