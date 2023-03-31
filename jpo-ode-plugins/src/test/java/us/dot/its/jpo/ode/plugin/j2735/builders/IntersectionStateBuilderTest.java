package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionState;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class IntersectionStateBuilderTest {

    // Common code for the tests
    private void shouldTranslateIntersectionState(final String xml, final String expectedJson) {
        JsonNode node = null;
        try {
            node = XmlUtils.toObjectNode(xml);
        } catch (XmlUtilsException e) {
            fail("XML parsing error:" + e.getMessage());
        }

        J2735IntersectionState actualIntersectionState = IntersectionStateBuilder.genericIntersectionState(node);

        // Convert expected and actual JSON to JsonNode structures to compare them
        // because we want to ignore null values and the order of keys in JSON objects
        // in the comparison, so the expected JSON string doesn't need to depend on the order of 
        // items in the Intersection Status and Maneuver Assist Bitstrings which is not
        // preserved in the JSON representation and not known before running the tests.
        JsonNode expectedJsonNode = null;
        JsonNode actualJsonNode = null;
        try {
            expectedJsonNode = JsonUtils.toObjectNode(expectedJson);
            actualJsonNode = JsonUtils.toObjectNode(JsonUtils.toJson(actualIntersectionState, false));
        } catch (JsonUtilsException e) {
            fail("Error parsing expected JSON: " + e.getMessage());
        }
        
        
        assertEquals(expectedJsonNode, actualJsonNode);
    }

    
     
    @Test
    public void shouldTranslateIntersectionState_J2735MandatoryElements() {
        final String xml = "<IntersectionState><id><id>12111</id></id><revision>0</revision><status>0000000000000000</status><states><MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed></MovementState><MovementState><signalGroup>4</signalGroup><state-time-speed><MovementEvent><eventState><stop-And-Remain/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed></MovementState></states></IntersectionState>";
        
        final String expectedJson = "{\"id\":{\"id\":12111},\"revision\":0,\"status\":{\"manualControlIsEnabled\":false,\"stopTimeIsActivated\":false,\"failureFlash\":false,\"preemptIsActive\":false,\"signalPriorityIsActive\":false,\"fixedTimeOperation\":false,\"trafficDependentOperation\":false,\"standbyOperation\":false,\"failureMode\":false,\"off\":false,\"recentMAPmessageUpdate\":false,\"recentChangeInMAPassignedLanesIDsUsed\":false,\"noValidMAPisAvailableAtThisTime\":false,\"noValidSPATisAvailableAtThisTime\":false},\"states\":{\"movementList\":[{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120}}]}},{\"signalGroup\":4,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":22120}}]}}]}}";
        
        shouldTranslateIntersectionState(xml, expectedJson);
    }



    @Test
    public void shouldTranslateIntersectionState_WithoutStatus() {
        final String xml = "<IntersectionState><id><id>12111</id></id><revision>0</revision><states><MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed></MovementState></states></IntersectionState>";
        
        final String expectedJson = "{\"id\":{\"id\":12111},\"revision\":0,\"states\":{\"movementList\":[{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120}}]}}]}}";

        shouldTranslateIntersectionState(xml, expectedJson);

       
    }

    @Test
    public void shouldTranslateIntersectionState_WithEnabledLanes() {
        final String xml = "<IntersectionState><id><id>12111</id></id><revision>0</revision><enabledLanes><LaneID>1</LaneID></enabledLanes><states><MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed></MovementState></states></IntersectionState>";
        
        final String expectedJson = "{\"id\":{\"id\":12111},\"revision\":0,\"enabledLanes\":{\"enabledLaneList\":[1]},\"states\":{\"movementList\":[{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120}}]}}]}}";

        shouldTranslateIntersectionState(xml, expectedJson);

        
    }

    @Test
    public void shouldTranslateIntersectionState_WithEnabledLanesArray() {
        final String xml = "<IntersectionState><id><id>12111</id></id><revision>0</revision><enabledLanes><LaneID>1</LaneID><LaneID>2</LaneID></enabledLanes><states><MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed></MovementState></states></IntersectionState>";
        
        final String expectedJson = "{\"id\":{\"id\":12111},\"revision\":0,\"enabledLanes\":{\"enabledLaneList\":[1,2]},\"states\":{\"movementList\":[{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120}}]}}]}}";
        
        shouldTranslateIntersectionState(xml, expectedJson);

        
    }

    @Test
    public void shouldTranslateIntersectionState_WithManeuverAssist() {
        final String xml = "<IntersectionState><id><id>12111</id></id><revision>0</revision><states><MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed></MovementState></states><maneuverAssistList><ConnectionManeuverAssist><connectionID>1</connectionID><queueLength>0</queueLength><availableStorageLength>0</availableStorageLength><waitOnStop><true/></waitOnStop><pedBicycleDetect><true/></pedBicycleDetect></ConnectionManeuverAssist></maneuverAssistList></IntersectionState>";
        final String expectedJson = "{\"id\":{\"id\":12111},\"revision\":0,\"states\":{\"movementList\":[{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120}}]}}]},\"maneuverAssistList\":{\"maneuverAssistList\":[{\"connectionID\":1,\"queueLength\":0,\"availableStorageLength\":0,\"waitOnStop\":true,\"pedBicycleDetect\":true}]}}";
        
        shouldTranslateIntersectionState(xml, expectedJson);

       
    }

    @Test
    public void shouldTranslateIntersectionState_WithManeuverAssistArray() {
        final String xml = "<IntersectionState><id><id>12111</id></id><revision>0</revision><states><MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><minEndTime>22120</minEndTime></timing></MovementEvent></state-time-speed></MovementState></states><maneuverAssistList><ConnectionManeuverAssist><connectionID>1</connectionID><queueLength>0</queueLength><availableStorageLength>0</availableStorageLength><waitOnStop><true/></waitOnStop><pedBicycleDetect><true/></pedBicycleDetect></ConnectionManeuverAssist><ConnectionManeuverAssist><connectionID>2</connectionID><queueLength>0</queueLength><availableStorageLength>0</availableStorageLength><waitOnStop><false/></waitOnStop><pedBicycleDetect><false/></pedBicycleDetect></ConnectionManeuverAssist></maneuverAssistList></IntersectionState>";
        
        final String expectedJson = "{\"id\":{\"id\":12111},\"revision\":0,\"states\":{\"movementList\":[{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"minEndTime\":22120}}]}}]},\"maneuverAssistList\":{\"maneuverAssistList\":[{\"connectionID\":1,\"queueLength\":0,\"availableStorageLength\":0,\"waitOnStop\":true,\"pedBicycleDetect\":true},{\"connectionID\":2,\"queueLength\":0,\"availableStorageLength\":0,\"waitOnStop\":false,\"pedBicycleDetect\":false}]}}";
        
        shouldTranslateIntersectionState(xml, expectedJson);

       
    }

    @Test
    public void shouldTranslateIntersectionState_WithOtherOptionalElements() {
        final String xml = "<IntersectionState><name>Intersection Name</name><id><region>0</region><id>12111</id></id><revision>0</revision><status>0000000000000000</status><moy>400000</moy><timeStamp>35176</timeStamp><states><MovementState><signalGroup>2</signalGroup><state-time-speed><MovementEvent><eventState><protected-Movement-Allowed/></eventState><timing><startTime>22120</startTime><minEndTime>22120</minEndTime><maxEndTime>22121</maxEndTime><likelyTime>22120</likelyTime><confidence>15</confidence><nextTime>22220</nextTime></timing></MovementEvent></state-time-speed></MovementState></states></IntersectionState>";
        
        final String expectedJson = "{\"name\":\"Intersection Name\",\"id\":{\"region\":0,\"id\":12111},\"revision\":0,\"status\":{\"manualControlIsEnabled\":false,\"stopTimeIsActivated\":false,\"failureFlash\":false,\"preemptIsActive\":false,\"signalPriorityIsActive\":false,\"fixedTimeOperation\":false,\"trafficDependentOperation\":false,\"standbyOperation\":false,\"failureMode\":false,\"off\":false,\"recentMAPmessageUpdate\":false,\"recentChangeInMAPassignedLanesIDsUsed\":false,\"noValidMAPisAvailableAtThisTime\":false,\"noValidSPATisAvailableAtThisTime\":false},\"moy\":400000,\"timeStamp\":35176,\"states\":{\"movementList\":[{\"signalGroup\":2,\"state_time_speed\":{\"movementEventList\":[{\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"timing\":{\"startTime\":22120,\"minEndTime\":22120,\"maxEndTime\":22121,\"likelyTime\":22120,\"confidence\":15,\"nextTime\":22220}}]}}]}}";
        
        shouldTranslateIntersectionState(xml, expectedJson);

       
    }
    
}
