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
package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({ "metadata", "payload" })
public class OdeData<TPayload extends OdeMsgPayload<?>> extends OdeObject implements OdeFilterable {
    private static final long serialVersionUID = -7711340868799607662L;

    private OdeMsgMetadata metadata;
    private TPayload payload;

    public OdeData() {
        super();
    }

    public OdeData(OdeMsgMetadata metadata, TPayload payload) {
        super();
        this.metadata = metadata;
        this.payload = payload;
        this.metadata.setPayloadType(payload.getClass().getName());
    }

}