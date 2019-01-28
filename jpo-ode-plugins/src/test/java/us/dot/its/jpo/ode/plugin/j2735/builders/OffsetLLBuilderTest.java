package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class OffsetLLBuilderTest {

  @Test
  public void testOffsetLL() {
    assertEquals(150000000, OffsetLLBuilder.offsetLL(BigDecimal.valueOf(15.0)).longValue());
  }

}
