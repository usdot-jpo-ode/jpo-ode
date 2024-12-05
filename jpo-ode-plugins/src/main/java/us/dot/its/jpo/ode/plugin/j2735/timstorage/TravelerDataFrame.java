/*******************************************************************************
 * Copyright 2018 572682.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at</p>
 *
 *   <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.</p>
 ******************************************************************************/

package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * Traveler data frame.
 */
@JsonPropertyOrder({"doNotUse1", "frameType", "msgId", "startYear", "startTime", "durationTime",
    "priority",
    "doNotUse2", "regions", "doNotUse3", "doNotUse4", "tcontent", "url"})
@EqualsAndHashCode(callSuper = false)
public class TravelerDataFrame extends Asn1Object {
  private static final long serialVersionUID = 1L;
  @JsonAlias({"sspTimRights", "notUsed"})
  private String doNotUse1;
  private FrameType frameType;
  private MsgId msgId;
  private String startYear;
  private String startTime;
  @JsonAlias("duratonTime")
  private String durationTime;
  private String priority;
  @JsonAlias({"sspLocationRights", "notUsed1"})
  private String doNotUse2;
  private Regions regions;
  @JsonAlias({"sspMsgContent", "sspMsgRights1", "notUsed2"})
  private String doNotUse3;
  @JsonAlias({"sspMsgTypes", "sspMsgRights2", "notUsed3"})
  private String doNotUse4;
  @JsonProperty("tcontent")
  private Content tcontent;
  private String url;

  public String getDoNotUse2() {
    return doNotUse2;
  }

  public void setDoNotUse2(String doNotUse2) {
    this.doNotUse2 = doNotUse2;
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

  public String getDoNotUse4() {
    return doNotUse4;
  }

  public void setDoNotUse4(String doNotUse4) {
    this.doNotUse4 = doNotUse4;
  }

  public String getDoNotUse1() {
    return doNotUse1;
  }

  public void setDoNotUse1(String doNotUse1) {
    this.doNotUse1 = doNotUse1;
  }

  public String getDoNotUse3() {
    return doNotUse3;
  }

  public void setDoNotUse3(String doNotUse3) {
    this.doNotUse3 = doNotUse3;
  }
}
