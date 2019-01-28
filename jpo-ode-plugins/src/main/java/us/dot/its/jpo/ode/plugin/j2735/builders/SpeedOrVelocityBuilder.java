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

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class SpeedOrVelocityBuilder {

    private SpeedOrVelocityBuilder() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericSpeed(JsonNode speed) {
        return genericSpeedOrVelocity(speed.asInt());
    }

    public static BigDecimal genericVelocity(JsonNode velocity) {
        return genericSpeedOrVelocity(velocity.asInt());
    }

    public static BigDecimal genericSpeedOrVelocity(int speedOrVelocity) {

        if (speedOrVelocity < 0 || speedOrVelocity > 8191) {
            throw new IllegalArgumentException("Speed or velocity out of bounds");
        }

        BigDecimal result = null;

        if (speedOrVelocity != 8191) {
            result = BigDecimal.valueOf(speedOrVelocity * (long) 2, 2);
        }

        return result;

    }
}
