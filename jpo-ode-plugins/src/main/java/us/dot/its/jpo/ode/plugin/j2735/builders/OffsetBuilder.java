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

public class OffsetBuilder {

    private static final Integer OFF_B12_LOWER_BOUND = -2048;
    private static final Integer OFF_B12_UPPER_BOUND = 2047;
    private static final Integer OFF_B09_LOWER_BOUND = -256;
    private static final Integer OFF_B09_UPPER_BOUND = 255;
    private static final Integer OFF_B10_LOWER_BOUND = -512;
    private static final Integer OFF_B10_UPPER_BOUND = 511;

    private OffsetBuilder() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericVertOffset_B07(JsonNode vertOffset_B07) {

        BigDecimal result;

        if (vertOffset_B07.asInt() == -64) {
            result = null;
        } else if (vertOffset_B07.asInt() >= 63) {
            result = BigDecimal.valueOf(6.3);
        } else if (vertOffset_B07.asInt() < -64) {
            result = BigDecimal.valueOf(-6.3);
        } else {
            result = BigDecimal.valueOf(vertOffset_B07.asLong(), 1);
        }

        return result;

    }

    public static BigDecimal genericOffset_B12(JsonNode offset_B12) {

        if (offset_B12.asInt() < OFF_B12_LOWER_BOUND || offset_B12.asInt() > OFF_B12_UPPER_BOUND) {
            throw new IllegalArgumentException("Offset-B12 out of bounds [-2048..2047]");
        }

        BigDecimal result = null;

        if (offset_B12.asInt() != -2048) {
            result = BigDecimal.valueOf(offset_B12.asLong(), 2);
        }

        return result;

    }

    public static BigDecimal genericOffset_B09(JsonNode offset_B09) {

        if (offset_B09.asInt() < OFF_B09_LOWER_BOUND || offset_B09.asInt() > OFF_B09_UPPER_BOUND) {
            throw new IllegalArgumentException("Offset-B09 out of bounds [-256..255]");
        }

        BigDecimal result = null;

        if (offset_B09.asInt() != -256) {
            result = BigDecimal.valueOf(offset_B09.asLong(), 2);
        }

        return result;
    }

    public static BigDecimal genericOffset_B10(JsonNode offset_B10) {

        if (offset_B10.asInt() < OFF_B10_LOWER_BOUND || offset_B10.asInt() > OFF_B10_UPPER_BOUND) {
            throw new IllegalArgumentException("Offset-B10 out of bounds [-512..511]");
        }

        BigDecimal result = null;

        if (offset_B10.asInt() != -512) {
            result = BigDecimal.valueOf(offset_B10.asLong(), 2);
        }

        return result;

    }

}
