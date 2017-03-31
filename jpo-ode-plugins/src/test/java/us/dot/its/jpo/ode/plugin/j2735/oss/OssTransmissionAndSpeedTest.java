package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.TransmissionAndSpeed;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionState;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;

/**
 * -- Summary -- JUnit test class for OssTransmissionandSpeed
 * 
 * Verifies correct conversion from generic TransmissionandSpeed to
 * compliant-J2735TransmissionandSpeed
 * 
 * Velocity is an independent variable for these tests and is set to 0
 * 
 * Note: The speed/velocity element of this class is tested by
 * OssSpeedandVelocityTest
 * 
 * -- Documentation -- Data Frame: DF_TransmissionAndSpeed Use: The
 * DF_TransmissionAndSpeed data frame expresses the speed of the vehicle and the
 * state of the transmission. The transmission state of 'reverse' can be used as
 * a sign value for the speed element when needed. ASN.1 Representation:
 * TransmissionAndSpeed ::= SEQUENCE { transmisson TransmissionState, speed
 * Velocity }
 * 
 * Data Element: DE_TransmissionState Use: The DE_TransmissionState data element
 * is used to provide the current state of the vehicle transmission. ASN.1
 * Representation: TransmissionState ::= ENUMERATED { neutral (0), -- Neutral
 * park (1), -- Park forwardGears (2), -- Forward gears reverseGears (3), --
 * Reverse gears reserved1 (4), reserved2 (5), reserved3 (6), unavailable (7) --
 * not-equipped or unavailable value, -- Any related speed is relative to the
 * vehicle reference frame used }
 * 
 * (see OssSpeedandVelocity for DE_Velocity documentation)
 */
public class OssTransmissionAndSpeedTest {

   /**
    * Test that the transmission state flag value (7) returns (unavailable)
    */
   @Test
   public void shouldReturnUnavailableTransmissionState() {

      Integer testInput = 7;
      J2735TransmissionState expectedValue = J2735TransmissionState.UNAVAILABLE;

      TransmissionState testTransmissionState = new TransmissionState(testInput);
      Velocity testVelocity = new Velocity(0);

      TransmissionAndSpeed testTransmissionandSpeed = new TransmissionAndSpeed(testTransmissionState, testVelocity);

      J2735TransmissionState actualValue = OssTransmissionAndSpeed.genericTransmissionAndSpeed(testTransmissionandSpeed)
            .getTransmisson();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the minimum transmission state value (0) returns (netural)
    */
   @Test
   public void shouldReturnMinimumTransmissionState() {

      Integer testInput = 0;
      J2735TransmissionState expectedValue = J2735TransmissionState.NEUTRAL;

      TransmissionState testTransmissionState = new TransmissionState(testInput);
      Velocity testVelocity = new Velocity(0);

      TransmissionAndSpeed testTransmissionandSpeed = new TransmissionAndSpeed(testTransmissionState, testVelocity);

      J2735TransmissionState actualValue = OssTransmissionAndSpeed.genericTransmissionAndSpeed(testTransmissionandSpeed)
            .getTransmisson();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that the maximum transmission state value (3) returns (reverseGears)
    */
   @Test
   public void shouldReturnMaximumTransmissionState() {

      Integer testInput = 3;
      J2735TransmissionState expectedValue = J2735TransmissionState.REVERSEGEARS;

      TransmissionState testTransmissionState = new TransmissionState(testInput);
      Velocity testVelocity = new Velocity(0);

      TransmissionAndSpeed testTransmissionandSpeed = new TransmissionAndSpeed(testTransmissionState, testVelocity);

      J2735TransmissionState actualValue = OssTransmissionAndSpeed.genericTransmissionAndSpeed(testTransmissionandSpeed)
            .getTransmisson();

      assertEquals(expectedValue, actualValue);

   }

   /**
    * Test that a transmission state value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionTransmissionStateBelowLowerBound() {

      Integer testInput = -1;

      TransmissionState testTransmissionState = new TransmissionState(testInput);
      Velocity testVelocity = new Velocity(0);

      TransmissionAndSpeed testTransmissionandSpeed = new TransmissionAndSpeed(testTransmissionState, testVelocity);

      try {
         OssTransmissionAndSpeed.genericTransmissionAndSpeed(testTransmissionandSpeed).getTransmisson();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a transmission state value (8) above the upper bound (7) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionTransmissionStateAboveUpperBound() {

      Integer testInput = 8;

      TransmissionState testTransmissionState = new TransmissionState(testInput);
      Velocity testVelocity = new Velocity(0);

      TransmissionAndSpeed testTransmissionandSpeed = new TransmissionAndSpeed(testTransmissionState, testVelocity);

      try {
         OssTransmissionAndSpeed.genericTransmissionAndSpeed(testTransmissionandSpeed).getTransmisson();
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }
   
   @Test
   public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
     Constructor<OssTransmissionAndSpeed> constructor = OssTransmissionAndSpeed.class.getDeclaredConstructor();
     assertTrue(Modifier.isPrivate(constructor.getModifiers()));
     constructor.setAccessible(true);
     try {
       constructor.newInstance();
       fail("Expected IllegalAccessException.class");
     } catch (Exception e) {
       assertEquals(InvocationTargetException.class, e.getClass());
     }
   }

}
