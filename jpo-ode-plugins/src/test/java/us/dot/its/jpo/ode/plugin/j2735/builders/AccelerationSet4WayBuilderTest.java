package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735AccelerationSet4Way;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class AccelerationSet4WayBuilderTest {

   @Test
   public void testConversion() {

      JsonNode jsonAccelSet = null;
      try {
         jsonAccelSet = (JsonNode) XmlUtils.fromXmlS(
               "<accelSet><long>2001</long><lat>2001</lat><vert>-127</vert><yaw>0</yaw></accelSet>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735AccelerationSet4Way actualAccelSet = AccelerationSet4WayBuilder.genericAccelerationSet4Way(jsonAccelSet);

      assertEquals("string", actualAccelSet.toJson());

   }
}
