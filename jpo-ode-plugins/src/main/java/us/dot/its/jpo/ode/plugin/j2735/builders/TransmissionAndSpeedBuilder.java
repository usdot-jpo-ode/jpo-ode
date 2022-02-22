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

        gts.setSpeed(SpeedOrVelocityBuilder.genericVelocity(ts.get("speed")));

        System.out.println("TransmissionAndSpeedBuilder: Trying to configure transmission...");
        JsonNode trans = ts.get("transmission");
        System.out.println("Transmission: " + trans.toString());
        String transValue = trans.fieldNames().next();
        System.out.println("Transmission value: " + transValue);
        System.out.println("Transmission value.toUpperCase: " + transValue.toUpperCase());
        J2735TransmissionState transmissionState = J2735TransmissionState.valueOf(transValue.toUpperCase());
        System.out.println("Transmission object: " + transmissionState.name());
        gts.setTransmisson(transmissionState);

        return gts;
    }

}
