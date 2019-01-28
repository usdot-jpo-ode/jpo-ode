package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMPackage;
import us.dot.its.jpo.ode.util.JsonUtils;

public class RTCMPackageBuilderTest {

  @Test
  public void testGenericRTCMPackage() {
    
    List<String> msgList = new ArrayList<>();
    msgList.add("message 1");
    msgList.add("message 2");
    msgList.add("message 3");
    
    ObjectNode rtcmPackage = JsonUtils.newNode();
    ArrayNode msgs = JsonUtils.newArrayNode();
    
    for (String msg : msgList) {
      msgs.add(msg);
    }
    
    rtcmPackage.set(RTCMPackageBuilder.MSGS, msgs);
    rtcmPackage.set(RTCMPackageBuilder.RTCM_HEADER, RTCMheaderBuilderTest.buildTestRtcmHeader());
    
    J2735RTCMPackage actualRTCMpackage = RTCMPackageBuilder.genericRTCMPackage(rtcmPackage);
    
    J2735RTCMPackage expectedRTCMHeader = new J2735RTCMPackage()
        .setMsgs(msgList)
        .setRtcmHeader(RTCMheaderBuilderTest.buildExpecteRtcMheader());
    
    assertEquals(expectedRTCMHeader, actualRTCMpackage);
  }

}
