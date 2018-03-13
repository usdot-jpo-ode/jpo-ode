package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.*;

import org.junit.Test;

import mockit.Tested;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2DataTag;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisoryDetails.AdvisoryBroadcastType;
import us.dot.its.jpo.ode.util.CodecUtils;

public class DdsAdvisoryDetailsTest {

   @Tested
   DdsAdvisoryDetails testDdsAdvisoryDetails;

   @Test
   public void testSettersGetters() {
      testDdsAdvisoryDetails.setAsdmID("testAsdmID");
      testDdsAdvisoryDetails.setAsdmType(1);
      testDdsAdvisoryDetails.setDistType(CodecUtils.toHex(DdsAdvisorySituationData.IP));
      testDdsAdvisoryDetails.setStartTime(new J2735DFullTime());
      testDdsAdvisoryDetails.setStopTime(new J2735DFullTime());
      testDdsAdvisoryDetails.setAdvisoryMessageBytes("testAdvisoryMessageBytes");
      testDdsAdvisoryDetails.setAdvisoryMessage(new Ieee1609Dot2DataTag());
      
      assertEquals("testAsdmID", testDdsAdvisoryDetails.getAsdmID());
      assertEquals(1, testDdsAdvisoryDetails.getAsdmType());
      assertEquals("02", testDdsAdvisoryDetails.getDistType());
      assertNotNull(testDdsAdvisoryDetails.getStartTime());
      assertNotNull(testDdsAdvisoryDetails.getStopTime());
      assertEquals("testAdvisoryMessageBytes", testDdsAdvisoryDetails.getAdvisoryMessageBytes());
      assertNotNull(testDdsAdvisoryDetails.getAdvisoryMessage());
   }

   @Test
   public void testHashCodeAndEquals() {
      String distType = CodecUtils.toHex(DdsAdvisorySituationData.RSU);
      
      DdsAdvisoryDetails ddsad1 = new DdsAdvisoryDetails("asdmID", AdvisoryBroadcastType.tim, distType, new J2735DFullTime(),
            new J2735DFullTime(), new Ieee1609Dot2DataTag());
      DdsAdvisoryDetails ddsad2 = new DdsAdvisoryDetails("asdmID", AdvisoryBroadcastType.tim, distType, new J2735DFullTime(),
            new J2735DFullTime(), new Ieee1609Dot2DataTag());
      DdsAdvisoryDetails ddsad3 = new DdsAdvisoryDetails("asdmID", AdvisoryBroadcastType.map, distType, new J2735DFullTime(),
            new J2735DFullTime(), new Ieee1609Dot2DataTag());

      assertEquals("Expected identical hashcodes", ddsad1.hashCode(), ddsad2.hashCode());
      assertNotEquals("Expected different hashcodes", ddsad2.hashCode(), ddsad3.hashCode());
      
      assertTrue("Expected objects to be equal", ddsad1.equals(ddsad2));
      assertFalse("Expected objects to not be equal", ddsad2.equals(ddsad3));
   }

}
