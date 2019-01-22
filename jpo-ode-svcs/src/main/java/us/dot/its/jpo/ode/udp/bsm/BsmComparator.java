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

import java.util.Comparator;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

/**
 * Comparator for the priority queue to keep the chronological order of bsms
 */
public class BsmComparator implements Comparator<J2735Bsm> {

   @Override
   public int compare(J2735Bsm x, J2735Bsm y) {
      // for now we are using the BSM's time offset property
      int xt = x.getCoreData().getSecMark();
      int yt = y.getCoreData().getSecMark();

      return Integer.compare(xt, yt);
   }

}
