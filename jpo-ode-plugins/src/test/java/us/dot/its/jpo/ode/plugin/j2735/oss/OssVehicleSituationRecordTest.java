package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class OssVehicleSituationRecordTest {

   @Test
   public void convertPosition3DshouldReturnCornerCaseMinimumLongitude() {

      BigDecimal testInput = BigDecimal.valueOf(-179.9999998);
      int expectedValue = -1799999998;
      
      J2735Position3D testPos = new J2735Position3D();
      testPos.setLongitude(testInput);
      testPos.setLatitude(null);
      testPos.setElevation(null);
      
      Position3D actualValue = OssVehicleSituationRecord.convertPosition3D(testPos);
      
      assertEquals(expectedValue, actualValue.get_long().intValue());
   }
   
   @Test
   public void convertPosition3DshouldReturnCornerCaseMinimumLatitude() {

      BigDecimal testInput = BigDecimal.valueOf(-89.9999998);
      int expectedValue = -899999998;
      
      J2735Position3D testPos = new J2735Position3D();
      testPos.setLongitude(null);
      testPos.setLatitude(testInput);
      testPos.setElevation(null);
      
      Position3D actualValue = OssVehicleSituationRecord.convertPosition3D(testPos);
      
      assertEquals(expectedValue, actualValue.getLat().intValue());
   }
   
   @Test
   public void convertPosition3DshouldReturnCornerCaseMinimumElevation() {

      BigDecimal testInput = BigDecimal.valueOf(-409.4);
      int expectedValue = -4094;
      
      J2735Position3D testPos = new J2735Position3D();
      testPos.setLongitude(null);
      testPos.setLatitude(null);
      testPos.setElevation(testInput);
      
      Position3D actualValue = OssVehicleSituationRecord.convertPosition3D(testPos);
      
      assertEquals(expectedValue, actualValue.getElevation().intValue());
   }
   
   

}
