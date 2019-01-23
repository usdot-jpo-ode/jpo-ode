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

import us.dot.its.jpo.ode.plugin.j2735.J2735DDateTime;

public class DDateTimeBuilder {

    private static final int YEAR_LOWER_BOUND = 0;
    private static final int YEAR_UPPER_BOUND = 4095;
    private static final int MONTH_LOWER_BOUND = 0;
    private static final int MONTH_UPPER_BOUND = 12;
    private static final int DAY_LOWER_BOUND = 0;
    private static final int DAY_UPPER_BOUND = 31;
    private static final int HOUR_LOWER_BOUND = 0;
    private static final int HOUR_UPPER_BOUND = 31;
    private static final int MINUTE_LOWER_BOUND = 0;
    private static final int MINUTE_UPPER_BOUND = 60;
    private static final int SECOND_LOWER_BOUND = 0;
    private static final int SECOND_UPPER_BOUND = 65535;
    private static final int OFFSET_LOWER_BOUND = -840;
    private static final int OFFSET_UPPER_BOUND = 840;

    private DDateTimeBuilder() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735DDateTime genericDDateTime(JsonNode dDateTime) {

        // Bounds checks
       int year = dDateTime.get("year").asInt();
        if (year < YEAR_LOWER_BOUND || year > YEAR_UPPER_BOUND) {
            throw new IllegalArgumentException("Year value out of bounds [0..4095]");
        }
        
        int month = dDateTime.get("month").asInt();
        if (month < MONTH_LOWER_BOUND || month > MONTH_UPPER_BOUND) {
            throw new IllegalArgumentException("Month value out of bounds [0..12]");
        }
        
        int day = dDateTime.get("day").asInt();
        if (day < DAY_LOWER_BOUND || day > DAY_UPPER_BOUND) {
            throw new IllegalArgumentException("Day value out of bounds [0..31]");
        }
        
        int hour = dDateTime.get("hour").asInt();
        if (hour < HOUR_LOWER_BOUND || hour > HOUR_UPPER_BOUND) {
            throw new IllegalArgumentException("Hour value out of bounds [0..31]");
        }
        
        int minute = dDateTime.get("minute").asInt();
        if (minute < MINUTE_LOWER_BOUND || minute > MINUTE_UPPER_BOUND) {
            throw new IllegalArgumentException("Minute value out of bounds [0..60]");
        }
        
        int second = dDateTime.get("second").asInt();
        if (second < SECOND_LOWER_BOUND || second > SECOND_UPPER_BOUND) {
            throw new IllegalArgumentException("Second value out of bounds [0..65535]");
        }
        
        int offset = dDateTime.get("offset").asInt();
        if (offset < OFFSET_LOWER_BOUND || offset > OFFSET_UPPER_BOUND) {
            throw new IllegalArgumentException("Offset value out of bounds [-840..840]");
        }

        // DDateTime creation
        J2735DDateTime dt = new J2735DDateTime();

        if (year != 0) {
            dt.setYear(year);
        }

        if (month != 0) {
            dt.setMonth(month);
        }

        if (day != 0) {
            dt.setDay(day);
        }

        if (hour != 31) {
            dt.setHour(hour);
        }

        if (minute != 60) {
            dt.setMinute(minute);
        }

        if (second != 65535) {
            dt.setSecond(second);
        }

        dt.setOffset(offset);

        return dt;
    }

}
