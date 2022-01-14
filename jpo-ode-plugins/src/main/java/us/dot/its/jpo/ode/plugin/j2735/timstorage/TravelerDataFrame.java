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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@JsonPropertyOrder({ "sspTimRights", "frameType", "msgId", "startYear", "startTime", "duratonTime", "priority",
      "sspLocationRights", "regions", "sspMsgRights1", "sspMsgRights2", "tcontent", "url" })
public class TravelerDataFrame extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private String sspTimRights;

   private FrameType frameType;

   private MsgId msgId;

   private String startYear;

   private String startTime;

   private String duratonTime;

   private String priority;

   private String sspLocationRights;

   private Regions regions;

   private String sspMsgRights1;

   private String sspMsgRights2;

   @JsonProperty("tcontent")
   private Content tcontent;

   private String url;

   public String getSspLocationRights() {
      return sspLocationRights;
   }

   public void setSspLocationRights(String sspLocationRights) {
      this.sspLocationRights = sspLocationRights;
   }


   public Regions getRegions() {
     return regions;
   }

   public void setRegions(Regions regions) {
     this.regions = regions;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public FrameType getFrameType() {
      return frameType;
   }

   public void setFrameType(FrameType frameType) {
      this.frameType = frameType;
   }

   public String getDuratonTime() {
      return duratonTime;
   }

   public void setDuratonTime(String duratonTime) {
      this.duratonTime = duratonTime;
   }

   @JsonIgnore
   public Content getContent() {
      return tcontent;
   }

   public Content getTcontent() {
     return tcontent;
   }

   public void setTcontent(Content tcontent) {
     this.tcontent = tcontent;
   }

   public void setContent(Content content) {
      this.tcontent = content;
   }

   public String getStartTime() {
      return startTime;
   }

   public void setStartTime(String startTime) {
      this.startTime = startTime;
   }

   public String getStartYear() {
      return startYear;
   }

   public void setStartYear(String startYear) {
      this.startYear = startYear;
   }

   public String getPriority() {
      return priority;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

   public MsgId getMsgId() {
      return msgId;
   }

   public void setMsgId(MsgId msgId) {
      this.msgId = msgId;
   }

   public String getSspMsgRights2() {
      return sspMsgRights2;
   }

   public void setSspMsgRights2(String sspMsgRights2) {
      this.sspMsgRights2 = sspMsgRights2;
   }

   public String getSspTimRights() {
      return sspTimRights;
   }

   public void setSspTimRights(String sspTimRights) {
      this.sspTimRights = sspTimRights;
   }

   public String getSspMsgRights1() {
      return sspMsgRights1;
   }

   public void setSspMsgRights1(String sspMsgRights1) {
      this.sspMsgRights1 = sspMsgRights1;
   }
}
