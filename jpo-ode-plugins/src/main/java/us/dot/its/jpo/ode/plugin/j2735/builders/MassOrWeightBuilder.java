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

public class MassOrWeightBuilder {

    private MassOrWeightBuilder() {
       throw new UnsupportedOperationException();
    }

    public static Integer genericTrailerMass(JsonNode mass) {

        if (mass.asInt() < 0 || mass.asInt() > 255) {
            throw new IllegalArgumentException("Trailer mass out of bounds");
        }

        Integer result = null;

        if (mass.asInt() != 0) {
            result = mass.asInt() * 500;
        }

        return result;
    }

    // A data element re-used from the SAE J1939 standard and encoded as:
    // 2kg/bit, 0 deg offset, Range: 0 to +128,510kg. See SPN 180, PGN reference
    // 65258
    public static Integer genericTrailerWeight(JsonNode weight) {

        if (weight.asInt() < 0 || weight.asInt() > 64255) {
            throw new IllegalArgumentException("Trailer weight out of bounds");
        }

        return weight.asInt() * 2;
    }

    /**
     * ASN.1 Representation: VehicleMass ::= INTEGER (0..255) -- Values 000 to
     * 080 in steps of 50kg -- Values 081 to 200 in steps of 500kg -- Values 201
     * to 253 in steps of 2000kg -- The Value 254 shall be used for weights
     * above 170000 kg -- The Value 255 shall be used when the value is unknown
     * or unavailable -- Encoded such that the values: -- 81 represents 4500 kg
     * -- 181 represents 54500 kg -- 253 represents 170000 kg
     * 
     * @param mass
     * @return
     */
    public static Integer genericVehicleMass(JsonNode mass) {

        if (mass.asInt() < 0 || mass.asInt() > 255) {
            throw new IllegalArgumentException("Vehicle mass out of bounds");
        }

        Integer gmass = null;
        int imass = mass.asInt();

        if (0 <= imass && imass <= 80) {
            gmass = imass * 50;
        } else if (81 <= imass && imass <= 200) {
            gmass = 80 * 50 + (imass - 80) * 500;
        } else if (201 <= imass && imass <= 253) {
            gmass = 80 * 50 + (200 - 80) * 500 + (imass - 200) * 2000;
        } else if (254 == imass) {
            gmass = 170001;
        }

        return gmass;
    }
}
