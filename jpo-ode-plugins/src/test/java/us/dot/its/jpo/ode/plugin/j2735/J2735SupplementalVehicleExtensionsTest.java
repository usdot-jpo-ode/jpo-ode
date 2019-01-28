/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
