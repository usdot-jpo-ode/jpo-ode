package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import us.dot.its.jpo.ode.plugin.SituationDataWarehouse;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2DataTag;

public class DdsAdvisorySituationDataTest {

   @Test
   public void testSettersAndGetters() {
      DdsAdvisorySituationData testDdsAdvisorySituationData = new DdsAdvisorySituationData();
      testDdsAdvisorySituationData.setDialogID(5);
      testDdsAdvisorySituationData.setSeqID(6);
      testDdsAdvisorySituationData.setRequestID("hello world!");
      testDdsAdvisorySituationData.setRecordID("hi world!");

      assertEquals("00000000", testDdsAdvisorySituationData.getGroupID());
      assertEquals(5, testDdsAdvisorySituationData.getDialogID());
      assertEquals(6, testDdsAdvisorySituationData.getSeqID());
      assertEquals("hello world!", testDdsAdvisorySituationData.getRequestID());
      assertEquals("hi world!", testDdsAdvisorySituationData.getRecordID());

   }

   @Test
   public void testParameterConstructor() throws ParseException {

      String isoStartTime = "2008-09-15T15:53:00+00:00";
      String isoStopTime = "2012-05-15T15:53:00+00:00";

      byte distroType = (byte) (DdsAdvisorySituationData.IP | DdsAdvisorySituationData.RSU);
      DdsAdvisorySituationData testDdsAdvisorySituationData = new DdsAdvisorySituationData(isoStartTime, isoStopTime,
            new Ieee1609Dot2DataTag(), new DdsGeoRegion(), SituationDataWarehouse.SDW.TimeToLive.onemonth, "1234", "ABCDEF", distroType);

      // verify time parsing
      assertEquals(Integer.valueOf(2008), testDdsAdvisorySituationData.getAsdmDetails().getStartTime().getYear());
      assertEquals(Integer.valueOf(2012), testDdsAdvisorySituationData.getAsdmDetails().getStopTime().getYear());
      assertNotNull(testDdsAdvisorySituationData.getServiceRegion());
      assertEquals(4, testDdsAdvisorySituationData.getTimeToLive());
      assertEquals(0x9C, testDdsAdvisorySituationData.getDialogID()); // test
                                                                      // default
                                                                      // dialog
                                                                      // ID
      assertEquals(0x05, testDdsAdvisorySituationData.getSeqID()); // test
                                                                   // default
                                                                   // sequence
                                                                   // ID
   }

   @Test
   public void testFlagValues() throws ParseException {
      // Null time should default to flag values
      // Null TTL should default to thirty minutes
      // Null group ID should default to 00 00 00 00

      byte distroType = (byte) (DdsAdvisorySituationData.IP | DdsAdvisorySituationData.RSU);
      DdsAdvisorySituationData testDdsAdvisorySituationData = new DdsAdvisorySituationData(null, null,
            new Ieee1609Dot2DataTag(), new DdsGeoRegion(), null, null,"ABCDEF", distroType);

      // verify time parsing
      assertEquals(Integer.valueOf(0), testDdsAdvisorySituationData.getAsdmDetails().getStartTime().getYear());
      assertEquals(Integer.valueOf(0), testDdsAdvisorySituationData.getAsdmDetails().getStartTime().getMonth());
      assertEquals(Integer.valueOf(0), testDdsAdvisorySituationData.getAsdmDetails().getStartTime().getDay());
      assertEquals(Integer.valueOf(31), testDdsAdvisorySituationData.getAsdmDetails().getStartTime().getHour());
      assertEquals(Integer.valueOf(60), testDdsAdvisorySituationData.getAsdmDetails().getStartTime().getMinute());

      assertEquals(1, testDdsAdvisorySituationData.getTimeToLive());
      assertEquals("00000000", testDdsAdvisorySituationData.getGroupID());
   }

   // must be done for parameterless constructor due to random
   @Test
   public void testHashCodeAndEquals() throws ParseException {
      String isoStartTime = "2008-09-15T15:53:00+00:00";
      String isoStopTime = "2012-05-15T15:53:00+00:00";
      byte distroType = (byte) (DdsAdvisorySituationData.IP | DdsAdvisorySituationData.RSU);

      DdsAdvisorySituationData ddsasd1 = new DdsAdvisorySituationData();
      DdsAdvisorySituationData ddsasd2 = new DdsAdvisorySituationData();
      DdsAdvisorySituationData ddsasd3 = new DdsAdvisorySituationData(isoStartTime, isoStopTime,
            new Ieee1609Dot2DataTag(), new DdsGeoRegion(), SituationDataWarehouse.SDW.TimeToLive.onemonth, "1234", "ABCDEF", distroType);

      assertEquals("Expected identical hashcodes", ddsasd1.hashCode(), ddsasd2.hashCode());
      assertNotEquals("Expected different hashcodes", ddsasd2.hashCode(), ddsasd3.hashCode());

      assertTrue("Expected objects to be equal", ddsasd1.equals(ddsasd2));
      assertFalse("Expected objects to not be equal", ddsasd2.equals(ddsasd3));

   }

}
