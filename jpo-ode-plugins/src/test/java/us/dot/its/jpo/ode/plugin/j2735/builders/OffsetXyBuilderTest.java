package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class OffsetXyBuilderTest {

  @Test
  public void testOffsetXy() {
    assertEquals(1500, OffsetXyBuilder.offsetXy(BigDecimal.valueOf(15.0)).longValue());
  }

}
