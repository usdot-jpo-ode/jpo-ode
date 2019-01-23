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
package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class DirectionOfUse extends Asn1Object {
  private static final long serialVersionUID = 1L;
  
  public enum DirectionOfUseEnum {
    unavailable,  // (0), -- unknown or NA, not typically used in valid expressions
    forward,      // (1), -- direction of travel follows node ordering
    reverse,      // (2), -- direction of travel is the reverse of node ordering
    both          // (3) -- direction of travel allowed in both directions
  }


  private String unavailable;
  private String forward;     // (1), -- direction of travel follows node ordering
  private String reverse;     // (2), -- direction of travel is the reverse of node ordering
  private String both;        // (3) -- direction of travel allowed in both directions
  
  public String getUnavailable() {
    return unavailable;
  }
  public void setUnavailable(String unavailable) {
    this.unavailable = unavailable;
  }
  public String getForward() {
    return forward;
  }
  public void setForward(String forward) {
    this.forward = forward;
  }
  public String getReverse() {
    return reverse;
  }
  public void setReverse(String reverse) {
    this.reverse = reverse;
  }
  public String getBoth() {
    return both;
  }
  public void setBoth(String both) {
    this.both = both;
  }
}