package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeAppliedStatus;

public class BrakeAppliedStatusNamesTest {
@Ignore
   @Test
   public void testBAS() throws JsonProcessingException, IOException {
      JsonNode testInput = 1;
      J2735BrakeAppliedStatus expectedValue = 1;

      
      
      assertEquals(expectedValue, BrakesAppliedStatusBuilder.genericBrakeAppliedStatus(testInput));
   }

}
