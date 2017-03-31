package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;

import mockit.Tested;

@RunWith(JMockit.class)
public class J2735TransmissionAndSpeedTest {
   @Tested
   J2735TransmissionAndSpeed tas;

   @Test
   public void testGettersAndSetters() {
      BigDecimal speed = BigDecimal.valueOf(1);
      tas.setSpeed(speed);
      assertEquals(speed,tas.getSpeed());
      J2735TransmissionState transmisson = null;
      tas.setTransmisson(transmisson);
      assertEquals(transmisson,tas.getTransmisson());
   }
}
