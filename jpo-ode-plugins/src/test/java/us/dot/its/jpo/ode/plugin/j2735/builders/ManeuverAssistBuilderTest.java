package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735ConnectionManueverAssist;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class ManeuverAssistBuilderTest {

    // Common code for the tests
    private void shouldTranslateManeuverAssist(final String xml, final String expectedJson) {
        JsonNode node = null;
        try {
            node = XmlUtils.toObjectNode(xml).get("ConnectionManeuverAssist");
        } catch (XmlUtilsException e) {
            fail("XML parsing error:" + e.getMessage());
        }

        final J2735ConnectionManueverAssist actualManeuverAssist = ManeuverAssistBuilder.genericManeuverAssist(node);
        
        assertEquals(expectedJson, actualManeuverAssist.toJson(false));
    }

    @Test
    public void shouldTranslateManeuverAssist_WithTrueBooleans() {
        String xml = "<ConnectionManeuverAssist><connectionID>1</connectionID><queueLength>20</queueLength><availableStorageLength>10</availableStorageLength><waitOnStop><true/></waitOnStop><pedBicycleDetect><true/></pedBicycleDetect></ConnectionManeuverAssist>";

        String expectedJson = "{\"connectionID\":1,\"queueLength\":20,\"availableStorageLength\":10,\"waitOnStop\":true,\"pedBicycleDetect\":true}";

        shouldTranslateManeuverAssist(xml, expectedJson);
    }

    @Test
    public void shouldTranslateManeuverAssist_WithFalseBooleans() {
        String xml = "<ConnectionManeuverAssist><connectionID>1</connectionID><queueLength>20</queueLength><availableStorageLength>10</availableStorageLength><waitOnStop><false/></waitOnStop><pedBicycleDetect><false/></pedBicycleDetect></ConnectionManeuverAssist>";

        String expectedJson = "{\"connectionID\":1,\"queueLength\":20,\"availableStorageLength\":10,\"waitOnStop\":false,\"pedBicycleDetect\":false}";

        shouldTranslateManeuverAssist(xml, expectedJson);
    }
}
