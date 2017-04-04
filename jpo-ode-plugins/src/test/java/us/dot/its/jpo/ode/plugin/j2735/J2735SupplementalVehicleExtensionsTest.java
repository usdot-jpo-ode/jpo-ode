package us.dot.its.jpo.ode.plugin.j2735;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735SupplementalVehicleExtensionsTest {
   @Tested
   J2735SupplementalVehicleExtensions sve;
   
   @Test
   public void testGettersAndSetters() {
      J2735SpeedProfile speedProfile = new J2735SpeedProfile();
      sve.setSpeedProfile(speedProfile);
      assertEquals(speedProfile,sve.getSpeedProfile());
      J2735RTCMPackage theRTCM = new J2735RTCMPackage();
      sve.setTheRTCM(theRTCM);
      assertEquals(theRTCM,sve.getTheRTCM());
      List<J2735RegionalContent> regional = new ArrayList<>();
      sve.setRegional(regional);
      assertEquals(regional,sve.getRegional());
   }
}
