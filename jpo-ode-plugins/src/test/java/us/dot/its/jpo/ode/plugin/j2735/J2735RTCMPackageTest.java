package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735RTCMPackageTest {
   @Tested
   J2735RTCMPackage pac;

   @Test
   public void testGettersAndSetters() {
      List<String> msgs = new ArrayList<>();
      pac.setMsgs(msgs);
      assertEquals(msgs,pac.getMsgs());
      J2735RTCMheader rtcmHeader = new J2735RTCMheader();
      pac.setRtcmHeader(rtcmHeader);
      assertEquals(rtcmHeader,pac.getRtcmHeader());
   }
}
