package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import mockit.Expectations;
import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.util.JsonUtils;

public class Position3DBuilderTest {

   @Test
   public void testJsonNodeExtractor() {
      ObjectNode node = JsonUtils.newNode();
      node.put("lat", 604739946);
      node.put("long", -1009691905);
      node.put("elevation", 14843);
      
      DsrcPosition3D result = Position3DBuilder.dsrcPosition3D(node);
      
      assertEquals(Long.valueOf(604739946), result.getLatitude());
      assertEquals(Long.valueOf(-1009691905), result.getLongitude());
      assertEquals(Long.valueOf(14843), result.getElevation());
   }
   
   @Test
   public void testNullDsrcPosition3D() {
      DsrcPosition3D input = new DsrcPosition3D();
      OdePosition3D result = Position3DBuilder.odePosition3D(input);
      
      assertNull(result.getLatitude());
      assertNull(result.getLongitude());
      assertNull(result.getElevation());
   }
   
   @Test
   public void testOdePosition3DPopulation() {
      OdePosition3D result = Position3DBuilder.odePosition3D(new DsrcPosition3D(Long.valueOf(54), Long.valueOf(65), Long.valueOf(76)));
      
      assertEquals(BigDecimal.valueOf(0.0000054), result.getLatitude());
      assertEquals(BigDecimal.valueOf(0.0000065), result.getLongitude());
      assertEquals(BigDecimal.valueOf(7.6), result.getElevation());
   }

}
