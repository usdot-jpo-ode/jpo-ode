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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * Traveler data frame.
 */
@JsonPropertyOrder({"doNotUse1", "frameType", "msgId", "startYear", "startTime", "durationTime",
    "priority",
    "doNotUse2", "regions", "doNotUse3", "doNotUse4", "tcontent", "url"})
@EqualsAndHashCode(callSuper = false)
@Data
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
}
