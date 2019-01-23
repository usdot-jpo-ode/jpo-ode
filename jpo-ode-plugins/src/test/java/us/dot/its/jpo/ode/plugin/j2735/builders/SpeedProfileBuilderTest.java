package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedProfile;
import us.dot.its.jpo.ode.util.JsonUtils;

public class SpeedProfileBuilderTest {

  @Test
  public void testGenericSpeedProfile() {
    
    List<Integer> speedReportList = new ArrayList<>();
    speedReportList.add(55);
    speedReportList.add(60);
    speedReportList.add(65);
    
    ObjectNode speedProfile = JsonUtils.newNode();
    ArrayNode speedReports = JsonUtils.newArrayNode();
    
    for (Integer report : speedReportList) {
      speedReports.add(report);
    }
    
    speedProfile.set(SpeedProfileBuilder.SPEED_REPORTS, speedReports);
    
    J2735SpeedProfile actualSpeedProfile = SpeedProfileBuilder.genericSpeedProfile(speedProfile);
    
    J2735SpeedProfile expectedSpeedProfile = new J2735SpeedProfile()
        .setSpeedReports(speedReportList);
    
    assertEquals(expectedSpeedProfile, actualSpeedProfile);
  }

}
