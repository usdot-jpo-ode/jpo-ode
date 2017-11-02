package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735WiperSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperStatus;
import us.dot.its.jpo.ode.util.JsonUtils;

public class WiperSetBuilderTest {

   @Test
   public void testRequiredConversions() {
      
      J2735WiperStatus expectedWiperStatusFront = J2735WiperStatus.OFF;
      Integer expectedWiperRateFront = 55;
      
      ObjectNode testInput = JsonUtils.newNode();
      
      testInput.put("statusFront", 1);
      testInput.put("rateFront", 55);
      
      J2735WiperSet actualValue = WiperSetBuilder.genericWiperSet(testInput);
      
      assertEquals(expectedWiperStatusFront, actualValue.getStatusFront());
      assertEquals(expectedWiperRateFront, actualValue.getRateFront());
   }
   
   @Test
   public void testOptionalConversions() {
      
      J2735WiperStatus expectedWiperStatusRear = J2735WiperStatus.AUTOMATICPRESENT;
      Integer expectedWiperRateRear = 12;
      
      ObjectNode testInput = JsonUtils.newNode();
      
      testInput.put("statusFront", 1);
      testInput.put("rateFront", 55);
      testInput.put("statusRear", 6);
      testInput.put("rateRear", 12);
      
      J2735WiperSet actualValue = WiperSetBuilder.genericWiperSet(testInput);
      
      assertEquals(expectedWiperStatusRear, actualValue.getStatusRear());
      assertEquals(expectedWiperRateRear, actualValue.getRateRear());
   }

}
