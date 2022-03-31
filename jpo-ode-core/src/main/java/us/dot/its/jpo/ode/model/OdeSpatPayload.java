package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.*;

import us.dot.its.jpo.ode.plugin.j2735.J2735SPAT;

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

public class OdeSpatPayload extends OdeMsgPayload {

	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OdeSpatPayload() {
	        this(new J2735SPAT());
	    }

		@JsonCreator
	    public OdeSpatPayload( @JsonProperty("data") J2735SPAT spat) {
	        super(spat);
	        this.setData(spat);
	    }

		@JsonProperty("data")
	    public J2735SPAT getSpat() {
	        return (J2735SPAT) getData();
	    }

	    public void setSpat(J2735SPAT spat) {
	        setData(spat);
	    }
}
