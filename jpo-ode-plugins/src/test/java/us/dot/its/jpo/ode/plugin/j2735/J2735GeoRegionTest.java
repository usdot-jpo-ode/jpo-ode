package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion.GeoRegionException;

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

   private J2735GeoRegion region1;
   private J2735GeoRegion region2;
   private J2735GeoRegion region3;
   private J2735GeoRegion region4;

   @Before
   public void setup() throws GeoRegionException {
      region1 = new J2735GeoRegion();
      region2 = new J2735GeoRegion("44.44,-55.55 22.22,   -33.33");
      region3 = new J2735GeoRegion(new J2735Position3D(), new J2735Position3D());
      region4 = new J2735GeoRegion(
            new J2735Position3D(
                  BigDecimal.valueOf(44.44), BigDecimal.valueOf(-55.55), null), 
            new J2735Position3D (
                  BigDecimal.valueOf(22.22), BigDecimal.valueOf(-33.33), null));
   }
   
   /**
    * Run the conversion constructor test
    * @throws GeoRegionException 
    */
   
   @Test
   public void testConstructorString() throws GeoRegionException {
      assertAll();
   }

   private void assertAll() {
      assertNull(region1.getNwCorner());
      assertNull(region1.getSeCorner());
      
      assertEquals(BigDecimal.valueOf(44.44), region2.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), region2.getNwCorner().getLongitude());
      assertEquals(BigDecimal.valueOf(22.22), region2.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-33.33), region2.getSeCorner().getLongitude());

      assertNotNull(region3.getNwCorner());
      assertNull(region3.getNwCorner().getLatitude());
      assertNull(region3.getNwCorner().getLongitude());
      assertNull(region3.getNwCorner().getElevation());
      
      assertNotNull(region3.getSeCorner());
      assertNull(region3.getSeCorner().getLatitude());
      assertNull(region3.getSeCorner().getLongitude());
      assertNull(region3.getSeCorner().getElevation());

      assertEquals(BigDecimal.valueOf(44.44), region4.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), region4.getNwCorner().getLongitude());
      assertEquals(BigDecimal.valueOf(22.22), region4.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-33.33), region4.getSeCorner().getLongitude());

   }
   
   /**
    * Run the J2735Position3D getNwCorner() method test
    */
   @Test
   public void testGetNwCorner() {
      assertNull(region1.getNwCorner());
      
      assertEquals(BigDecimal.valueOf(44.44), region2.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), region2.getNwCorner().getLongitude());

      assertNotNull(region3.getNwCorner());
      assertNull(region3.getNwCorner().getLatitude());
      assertNull(region3.getNwCorner().getLongitude());
      assertNull(region3.getNwCorner().getElevation());
      
      assertEquals(BigDecimal.valueOf(44.44), region4.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), region4.getNwCorner().getLongitude());
   }

   /**
    * Run the OdeGeoRegion setNwCorner(J2735Position3D) method test
    */
   @Test
   public void testSetNwCorner() {
      region1.setNwCorner(new J2735Position3D(
            BigDecimal.valueOf(44.44), BigDecimal.valueOf(-55.55), null));
      
      assertEquals(BigDecimal.valueOf(44.44), region2.getNwCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-55.55), region2.getNwCorner().getLongitude());
   }

   /**
    * Run the J2735Position3D getSeCorner() method test
    */
   @Test
   public void testGetSeCorner() {
      assertNull(region1.getSeCorner());
      
      assertEquals(BigDecimal.valueOf(22.22), region2.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-33.33), region2.getSeCorner().getLongitude());

      assertNotNull(region3.getSeCorner());
      assertNull(region3.getSeCorner().getLatitude());
      assertNull(region3.getSeCorner().getLongitude());
      assertNull(region3.getSeCorner().getElevation());

      assertEquals(BigDecimal.valueOf(22.22), region4.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-33.33), region4.getSeCorner().getLongitude());
   }

   /**
    * Run the OdeGeoRegion setSeCorner(J2735Position3D) method test
    */
   @Test
   public void testSetSeCorner() {
      region1.setSeCorner(new J2735Position3D (
            BigDecimal.valueOf(22.22), BigDecimal.valueOf(-33.33), null));
      assertEquals(BigDecimal.valueOf(22.22), region2.getSeCorner().getLatitude());
      assertEquals(BigDecimal.valueOf(-33.33), region2.getSeCorner().getLongitude());
   }

   /**
    * Run the J2735Position3D getCenterPosition() method test
    * @throws GeoRegionException 
    */
   @Test
   public void testGetCenterPosition() throws GeoRegionException {
      J2735Position3D result = region2.getCenterPosition();
      assertEquals(BigDecimal.valueOf(33.33), result.getLatitude());
      assertEquals(BigDecimal.valueOf(-44.44), result.getLongitude());
   }

   @Test
   public void testContainsPosition3DNull() throws GeoRegionException {
      assertFalse(region1.contains((J2735Position3D)null));
   }

   @Test
   public void testContainsPosition3DFailsNwLatitude() throws GeoRegionException {
      //region2=(44.44, -55.55), (22.22, -33.33)
      //Test pos.getLatitude().doubleValue() > nw.getLatitude().doubleValue()
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(66.66), BigDecimal.valueOf(-44.44), null)));
      //Test nw.getLatitude() == null
      region2.getNwCorner().setLatitude(null);
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(33.33), BigDecimal.valueOf(-44.44), null)));
      //Test nw == null
      region2.setNwCorner(null);
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(33.33), BigDecimal.valueOf(-44.44), null)));
   }

   @Test
   public void testContainsPosition3DFailsNwLongitude() throws GeoRegionException {
      //region2=(44.44, -55.55), (22.22, -33.33)
      //Test pos.getLongitude().doubleValue() < nw.getLongitude().doubleValue()
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(33.33), BigDecimal.valueOf(-22.22), null)));
      //Test nw.getLatitude() == null
      region2.getNwCorner().setLongitude(null);
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(33.33), BigDecimal.valueOf(-44.44), null)));
   }

   @Test
   public void testContainsPosition3DFailsSeLatitude() throws GeoRegionException {
      //region2=(44.44, -22.22), (55.55, -33.33)
      //Test pos.getLatitude().doubleValue() < se.getLatitude().doubleValue()
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(11.11), BigDecimal.valueOf(-44.44), null)));
      //Test nw.getLatitude() == null
      region2.getSeCorner().setLatitude(null);
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(33.33), BigDecimal.valueOf(-44.44), null)));
      //Test nw == null
      region2.setSeCorner(null);
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(33.33), BigDecimal.valueOf(-44.44), null)));
   }

   @Test
   public void testContainsPosition3DFailsSeLongitude() throws GeoRegionException {
      //region2=(44.44, -55.55), (22.22, -33.33)
      //Test pos.getLongitude().doubleValue() > se.getLongitude().doubleValue()
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(33.33), BigDecimal.valueOf(-22.22), null)));
      //Test se.getLatitude() == null
      region2.getSeCorner().setLongitude(null);
      assertFalse(region2.contains(new J2735Position3D(BigDecimal.valueOf(33.33), BigDecimal.valueOf(-44.44), null)));
   }

   @Test
   public void testContainsPosition3DPasses() throws GeoRegionException {
      //region2=(44.44, -55.55), (22.22, -33.33)
      //Test pos.getLatitude().doubleValue() > nw.getLatitude().doubleValue()
      assertTrue(region2.contains(new J2735Position3D(BigDecimal.valueOf(33.33), BigDecimal.valueOf(-44.44), null)));
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