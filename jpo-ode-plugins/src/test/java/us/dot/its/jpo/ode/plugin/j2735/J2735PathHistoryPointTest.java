package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735PathHistoryPointTest {
   @Tested
   J2735PathHistoryPoint php;

   @Test
   public void testGettersAndSetters() {
      BigDecimal elevationOffset = BigDecimal.valueOf(1);
      php.setElevationOffset(elevationOffset);
      assertEquals(elevationOffset, php.getElevationOffset());
      BigDecimal heading = BigDecimal.valueOf(1);
      php.setHeading(heading);
      assertEquals(heading, php.getHeading());
      BigDecimal latOffset = BigDecimal.valueOf(1);
      php.setLatOffset(latOffset);
      assertEquals(latOffset, php.getLatOffset());
      BigDecimal lonOffset = BigDecimal.valueOf(1);
      php.setLonOffset(lonOffset);
      assertEquals(lonOffset, php.getLonOffset());
      J2735PositionalAccuracy posAccuracy = new J2735PositionalAccuracy();
      php.setPosAccuracy(posAccuracy);
      assertEquals(posAccuracy, php.getPosAccuracy());
      BigDecimal speed = BigDecimal.valueOf(1);
      php.setSpeed(speed);
      assertEquals(speed, php.getSpeed());
      BigDecimal timeOffset = BigDecimal.valueOf(1);
      php.setTimeOffset(timeOffset);
      assertEquals(timeOffset, php.getTimeOffset());
   }
}
