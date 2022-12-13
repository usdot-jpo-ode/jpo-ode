package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735MovementEvent;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class MovementEventBuilderTest {

// Common code for the tests
    private void shouldTranslateMovementEvent(final String xml, final String expectedJson) {
        JsonNode node = null;
        try {
            node = XmlUtils.toObjectNode(xml).get("MovementEvent");
        } catch (XmlUtilsException e) {
            fail("XML parsing error: " + e.getMessage());
        }

        final J2735MovementEvent actualMovementEvent = MovementEventBuilder.genericMovementState(node);
        
        assertEquals(expectedJson, actualMovementEvent.toJson(false));
    }

    @Test
    public void shouldTranslateMovementEvent() {
        final String xml = "<MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><startTime>22120</startTime><minEndTime>22120</minEndTime><maxEndTime>22121</maxEndTime><likelyTime>22120</likelyTime><confidence>15</confidence><nextTime>22220</nextTime></timing></MovementEvent>";

        final String expectedJson = "{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"startTime\":22120,\"minEndTime\":22120,\"maxEndTime\":22121,\"likelyTime\":22120,\"confidence\":15,\"nextTime\":22220}}";

        shouldTranslateMovementEvent(xml, expectedJson);
    }
    
    @Test
    public void shouldTranslateMovementEvent_WithAdvisorySpeed() {
        final String xml = "<MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><startTime>22120</startTime><minEndTime>22120</minEndTime><maxEndTime>22121</maxEndTime><likelyTime>22120</likelyTime><confidence>15</confidence><nextTime>22220</nextTime></timing><speeds><AdvisorySpeed><type><transit/></type><speed>250</speed><confidence><prec1ms/></confidence><distance>100</distance><class>1</class></AdvisorySpeed></speeds></MovementEvent>";

        final String expectedJson = "{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"startTime\":22120,\"minEndTime\":22120,\"maxEndTime\":22121,\"likelyTime\":22120,\"confidence\":15,\"nextTime\":22220},\"speeds\":{\"advisorySpeedList\":[{\"type\":\"TRANSIT\",\"speed\":250,\"confidence\":\"PREC1MS\",\"distance\":100,\"classId\":1}]}}";

        shouldTranslateMovementEvent(xml, expectedJson);
    }

    @Test
    public void shouldTranslateMovementEvent_WithAdvisorySpeedArray() {
        final String xml = "<MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><startTime>22120</startTime><minEndTime>22120</minEndTime><maxEndTime>22121</maxEndTime><likelyTime>22120</likelyTime><confidence>15</confidence><nextTime>22220</nextTime></timing><speeds><AdvisorySpeed><type><transit/></type><speed>250</speed><confidence><prec1ms/></confidence><distance>100</distance><class>1</class></AdvisorySpeed><AdvisorySpeed><type><greenwave/></type><speed>250</speed><confidence><prec100ms/></confidence><distance>50</distance><class>1</class></AdvisorySpeed></speeds></MovementEvent>";

        final String expectedJson = "{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"startTime\":22120,\"minEndTime\":22120,\"maxEndTime\":22121,\"likelyTime\":22120,\"confidence\":15,\"nextTime\":22220},\"speeds\":{\"advisorySpeedList\":[{\"type\":\"TRANSIT\",\"speed\":250,\"confidence\":\"PREC1MS\",\"distance\":100,\"classId\":1},{\"type\":\"GREENWAVE\",\"speed\":250,\"confidence\":\"PREC100MS\",\"distance\":50,\"classId\":1}]}}";

        shouldTranslateMovementEvent(xml, expectedJson);
    }

}
