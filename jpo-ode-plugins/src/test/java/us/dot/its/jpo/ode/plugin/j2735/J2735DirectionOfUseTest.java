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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class J2735DirectionOfUseTest {
   @Test
   public void checkForward() {
      assertNotNull(J2735DirectionOfUse.FORWARD);
   }

   @Test
   public void checkReverse() {
      assertNotNull(J2735DirectionOfUse.REVERSE);
   }

   @Test
   public void checkBoth() {
      assertNotNull(J2735DirectionOfUse.BOTH);
   }
}
