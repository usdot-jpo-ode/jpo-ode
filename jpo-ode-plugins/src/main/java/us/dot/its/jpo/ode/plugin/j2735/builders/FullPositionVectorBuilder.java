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

import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735FullPositionVector;
import us.dot.its.jpo.ode.plugin.j2735.J2735TimeConfidence;

public class FullPositionVectorBuilder {
    
    private static final String UTC_TIME = "utcTime";
    private static final String TIME_CONFIDENCE = "timeConfidence";
    private static final String SPEED_CONFIDENCE = "speedConfidence";
    private static final String SPEED = "speed";
    private static final String POS_CONFIDENCE = "posConfidence";
    private static final String POS_ACCURACY = "posAccuracy";
    private static final String HEADING = "heading";
    private static final long LONG_LOWER_BOUND = -1799999999L;
    private static final long LONG_UPPER_BOUND = 1800000001L;
    private static final long LAT_LOWER_BOUND = -900000000;
    private static final long LAT_UPPER_BOUND = 900000001;
    private static final long ELEV_LOWER_BOUND = -4096;
    private static final long ELEV_UPPER_BOUND = 61439;
    
    private FullPositionVectorBuilder() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735FullPositionVector genericFullPositionVector(JsonNode initialPosition) {
       
       long longitude = initialPosition.get("long").asLong();
       long latitude = initialPosition.get("lat").asLong();
       
       Long elevation = null;
       if (initialPosition.get("elevation") != null) {
          elevation = initialPosition.get("elevation").asLong();
       }
       
       Long timeConfidence = null;
       if (initialPosition.get(TIME_CONFIDENCE) != null) {
          timeConfidence = initialPosition.get(TIME_CONFIDENCE).asLong();
       }
       
        // Bounds checks
        if (longitude < LONG_LOWER_BOUND 
                || longitude > LONG_UPPER_BOUND) {
            throw new IllegalArgumentException("Longitude value out of bounds [-1799999999..1800000001]");
        }
        
        if (latitude < LAT_LOWER_BOUND
                || latitude > LAT_UPPER_BOUND) {
            throw new IllegalArgumentException("Latitude value out of bounds [-900000000..900000001]");
        }
        
        if (null == elevation 
              || elevation.longValue() < ELEV_LOWER_BOUND 
              || elevation.longValue() > ELEV_UPPER_BOUND) {
            throw new IllegalArgumentException("Elevation value out of bounds [-4096..61439]");
        }
        
        if (null == timeConfidence) {
            throw new IllegalArgumentException("Time confidence value out of bounds [0..39]");
        }
        
        // Required elements
        J2735FullPositionVector fpv = new J2735FullPositionVector();
        
        fpv.setPosition(Position3DBuilder.odePosition3D(
              new DsrcPosition3D(latitude, longitude, elevation)));
        
        // Optional elements
        if (initialPosition.get(HEADING) != null) {
            fpv.setHeading(HeadingBuilder.genericHeading(initialPosition.get(HEADING)));
        }

        if (initialPosition.get(POS_ACCURACY) != null) {
            fpv.setPosAccuracy(PositionalAccuracyBuilder.genericPositionalAccuracy(initialPosition.get(POS_ACCURACY)));
        }

        if (initialPosition.get(POS_CONFIDENCE) != null) {
            fpv.setPosConfidence(PositionConfidenceSetBuilder.genericPositionConfidenceSet(initialPosition.get(POS_CONFIDENCE)));
        }

        if (initialPosition.get(SPEED) != null) {
            fpv.setSpeed(TransmissionAndSpeedBuilder.genericTransmissionAndSpeed(initialPosition.get(SPEED)));
        }

        if (initialPosition.get(SPEED_CONFIDENCE) != null) {
            fpv.setSpeedConfidence(SpeedandHeadingandThrottleConfidenceBuilder
                    .genericSpeedandHeadingandThrottleConfidence(initialPosition.get(SPEED_CONFIDENCE)));
        }

        if (initialPosition.get(TIME_CONFIDENCE) != null) {
            fpv.setTimeConfidence(J2735TimeConfidence.valueOf(initialPosition.get(TIME_CONFIDENCE).asText().replaceAll("-", "_").toUpperCase()));
        }

        if (initialPosition.get(UTC_TIME) != null) {
            fpv.setUtcTime(DDateTimeBuilder.genericDDateTime(initialPosition.get(UTC_TIME)));
        }

        return fpv;
    }

}
