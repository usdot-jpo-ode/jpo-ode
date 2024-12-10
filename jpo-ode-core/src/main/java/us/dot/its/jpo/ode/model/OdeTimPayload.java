/*******************************************************************************
 * Copyright 2018 572682
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage;
import us.dot.its.jpo.ode.plugin.j2735.travelerinformation.TravelerInformation;

/**
 * ODE TIM payload class for both J2735 TravelerInformation and ODE 
 * TIM Creator OdeTravelerInformationMessage.
 */
public class OdeTimPayload extends OdeMsgPayload {

  private static final long serialVersionUID = 7061315628111448390L;

  public OdeTimPayload() {
    this(new OdeTravelerInformationMessage());
  }

  public OdeTimPayload(OdeTravelerInformationMessage tim) {
    super(tim);
    this.setData(tim);
  }

  @JsonCreator
  public OdeTimPayload(@JsonProperty("data") TravelerInformation tim) {
    super(tim);
    this.setData(tim);
  }
}
