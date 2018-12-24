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

    private TrailerUnitDescriptionBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735TrailerUnitDescription genericTrailerUnitDescription(JsonNode tud) {
        J2735TrailerUnitDescription gtud = new J2735TrailerUnitDescription();

        // Required elements
        gtud.setIsDolly(tud.get("isDolly").asBoolean());

        if (tud.get("width").asInt() < 0 || tud.get("width").asInt() > 1023) {
            throw new IllegalArgumentException("Trailer width value out of bounds [0..1023]");
        } else if (tud.get("width").asInt() == 0) {
            gtud.setWidth(null);
        } else {
            gtud.setWidth(tud.get("width").asInt());
        }

        if (tud.get("length").asInt() < 0 || tud.get("length").asInt() > 4095) {
            throw new IllegalArgumentException("Trailer length value out of bounds [0..4095]");
        } else if (tud.get("length").asInt() == 0) {
            gtud.setLength(null);
        } else {
            gtud.setLength(tud.get("length").asInt());
        }

        gtud.setFrontPivot(PivotPointDescriptionBuilder.genericPivotPointDescription(tud.get("frontPivot")));
        gtud.setPositionOffset(Node_XYBuilder.genericNode_XY(tud.get("positionOffset")));

        // Optional elements
        if (tud.get("height") != null) {
            gtud.setHeight(HeightBuilder.genericHeight(tud.get("height")));
        }
        if (tud.get("mass") != null) {
            gtud.setMass(MassOrWeightBuilder.genericTrailerMass(tud.get("mass")));
        }
        if (tud.get("bumperHeights") != null) {
            gtud.setBumperHeights(BumperHeightsBuilder.genericBumperHeights(tud.get("bumperHeights")));
        }
        if (tud.get("centerOfGravity") != null) {
            gtud.setCenterOfGravity(HeightBuilder.genericHeight(tud.get("centerOfGravity")));
        }
        if (tud.get("rearPivot") != null) {
            gtud.setRearPivot(PivotPointDescriptionBuilder.genericPivotPointDescription(tud.get("rearPivot")));
        }
        if (tud.get("rearWheelOffset") != null) {
            gtud.setRearWheelOffset(OffsetBuilder.genericOffset_B12(tud.get("rearWheelOffset")));
        }
        if (tud.get("elevationOffset") != null) {
            gtud.setElevationOffset(OffsetBuilder.genericVertOffset_B07(tud.get("elevationOffset")));
        }
        if (tud.get("crumbData") != null ) {
            Iterator<JsonNode> iter = tud.get("crumbData").elements();
            while (iter.hasNext()) {
                gtud.getCrumbData().add(TrailerHistoryPointBuilder.genericTrailerHistoryPoint(iter.next()));
            }
        }

        return gtud;
    }

}
