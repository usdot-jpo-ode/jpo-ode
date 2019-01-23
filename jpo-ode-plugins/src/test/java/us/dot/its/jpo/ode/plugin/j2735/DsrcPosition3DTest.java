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

import static org.junit.Assert.*;

import org.junit.Test;

public class DsrcPosition3DTest {

   @Test
   public void testSettersAndGetters() {
      DsrcPosition3D dpd = new DsrcPosition3D();
      dpd.setElevation(235L);
      dpd.setLatitude(788989898L);
      dpd.setLongitude(687187341L);
      
      assertEquals(Long.valueOf(235), dpd.getElevation());
      assertEquals(Long.valueOf(788989898), dpd.getLatitude());
      assertEquals(Long.valueOf(687187341), dpd.getLongitude());
   }
   
   @Test
   public void testHashcodeAndEquals() {
      DsrcPosition3D dpd1 = new DsrcPosition3D(235L, 788989898L, 687187341L);
      DsrcPosition3D dpd2 = new DsrcPosition3D(235L, 788989898L, 687187341L);
      DsrcPosition3D dpd3 = new DsrcPosition3D(265L, 788989898L, 687187341L);
      
      assertEquals("Expected identical hashcodes", dpd1.hashCode(), dpd2.hashCode());
      assertNotEquals("Expected different hashcodes", dpd2.hashCode(), dpd3.hashCode());
      
      assertTrue("Expected objects to be equal", dpd1.equals(dpd2));
      assertFalse("Expected objects to be not equal", dpd2.equals(dpd3));
   }
   
   @Test
   public void testHashcodeAndEqualsNulls() {
      DsrcPosition3D dpd1 = new DsrcPosition3D();
      DsrcPosition3D dpd2 = new DsrcPosition3D();
      DsrcPosition3D dpd3 = new DsrcPosition3D(265L, 788989898L, 687187341L);
      
      assertEquals("Expected identical hashcodes", dpd1.hashCode(), dpd2.hashCode());
      assertNotEquals("Expected different hashcodes", dpd2.hashCode(), dpd3.hashCode());
      
      assertTrue("Expected objects to be equal", dpd1.equals(dpd2));
      assertFalse("Expected objects to be not equal", dpd2.equals(dpd3));
   }
}
