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

import com.fasterxml.jackson.annotation.*;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class OdeBsmPayload extends OdeMsgPayload<OdeObject> {

    private static final long serialVersionUID = 7061315628111448390L;

    public OdeBsmPayload() {
        this(new J2735Bsm());
    }

    @JsonCreator
    public OdeBsmPayload( @JsonProperty("data") J2735Bsm bsm) {
        super(bsm);
        this.setData(bsm);
    }

    @JsonProperty("data")
    public J2735Bsm getBsm() {
        return (J2735Bsm) getData();
    }

    public void setBsm(J2735Bsm bsm) {
        setData(bsm);
    }

}
