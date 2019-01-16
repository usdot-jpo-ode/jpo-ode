package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735AntennaOffsetSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMheader;
import us.dot.its.jpo.ode.util.JsonUtils;

public class RTCMheaderBuilderTest {

  @Test
  public void testGenericRTCMheader() {
     ObjectNode rtcmHeader = buildTestRtcmHeader();
     
     J2735RTCMheader actualRTCMheader = RTCMheaderBuilder.genericRTCMheader(rtcmHeader);
     
     J2735RTCMheader expectedRTCMHeader = buildExpecteRtcMheader();
     assertEquals(expectedRTCMHeader, actualRTCMheader);
  }

  public static J2735RTCMheader buildExpecteRtcMheader() {
    J2735AntennaOffsetSet expectedOffset = new J2735AntennaOffsetSet()
         .setAntOffsetX(BigDecimal.valueOf(12.34))
         .setAntOffsetY(BigDecimal.valueOf(2.34))
         .setAntOffsetZ(BigDecimal.valueOf(3.21));
     J2735BitString expectedStatus = new J2735BitString();
     expectedStatus.put("localCorrectionsPresent", true);
     expectedStatus.put("baseStationType", false);
     expectedStatus.put("inViewOfUnder5", false);
     expectedStatus.put("unavailable", true);
     expectedStatus.put("aPDOPofUnder5", true);
     expectedStatus.put("isMonitored", true);
     expectedStatus.put("isHealthy", false);
     expectedStatus.put("networkCorrectionsPresent", false);
     J2735RTCMheader expectedRTCMHeader = new J2735RTCMheader().setOffsetSet(expectedOffset).setStatus(expectedStatus);
    return expectedRTCMHeader;
  }

  public static ObjectNode buildTestRtcmHeader() {
    ObjectNode rtcmHeader = JsonUtils.newNode();
     rtcmHeader.set(RTCMheaderBuilder.OFFSET_SET, AntennaOffsetSetBuilderTest.buildTestJ2735AntennaOffsetSet());
     rtcmHeader.put(RTCMheaderBuilder.STATUS, "10101010");
    return rtcmHeader;
  }

}
