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

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerUnitDescription;

public class TrailerUnitDescriptionBuilder {

    private static final String CRUMB_DATA = "crumbData";
    private static final String ELEVATION_OFFSET = "elevationOffset";
    private static final String REAR_WHEEL_OFFSET = "rearWheelOffset";
    private static final String REAR_PIVOT = "rearPivot";
    private static final String CENTER_OF_GRAVITY = "centerOfGravity";
    private static final String BUMPER_HEIGHTS = "bumperHeights";
    private static final String MASS = "mass";
    private static final String HEIGHT = "height";
    private static final String LENGTH = "length";
    private static final String WIDTH = "width";

    private TrailerUnitDescriptionBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735TrailerUnitDescription genericTrailerUnitDescription(JsonNode tud) {
        J2735TrailerUnitDescription gtud = new J2735TrailerUnitDescription();

        // Required elements
        gtud.setIsDolly(tud.get("isDolly").asBoolean());

        if (tud.get(WIDTH).asInt() < 0 || tud.get(WIDTH).asInt() > 1023) {
            throw new IllegalArgumentException("Trailer width value out of bounds [0..1023]");
        } else if (tud.get(WIDTH).asInt() == 0) {
            gtud.setWidth(null);
        } else {
            gtud.setWidth(tud.get(WIDTH).asInt());
        }

        if (tud.get(LENGTH).asInt() < 0 || tud.get(LENGTH).asInt() > 4095) {
            throw new IllegalArgumentException("Trailer length value out of bounds [0..4095]");
        } else if (tud.get(LENGTH).asInt() == 0) {
            gtud.setLength(null);
        } else {
            gtud.setLength(tud.get(LENGTH).asInt());
        }

        gtud.setFrontPivot(PivotPointDescriptionBuilder.genericPivotPointDescription(tud.get("frontPivot")));
        gtud.setPositionOffset(Node_XYBuilder.genericNode_XY(tud.get("positionOffset")));

        // Optional elements
        if (tud.get(HEIGHT) != null) {
            gtud.setHeight(HeightBuilder.genericHeight(tud.get(HEIGHT)));
        }
        if (tud.get(MASS) != null) {
            gtud.setMass(MassOrWeightBuilder.genericTrailerMass(tud.get(MASS)));
        }
        if (tud.get(BUMPER_HEIGHTS) != null) {
            gtud.setBumperHeights(BumperHeightsBuilder.genericBumperHeights(tud.get(BUMPER_HEIGHTS)));
        }
        if (tud.get(CENTER_OF_GRAVITY) != null) {
            gtud.setCenterOfGravity(HeightBuilder.genericHeight(tud.get(CENTER_OF_GRAVITY)));
        }
        if (tud.get(REAR_PIVOT) != null) {
            gtud.setRearPivot(PivotPointDescriptionBuilder.genericPivotPointDescription(tud.get(REAR_PIVOT)));
        }
        if (tud.get(REAR_WHEEL_OFFSET) != null) {
            gtud.setRearWheelOffset(OffsetBuilder.genericOffset_B12(tud.get(REAR_WHEEL_OFFSET)));
        }
        if (tud.get(ELEVATION_OFFSET) != null) {
            gtud.setElevationOffset(OffsetBuilder.genericVertOffset_B07(tud.get(ELEVATION_OFFSET)));
        }
        if (tud.get(CRUMB_DATA) != null ) {
            Iterator<JsonNode> iter = tud.get(CRUMB_DATA).elements();
            while (iter.hasNext()) {
                gtud.getCrumbData().add(TrailerHistoryPointBuilder.genericTrailerHistoryPoint(iter.next()));
            }
        }

        return gtud;
    }

}
