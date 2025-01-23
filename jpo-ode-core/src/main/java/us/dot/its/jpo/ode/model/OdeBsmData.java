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

package us.dot.its.jpo.ode.model;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;

/**
 * Represents a specialized type of data in the Open Data Environment (ODE) system, specifically
 * related to Basic Safety Messages (BSM). This class extends OdeData and provides BSM-specific
 * metadata and payload handling.
 */
@EqualsAndHashCode(callSuper = false)
public class OdeBsmData extends OdeData {

  private static final long serialVersionUID = 4944935387116447760L;

  public OdeBsmData() {
    super();
  }

  public OdeBsmData(OdeMsgMetadata metadata, OdeMsgPayload payload) {
    super(metadata, payload);
  }

  @Override
  @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY, defaultImpl = OdeBsmMetadata.class)
  public void setMetadata(OdeMsgMetadata metadata) {
    super.setMetadata(metadata);
  }

  @Override
  @JsonTypeInfo(use = Id.CLASS, include = As.EXISTING_PROPERTY, defaultImpl = OdeBsmPayload.class)
  public void setPayload(OdeMsgPayload payload) {
    super.setPayload(payload);
  }

}
