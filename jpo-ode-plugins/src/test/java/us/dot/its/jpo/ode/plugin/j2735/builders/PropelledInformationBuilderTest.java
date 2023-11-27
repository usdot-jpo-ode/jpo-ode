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

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PropelledInformation;

import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class PropelledInformationBuilderTest {

    @Test
    public void shouldTranslatePropelledinformationHuman() {
        JsonNode jsonPropulsion = null;
        try {
            jsonPropulsion = XmlUtils.toObjectNode(
                "<propulsion><human>SKATEBOARD</human><animal>UNAVAILABLE</animal><motor>UNAVAILABLE</motor></propulsion>");
        } catch (XmlUtilsException e) {
            fail("XML parsing error:" + e);
        }

        J2735PropelledInformation actualPropulsion = PropelledInformationBuilder.genericPropelledInformation(jsonPropulsion.get("propulsion"));

        assertNotNull(actualPropulsion);
        String expected = "{\"human\":\"SKATEBOARD\",\"animal\":\"UNAVAILABLE\",\"motor\":\"UNAVAILABLE\"}";
        assertEquals(expected , actualPropulsion.toString());
    }

    @Test
    public void shouldTranslatePropelledinformationAnimal() {
        JsonNode jsonPropulsion = null;
        try {
            jsonPropulsion = XmlUtils.toObjectNode(
                "<propulsion><human>UNAVAILABLE</human><animal>ANIMALDRAWNCARRIAGE</animal><motor>UNAVAILABLE</motor></propulsion>");
        } catch (XmlUtilsException e) {
            fail("XML parsing error:" + e);
        }

        J2735PropelledInformation actualPropulsion = PropelledInformationBuilder.genericPropelledInformation(jsonPropulsion.get("propulsion"));

        assertNotNull(actualPropulsion);
        String expected = "{\"human\":\"UNAVAILABLE\",\"animal\":\"ANIMALDRAWNCARRIAGE\",\"motor\":\"UNAVAILABLE\"}";
        assertEquals(expected , actualPropulsion.toString());
    }

    @Test
    public void shouldTranslatePropelledinformationMotor() {
        JsonNode jsonPropulsion = null;
        try {
            jsonPropulsion = XmlUtils.toObjectNode(
                "<propulsion><human>UNAVAILABLE</human><animal>UNAVAILABLE</animal><motor>SELFBALANCINGDEVICE</motor></propulsion>");
        } catch (XmlUtilsException e) {
            fail("XML parsing error:" + e);
        }

        J2735PropelledInformation actualPropulsion = PropelledInformationBuilder.genericPropelledInformation(jsonPropulsion.get("propulsion"));

        assertNotNull(actualPropulsion);
        String expected = "{\"human\":\"UNAVAILABLE\",\"animal\":\"UNAVAILABLE\",\"motor\":\"SELFBALANCINGDEVICE\"}";
        assertEquals(expected , actualPropulsion.toString());
    }

}
