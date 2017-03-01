package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

/**
 * The class <code>OdeGeoRegionTest</code> contains tests for the class {@link
 * <code>OdeGeoRegion</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 10/2/16 10:03 AM
 *
 * @author 572682
 *
 * @version $Revision$
 */
public class J2735GeoRegionTest {

   private J2735GeoRegion fixture1;
   private J2735GeoRegion fixture2;
   private J2735GeoRegion fixture3;
   private J2735GeoRegion fixture4;

   @Before
   public void setup() throws Exception {
      fixture1 = new J2735GeoRegion();
      fixture2 = new J2735GeoRegion("44.44,-55.55 66.66,   -77.77");
      fixture3 = new J2735GeoRegion(new J2735Position3D(), new J2735Position3D());
      fixture4 = new J2735GeoRegion(
            new J2735Position3D(
                  BigDecimal.valueOf(44.44), BigDecimal.valueOf(-55.55), null), 
            new J2735Position3D (
                  BigDecimal.valueOf(66.66), BigDecimal.valueOf(-77.77), null));
   }
   
   /**
    * Run the conversion constructor test
    * @throws Exception 
    */
   
   @Test
   public void testConstructorString() throws Exception {
      assertAll();
   }

   private void assertAll() {
      assertNull(fixture1.getNwCorner());
      assertNull(fixture1.getSeCorner());
      
      assertEquals(BigDecimal.valueOf(44.44), fixture2.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), fixture2.getNwCorner().getLongitude());
      assertEquals(BigDecimal.valueOf(66.66), fixture2.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-77.77), fixture2.getSeCorner().getLongitude());

      assertNotNull(fixture3.getNwCorner());
      assertNull(fixture3.getNwCorner().getLatitude());
      assertNull(fixture3.getNwCorner().getLongitude());
      assertNull(fixture3.getNwCorner().getElevation());
      
      assertNotNull(fixture3.getSeCorner());
      assertNull(fixture3.getSeCorner().getLatitude());
      assertNull(fixture3.getSeCorner().getLongitude());
      assertNull(fixture3.getSeCorner().getElevation());

      assertEquals(BigDecimal.valueOf(44.44), fixture4.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), fixture4.getNwCorner().getLongitude());
      assertEquals(BigDecimal.valueOf(66.66), fixture4.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-77.77), fixture4.getSeCorner().getLongitude());

   }
   
   /**
    * Run the J2735Position3D getNwCorner() method test
    */
   @Test
   public void testGetNwCorner() {
      assertNull(fixture1.getNwCorner());
      
      assertEquals(BigDecimal.valueOf(44.44), fixture2.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), fixture2.getNwCorner().getLongitude());

      assertNotNull(fixture3.getNwCorner());
      assertNull(fixture3.getNwCorner().getLatitude());
      assertNull(fixture3.getNwCorner().getLongitude());
      assertNull(fixture3.getNwCorner().getElevation());
      
      assertEquals(BigDecimal.valueOf(44.44), fixture4.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), fixture4.getNwCorner().getLongitude());
   }

   /**
    * Run the OdeGeoRegion setNwCorner(J2735Position3D) method test
    */
   @Test
   public void testSetNwCorner() {
      fixture1.setNwCorner(new J2735Position3D(
            BigDecimal.valueOf(44.44), BigDecimal.valueOf(-55.55), null));
      
      assertEquals(BigDecimal.valueOf(44.44), fixture2.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), fixture2.getNwCorner().getLongitude());
   }

   /**
    * Run the J2735Position3D getSeCorner() method test
    */
   @Test
   public void testGetSeCorner() {
      assertNull(fixture1.getSeCorner());
      
      assertEquals(BigDecimal.valueOf(66.66), fixture2.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-77.77), fixture2.getSeCorner().getLongitude());

      assertNotNull(fixture3.getSeCorner());
      assertNull(fixture3.getSeCorner().getLatitude());
      assertNull(fixture3.getSeCorner().getLongitude());
      assertNull(fixture3.getSeCorner().getElevation());

      assertEquals(BigDecimal.valueOf(66.66), fixture4.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-77.77), fixture4.getSeCorner().getLongitude());
   }

   /**
    * Run the OdeGeoRegion setSeCorner(J2735Position3D) method test
    */
   @Test
   public void testSetSeCorner() {
      fixture1.setSeCorner(new J2735Position3D (
            BigDecimal.valueOf(66.66), BigDecimal.valueOf(-77.77), null));
      assertEquals(BigDecimal.valueOf(66.66), fixture2.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-77.77), fixture2.getSeCorner().getLongitude());
   }

   /**
    * Run the J2735Position3D getCenterPosition() method test
    * @throws OdeException 
    */
   @Test
   public void testGetCenterPosition() throws Exception {
      J2735GeoRegion fixture = new J2735GeoRegion("42.537903,-83.477903 42.305753,-82.842753");
      J2735Position3D result = fixture.getCenterPosition();
      assertEquals(BigDecimal.valueOf(42.421828000000005), result.getLatitude());
      assertEquals(BigDecimal.valueOf(-83.16032799999999), result.getLongitude());
   }

}

/*$CPS$ This comment was generated by CodePro. Do not edit it.
 * patternId = com.instantiations.assist.eclipse.pattern.testCasePattern
 * strategyId = com.instantiations.assist.eclipse.pattern.testCasePattern.junitTestCase
 * additionalTestNames = 
 * assertTrue = true
 * callTestMethod = true
 * createMain = false
 * createSetUp = false
 * createTearDown = false
 * createTestFixture = false
 * createTestStubs = true
 * methods = getCenterPosition()
 * package = com.bah.ode.model
 * package.sourceFolder = ode-core/src/test/java
 * superclassType = junit.framework.TestCase
 * testCase = OdeGeoRegionTest
 * testClassType = com.bah.ode.asn.OdeGeoRegion
 */