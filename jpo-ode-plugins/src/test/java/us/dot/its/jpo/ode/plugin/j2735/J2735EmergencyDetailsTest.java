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

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class J2735EmergencyDetailsTest {

   @Tested
   J2735EmergencyDetails ed;

   @Test
   public void testGettersAndSetters() {
      Integer sspRights = 1;
      ed.setSspRights(sspRights);
      assertEquals(sspRights,ed.getSspRights());
      J2735PrivilegedEvents events = new J2735PrivilegedEvents();
      ed.setEvents(events);
      assertEquals(events, ed.getEvents());
      J2735LightbarInUse lightsUse = null;
      ed.setLightsUse(lightsUse);
      assertEquals(lightsUse,ed.getLightsUse());
      J2735MultiVehicleResponse multi = null;
      ed.setMulti(multi);
      assertEquals(multi,ed.getMulti());
      J2735ResponseType responseType = null;
      ed.setResponseType(responseType);
      assertEquals(responseType,ed.getResponseType());
      J2735SirenInUse sirenUse = null;
      ed.setSirenUse(sirenUse);
      assertEquals(sirenUse,ed.getSirenUse());
   }
}
