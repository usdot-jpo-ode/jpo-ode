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

import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerHistoryPoint;

public class TrailerHistoryPointBuilder {

    private TrailerHistoryPointBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735TrailerHistoryPoint genericTrailerHistoryPoint(JsonNode thp) {
        J2735TrailerHistoryPoint gthp = new J2735TrailerHistoryPoint();

        gthp.setElevationOffset(OffsetBuilder.genericVertOffset_B07(thp.get("elevationOffset")));
        gthp.setHeading(HeadingBuilder.genericHeading(thp.get("heading")));
        gthp.setPivotAngle(AngleBuilder.genericAngle(thp.get("pivotAngle")));
        gthp.setPositionOffset(Node_XYBuilder.genericNode_XY(thp.get("positionOffset")));
        gthp.setTimeOffset(TimeOffsetBuilder.genericTimeOffset(thp.get("timeOffset")));

        return gthp;
    }

}
