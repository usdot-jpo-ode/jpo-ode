package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735MovementState;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class MovementStateBuilderTest {
    

    // Common code for the tests
    private void shouldTranslateMovementState(final String xml, final String expectedJson) {
        JsonNode node = null;
        try {
            node = XmlUtils.toObjectNode(xml).get("MovementState");
        } catch (XmlUtilsException e) {
            fail("XML parsing error: " + e.getMessage());
        }

        final J2735MovementState actualMovementState = MovementStateBuilder.genericMovementState(node);
        
        assertEquals(expectedJson, actualMovementState.toJson(false));
    }

    @Test
    public void shouldTranslateMovementState_J2735MandatoryElements() {
        final String xml = "<MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed></MovementState>";

        final String expectedJson = "{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120}}]}}";

        shouldTranslateMovementState(xml, expectedJson);
    }

    

    @Test
    public void shouldTranslateMovementState_WithManeuverAssist() {
        final String xml = "<MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed><maneuverAssistList><ConnectionManeuverAssist><connectionID>1</connectionID><queueLength>20</queueLength><availableStorageLength>10</availableStorageLength><waitOnStop><false/></waitOnStop><pedBicycleDetect><false/></pedBicycleDetect></ConnectionManeuverAssist></maneuverAssistList></MovementState>";

        final String expectedJson = "{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120}}]},\"maneuverAssistList\":{\"maneuverAssistList\":[{\"connectionID\":1,\"queueLength\":20,\"availableStorageLength\":10,\"waitOnStop\":false,\"pedBicycleDetect\":false}]}}";

        shouldTranslateMovementState(xml, expectedJson);
    }

    @Test 
    public void shouldTranslateMovementState_WithManeuverAssistArray() {
        final String xml = "<MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed><maneuverAssistList><ConnectionManeuverAssist><connectionID>1</connectionID><queueLength>20</queueLength><availableStorageLength>10</availableStorageLength><waitOnStop><true/></waitOnStop><pedBicycleDetect><true/></pedBicycleDetect></ConnectionManeuverAssist><ConnectionManeuverAssist><connectionID>2</connectionID><queueLength>20</queueLength><availableStorageLength>10</availableStorageLength><waitOnStop><true/></waitOnStop><pedBicycleDetect><true/></pedBicycleDetect></ConnectionManeuverAssist></maneuverAssistList></MovementState>";

        final String expectedJson = "{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120}}]},\"maneuverAssistList\":{\"maneuverAssistList\":[{\"connectionID\":1,\"queueLength\":20,\"availableStorageLength\":10,\"waitOnStop\":true,\"pedBicycleDetect\":true},{\"connectionID\":2,\"queueLength\":20,\"availableStorageLength\":10,\"waitOnStop\":true,\"pedBicycleDetect\":true}]}}";

        shouldTranslateMovementState(xml, expectedJson);
    }

    
}
