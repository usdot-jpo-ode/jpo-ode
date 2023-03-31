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

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;

public class BsmCoreDataBuilder {
    
    private BsmCoreDataBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735BsmCoreData genericBsmCoreData(JsonNode coreData) {
        J2735BsmCoreData genericBsmCoreData = new J2735BsmCoreData();

        genericBsmCoreData.setMsgCnt(coreData.get("msgCnt").asInt());

        genericBsmCoreData.setId(coreData.get("id").asText());

        if (coreData.get("secMark").asInt() != 65535) {
            genericBsmCoreData.setSecMark(coreData.get("secMark").asInt());
        } else {
            genericBsmCoreData.setSecMark(null);
        }

         genericBsmCoreData.setPosition(new OdePosition3D(LatitudeBuilder.genericLatitude(coreData.get("lat")),
               LongitudeBuilder.genericLongitude(coreData.get("long")),
               ElevationBuilder.genericElevation(coreData.get("elev"))));

        genericBsmCoreData.setAccelSet(AccelerationSet4WayBuilder.genericAccelerationSet4Way(coreData.get("accelSet")));

        genericBsmCoreData.setAccuracy(PositionalAccuracyBuilder.genericPositionalAccuracy(coreData.get("accuracy")));

        JsonNode transmission = coreData.get("transmission");
        if (transmission != null) {
            J2735TransmissionState enumTransmission; 
            try {
                enumTransmission = J2735TransmissionState.valueOf(transmission.fieldNames().next().toUpperCase());
            } catch (IllegalArgumentException e) {
                enumTransmission = J2735TransmissionState.UNAVAILABLE;
            }
            genericBsmCoreData.setTransmission(enumTransmission);
        }

        // speed is received in units of 0.02 m/s
        genericBsmCoreData.setSpeed(SpeedOrVelocityBuilder.genericSpeed(coreData.get("speed")));

        JsonNode heading = coreData.get("heading");
        if (heading != null) {
            // Heading ::= INTEGER (0..28800)
            // -- LSB of 0.0125 degrees
            // -- A range of 0 to 359.9875 degrees
            genericBsmCoreData.setHeading(HeadingBuilder.genericHeading(heading));
        }

        genericBsmCoreData.setAngle(steeringAngle(coreData.get("angle")));
        genericBsmCoreData.setBrakes(BrakeSystemStatusBuilder.genericBrakeSystemStatus(coreData.get("brakes")));
        genericBsmCoreData.setSize(VehicleSizeBuilder.genericVehicleSize(coreData.get("size")));

        return genericBsmCoreData;
    }

    private static BigDecimal steeringAngle(JsonNode steeringWheelAngle) {
        // SteeringWheelAngle ::= OCTET STRING (SIZE(1))
        // -- LSB units of 1.5 degrees.
        // -- a range of -189 to +189 degrees
        // -- 0x01 = 00 = +1.5 deg
        // -- 0x81 = -126 = -189 deg and beyond
        // -- 0x7E = +126 = +189 deg and beyond
        // -- 0x7F = +127 to be used for unavailable
        BigDecimal angle = null;
        if (steeringWheelAngle != null && steeringWheelAngle.asInt() != 0x7F) {
            angle = BigDecimal.valueOf(steeringWheelAngle.asInt() * (long)15, 1);
        }
        return angle;
    }

}
