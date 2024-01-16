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

@JsonPropertyOrder({ "notUsed", "frameType", "msgId", "startYear", "startTime", "durationTime", "priority",
      "notUsed1", "regions", "notUsed2", "notUsed3", "tcontent", "url" })
public class TravelerDataFrame extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private String notUsed;

   private FrameType frameType;

   private MsgId msgId;

   private String startYear;

   private String startTime;

   private String durationTime;

   private String priority;

   private String notUsed1;

   private Regions regions;

   private String notUsed2;

   private String notUsed3;

   @JsonProperty("tcontent")
   private Content tcontent;

   private String url;

   public String getNotUsed1() {
      return notUsed1;
   }

   public void setNotUsed1(String sspLocationRights) {
      this.notUsed1 = sspLocationRights;
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

   public String getDurationTime() {
      return durationTime;
   }

   public void setDurationTime(String duratonTime) {
      this.durationTime = duratonTime;
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

   public String getNotUsed3() {
      return notUsed3;
   }

   public void setNotUsed3(String sspMsgRights2) {
      this.notUsed3 = sspMsgRights2;
   }

   public String getNotUsed() {
      return notUsed;
   }

   public void setNotUsed(String sspTimRights) {
      this.notUsed = sspTimRights;
   }

   public String getNotUsed2() {
      return notUsed2;
   }

   public void setNotUsed2(String sspMsgRights1) {
      this.notUsed2 = sspMsgRights1;
   }
}
