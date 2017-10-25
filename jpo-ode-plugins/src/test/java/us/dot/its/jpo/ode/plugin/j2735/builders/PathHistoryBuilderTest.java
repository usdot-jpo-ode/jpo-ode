package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistory;

public class PathHistoryBuilderTest {

   @Ignore
   @Test
   public void testInZeroPositive() throws JsonProcessingException, IOException {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode testInput = mapper.readTree("1234");
      
      
   //   J2735PathHistory expectedValue = BigDecimal.valueOf(24.68);

    //  assertEquals(expectedValue, PathHistoryBuilder.genericPathHistory(testInput));
   }

}
