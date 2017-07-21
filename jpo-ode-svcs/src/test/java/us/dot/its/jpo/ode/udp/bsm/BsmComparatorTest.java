package us.dot.its.jpo.ode.udp.bsm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.udp.bsm.BsmComparator;

public class BsmComparatorTest {

   @Test
   public void shouldReturnXLessThanY() {
      J2735BsmCoreData testDataX = new J2735BsmCoreData();
      testDataX.setSecMark(5);
      J2735BsmCoreData testDataY = new J2735BsmCoreData();
      testDataY.setSecMark(6);

      J2735Bsm bsmX = new J2735Bsm();
      bsmX.setCoreData(testDataX);

      J2735Bsm bsmY = new J2735Bsm();
      bsmY.setCoreData(testDataY);

      BsmComparator testBsmComparator = new BsmComparator();
      assertEquals(-1, testBsmComparator.compare(bsmX, bsmY));
   }
   
   @Test
   public void shouldReturnYLessThanX() {
      J2735BsmCoreData testDataX = new J2735BsmCoreData();
      testDataX.setSecMark(6);
      J2735BsmCoreData testDataY = new J2735BsmCoreData();
      testDataY.setSecMark(5);

      J2735Bsm bsmX = new J2735Bsm();
      bsmX.setCoreData(testDataX);

      J2735Bsm bsmY = new J2735Bsm();
      bsmY.setCoreData(testDataY);

      BsmComparator testBsmComparator = new BsmComparator();
      assertEquals(1, testBsmComparator.compare(bsmX, bsmY));
   }
   
   @Test
   public void shouldReturnXEqualsY() {
      J2735BsmCoreData testDataX = new J2735BsmCoreData();
      testDataX.setSecMark(5);
      J2735BsmCoreData testDataY = new J2735BsmCoreData();
      testDataY.setSecMark(5);

      J2735Bsm bsmX = new J2735Bsm();
      bsmX.setCoreData(testDataX);

      J2735Bsm bsmY = new J2735Bsm();
      bsmY.setCoreData(testDataY);

      BsmComparator testBsmComparator = new BsmComparator();
      assertEquals(0, testBsmComparator.compare(bsmX, bsmY));
   }

}
