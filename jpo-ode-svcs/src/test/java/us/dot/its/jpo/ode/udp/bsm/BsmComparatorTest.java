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
package us.dot.its.jpo.ode.udp.bsm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.udp.bsm.BsmComparator;

public class BsmComparatorTest {

   @Test
   public void shouldReturnXLessThanY() {
      J2735BsmCoreData testDataX = new J2735BsmCoreData();
      testDataX.setSecMark(5);
      J2735BsmCoreData testDataY = new J2735BsmCoreData();
      testDataY.setSecMark(6);

      J2735Bsm bsmX = new J2735Bsm();
      bsmX.setCoreData(testDataX);

      J2735Bsm bsmY = new J2735Bsm();
      bsmY.setCoreData(testDataY);

      BsmComparator testBsmComparator = new BsmComparator();
      assertEquals(-1, testBsmComparator.compare(bsmX, bsmY));
   }
   
   @Test
   public void shouldReturnYLessThanX() {
      J2735BsmCoreData testDataX = new J2735BsmCoreData();
      testDataX.setSecMark(6);
      J2735BsmCoreData testDataY = new J2735BsmCoreData();
      testDataY.setSecMark(5);

      J2735Bsm bsmX = new J2735Bsm();
      bsmX.setCoreData(testDataX);

      J2735Bsm bsmY = new J2735Bsm();
      bsmY.setCoreData(testDataY);

      BsmComparator testBsmComparator = new BsmComparator();
      assertEquals(1, testBsmComparator.compare(bsmX, bsmY));
   }
   
   @Test
   public void shouldReturnXEqualsY() {
      J2735BsmCoreData testDataX = new J2735BsmCoreData();
      testDataX.setSecMark(5);
      J2735BsmCoreData testDataY = new J2735BsmCoreData();
      testDataY.setSecMark(5);

      J2735Bsm bsmX = new J2735Bsm();
      bsmX.setCoreData(testDataX);

      J2735Bsm bsmY = new J2735Bsm();
      bsmY.setCoreData(testDataY);

      BsmComparator testBsmComparator = new BsmComparator();
      assertEquals(0, testBsmComparator.compare(bsmX, bsmY));
   }

}
