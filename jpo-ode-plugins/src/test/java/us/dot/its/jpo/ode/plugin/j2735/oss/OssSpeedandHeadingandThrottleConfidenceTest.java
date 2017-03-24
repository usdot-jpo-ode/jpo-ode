package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.HeadingConfidence;
import us.dot.its.jpo.ode.j2735.dsrc.SpeedConfidence;
import us.dot.its.jpo.ode.j2735.dsrc.SpeedandHeadingandThrottleConfidence;
import us.dot.its.jpo.ode.j2735.dsrc.ThrottleConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735HeadingConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedandHeadingandThrottleConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735ThrottleConfidence;

/**
 * -- Summary -- JUnit test class for OssSpeedandHeadingandThrottleConfidence
 * 
 * Verifies correct conversion from generic SpeedandHeadingandThrottleConfidence
 * to compliant-J2735SpeedandHeadingandThrottleConfidence
 * 
 * Independent variables for the tests are set to value 0
 * 
 * -- Documentation -- Data Frame: DF_SpeedHeadingThrottleConfidence Use: The
 * DF_SpeedHeadingThrottleConfidence data frame is a single data frame combining
 * multiple related bit fields into one concept. ASN.1 Representation:
 * SpeedandHeadingandThrottleConfidence ::= SEQUENCE { heading
 * HeadingConfidence, speed SpeedConfidence, throttle ThrottleConfidence }
 * 
 * Data Element: DE_HeadingConfidence Use: The DE_HeadingConfidence data element
 * is used to provide the 95% confidence level for the currently reported value
 * of DE_Heading, taking into account the current calibration and precision of
 * the sensor(s) used to measure and/or calculate the value. This data element
 * is only to provide the listener with information on the limitations of the
 * sensing system, not to support any type of automatic error correction or to
 * imply a guaranteed maximum error. This data element should not be used for
 * fault detection or diagnosis, but if a vehicle is able to detect a fault, the
 * confidence interval should be increased accordingly. The frame of reference
 * and axis of rotation used shall be in accordance with that defined Section
 * 11. ASN.1 Representation: HeadingConfidence ::= ENUMERATED { unavailable (0),
 * -- B'000 Not Equipped or unavailable prec10deg (1), -- B'010 10 degrees
 * prec05deg (2), -- B'011 5 degrees prec01deg (3), -- B'100 1 degrees
 * prec0-1deg (4), -- B'101 0.1 degrees prec0-05deg (5), -- B'110 0.05 degrees
 * prec0-01deg (6), -- B'110 0.01 degrees prec0-0125deg (7) -- B'111 0.0125
 * degrees, aligned with heading LSB } -- Encoded as a 3 bit value
 * 
 * Data Element: DE_SpeedConfidence Use: The DE_SpeedConfidence data element is
 * used to provide the 95% confidence level for the currently reported value of
 * DE_Speed, taking into account the current calibration and precision of the
 * sensor(s) used to measure and/or calculate the value. This data element is
 * only to provide the listener with information on the limitations of the
 * sensing system, not to support any type of automatic error correction or to
 * imply a guaranteed maximum error. This data element should not be used for
 * fault detection or diagnosis, but if a vehicle is able to detect a fault, the
 * confidence interval should be increased accordingly. The frame of reference
 * and axis of rotation used shall be in accordance with that defined Section
 * 11. ASN.1 Representation: SpeedConfidence ::= ENUMERATED { unavailable (0),
 * -- Not Equipped or unavailable prec100ms (1), -- 100 meters / sec prec10ms
 * (2), -- 10 meters / sec prec5ms (3), -- 5 meters / sec prec1ms (4), -- 1
 * meters / sec prec0-1ms (5), -- 0.1 meters / sec prec0-05ms (6), -- 0.05
 * meters / sec prec0-01ms (7) -- 0.01 meters / sec }
 *
 * Data Element: DE_ThrottleConfidence Use: The DE_ThrottleConfidence data
 * element is used to provide the 95% confidence level for the currently
 * reported value of DE_Throttle, taking into account the current calibration
 * and precision of the sensor(s) used to measure and/or calculate the value.
 * This data element is only to provide information on the limitations of the
 * sensing system, not to support any type of automatic error correction or to
 * imply a guaranteed maximum error. This data element should not be used for
 * fault detection or diagnosis, but if a vehicle is able to detect a fault, the
 * confidence interval should be increased accordingly. If a fault that triggers
 * the MIL is of a nature to render throttle performance unreliable, then
 * ThrottleConfidence should be represented as "notEquipped." ASN.1
 * Representation: ThrottleConfidence ::= ENUMERATED { unavailable (0), -- B'00
 * Not Equipped or unavailable prec10percent (1), -- B'01 10 percent Confidence
 * level prec1percent (2), -- B'10 1 percent Confidence level prec0-5percent (3)
 * -- B'11 0.5 percent Confidence level }
 */
public class OssSpeedandHeadingandThrottleConfidenceTest {

   // HeadingConfidence tests

   /**
    * Test that the undefined heading confidence flag (0) returns (unavailable)
    */
   @Test
   public void shouldReturnUnavailableHeadingConfidence() {

      Integer testInput = 0;
      J2735HeadingConfidence expectedValue = J2735HeadingConfidence.UNAVAILABLE;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(testInput);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      J2735SpeedandHeadingandThrottleConfidence actualshtc = OssSpeedandHeadingandThrottleConfidence
            .genericSpeedandHeadingandThrottleConfidence(testshtc);

      assertEquals(expectedValue, actualshtc.getHeading());

   }

   /**
    * Test that the minimum heading confidence value (1) returns (prec10deg)
    */
   @Test
   public void shouldReturnMinimumHeadingConfidence() {

      Integer testInput = 1;
      J2735HeadingConfidence expectedValue = J2735HeadingConfidence.PREC10DEG;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(testInput);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      J2735SpeedandHeadingandThrottleConfidence actualshtc = OssSpeedandHeadingandThrottleConfidence
            .genericSpeedandHeadingandThrottleConfidence(testshtc);

      assertEquals(expectedValue, actualshtc.getHeading());

   }

   /**
    * Test that the maximum heading confidence value (7) returns (prec0-0125deg)
    */
   @Test
   public void shouldReturnMaximumHeadingConfidence() {

      Integer testInput = 7;
      J2735HeadingConfidence expectedValue = J2735HeadingConfidence.PREC0_0125DEG;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(testInput);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtch = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      J2735SpeedandHeadingandThrottleConfidence actualshtc = OssSpeedandHeadingandThrottleConfidence
            .genericSpeedandHeadingandThrottleConfidence(testshtch);

      assertEquals(expectedValue, actualshtc.getHeading());

   }

   /**
    * Test that a heading confidence value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionHeadingConfidenceBelowLowerBound() {

      Integer testInput = -1;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(testInput);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtch = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      try {
         OssSpeedandHeadingandThrottleConfidence.genericSpeedandHeadingandThrottleConfidence(testshtch);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a heading confidence value (8) above the upper bound (7) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionHeadingConfidenceAboveUpperBound() {

      Integer testInput = 8;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(testInput);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      try {
         OssSpeedandHeadingandThrottleConfidence.genericSpeedandHeadingandThrottleConfidence(testshtc);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   // SpeedConfidence tests

   /**
    * Test that the undefined speed confidence flag value (0) returns
    * (unavailable)
    */
   @Test
   public void shouldReturnUnavailableSpeedConfidence() {

      Integer testInput = 0;
      J2735SpeedConfidence expectedValue = J2735SpeedConfidence.UNAVAILABLE;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(testInput);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      J2735SpeedandHeadingandThrottleConfidence actualshtc = OssSpeedandHeadingandThrottleConfidence
            .genericSpeedandHeadingandThrottleConfidence(testshtc);

      assertEquals(expectedValue, actualshtc.getSpeed());

   }

   /**
    * Test that the minimum speed confidence value (1) returns (prec100ms)
    */
   @Test
   public void shouldReturnMinimumSpeedConfidence() {

      Integer testInput = 1;
      J2735SpeedConfidence expectedValue = J2735SpeedConfidence.PREC100MS;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(testInput);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      J2735SpeedandHeadingandThrottleConfidence actualshtc = OssSpeedandHeadingandThrottleConfidence
            .genericSpeedandHeadingandThrottleConfidence(testshtc);

      assertEquals(expectedValue, actualshtc.getSpeed());

   }

   /**
    * Test that the maximum speed confidence value (7) returns (prec0-01ms)
    */
   @Test
   public void shouldReturnMaximumSpeedConfidence() {

      Integer testInput = 7;
      J2735SpeedConfidence expectedValue = J2735SpeedConfidence.PREC0_01MS;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(testInput);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      J2735SpeedandHeadingandThrottleConfidence actualshtc = OssSpeedandHeadingandThrottleConfidence
            .genericSpeedandHeadingandThrottleConfidence(testshtc);

      assertEquals(expectedValue, actualshtc.getSpeed());

   }

   /**
    * Test that a speed confidence value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionSpeedConfidenceBelowLowerBound() {

      Integer testInput = -1;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(testInput);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      try {
         OssSpeedandHeadingandThrottleConfidence.genericSpeedandHeadingandThrottleConfidence(testshtc);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a speed confidence value (8) above the upper bound (7) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionSpeedConfidenceAboveUpperBound() {

      Integer testInput = 8;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(testInput);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(0);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      try {
         OssSpeedandHeadingandThrottleConfidence.genericSpeedandHeadingandThrottleConfidence(testshtc);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   // ThrottleConfidence tests

   /**
    * Test that the throttle confidence flag value (0) returns (unavailable)
    */
   @Test
   public void shouldReturnUnavailableThrottleConfidence() {

      Integer testInput = 0;
      J2735ThrottleConfidence expectedValue = J2735ThrottleConfidence.UNAVAILABLE;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(testInput);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      J2735SpeedandHeadingandThrottleConfidence actualshtc = OssSpeedandHeadingandThrottleConfidence
            .genericSpeedandHeadingandThrottleConfidence(testshtc);

      assertEquals(expectedValue, actualshtc.getThrottle());

   }

   /**
    * Test that the minimum throttle confidence value (1) returns
    * (prec10percent)
    */
   @Test
   public void shouldReturnMinimumThrottleConfidence() {

      Integer testInput = 1;
      J2735ThrottleConfidence expectedValue = J2735ThrottleConfidence.PREC10PERCENT;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(testInput);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      J2735SpeedandHeadingandThrottleConfidence actualshtc = OssSpeedandHeadingandThrottleConfidence
            .genericSpeedandHeadingandThrottleConfidence(testshtc);

      assertEquals(expectedValue, actualshtc.getThrottle());

   }

   /**
    * Test that the maximum throttle confidence value (3) returns
    * (prec0-5percent)
    */
   @Test
   public void shouldReturnMaximumThrottleConfidence() {

      Integer testInput = 3;
      J2735ThrottleConfidence expectedValue = J2735ThrottleConfidence.PREC0_5PERCENT;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(testInput);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      J2735SpeedandHeadingandThrottleConfidence actualshtc = OssSpeedandHeadingandThrottleConfidence
            .genericSpeedandHeadingandThrottleConfidence(testshtc);

      assertEquals(expectedValue, actualshtc.getThrottle());

   }

   /**
    * Test that a throttle confidence value (-1) below the lower bound (0)
    * throws IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionThrottleConfidenceBelowLowerBound() {

      Integer testInput = -1;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(testInput);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      try {
         OssSpeedandHeadingandThrottleConfidence.genericSpeedandHeadingandThrottleConfidence(testshtc);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a throttle confidence value (4) above the upper bound (3) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionThrottleConfidenceAboveUpperBound() {

      Integer testInput = 4;

      HeadingConfidence testHeadingConfidence = new HeadingConfidence(0);
      SpeedConfidence testSpeedConfidence = new SpeedConfidence(0);
      ThrottleConfidence testThrottleConfidence = new ThrottleConfidence(testInput);

      SpeedandHeadingandThrottleConfidence testshtc = new SpeedandHeadingandThrottleConfidence(testHeadingConfidence,
            testSpeedConfidence, testThrottleConfidence);

      try {
         OssSpeedandHeadingandThrottleConfidence.genericSpeedandHeadingandThrottleConfidence(testshtc);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }
}
