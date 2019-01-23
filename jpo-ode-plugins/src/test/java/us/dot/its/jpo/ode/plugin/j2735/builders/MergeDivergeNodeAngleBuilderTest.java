package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class MergeDivergeNodeAngleBuilderTest {

  @Test
  public void testMergeDivergeNodeAngle() {
    assertEquals(10, MergeDivergeNodeAngleBuilder.mergeDivergeNodeAngle(BigDecimal.valueOf(15.0)));
  }

}
