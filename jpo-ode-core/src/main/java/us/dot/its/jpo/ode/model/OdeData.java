/*******************************************************************************
 * Copyright 2018 572682 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for ODE data objects containing metadata and payload.
 *
 * @param <MetadataT> the type of metadata associated with this data
 * @param <PayloadT> the type of payload contained in this data
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"metadata", "payload"})
public class OdeData<MetadataT extends OdeMsgMetadata, PayloadT extends OdeMsgPayload<?>>
    extends OdeObject implements OdeFilterable {
  private static final long serialVersionUID = -7711340868799607662L;

  private MetadataT metadata;
  private PayloadT payload;

  public OdeData() {
    super();
  }

  /**
   * Constructs an ODE data object with the specified metadata and payload.
   *
   * @param metadata the metadata for this data object
   * @param payload the payload for this data object
   */
  public OdeData(MetadataT metadata, PayloadT payload) {
    super();
    this.metadata = metadata;
    this.payload = payload;
    this.metadata.setPayloadType(payload.getClass().getName());
  }
}
