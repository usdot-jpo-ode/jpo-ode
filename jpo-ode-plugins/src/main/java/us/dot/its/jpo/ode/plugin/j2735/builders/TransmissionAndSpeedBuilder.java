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
package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionAndSpeed;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;

public class TransmissionAndSpeedBuilder {
    
    private TransmissionAndSpeedBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735TransmissionAndSpeed genericTransmissionAndSpeed(JsonNode ts) {
        J2735TransmissionAndSpeed gts = new J2735TransmissionAndSpeed();

        JsonNode speed = ts.get("speed");
		if(speed != null)
		{
            gts.setSpeed(SpeedOrVelocityBuilder.genericVelocity(speed));
        }

        // The typo is deliberate as defined by J2735 TransmissionAndSpeed
        JsonNode transmisson = ts.get("transmisson");
		if(transmisson != null)
		{
            gts.setTransmisson(J2735TransmissionState.valueOf(transmisson.fieldNames().next().toUpperCase()));	
		}

        return gts;
    }

}