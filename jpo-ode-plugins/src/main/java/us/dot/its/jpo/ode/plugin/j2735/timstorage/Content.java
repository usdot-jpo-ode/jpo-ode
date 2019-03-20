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

public class Content extends Asn1Object {
   private static final long serialVersionUID = 1L;
   private ITIS_CodesAndText advisory;
   private ITIS_CodesAndText workZone;
   private ITIS_CodesAndText genericSign;
   private ITIS_CodesAndText speedLimit;
   private ITIS_CodesAndText exitService;
   
  public ITIS_CodesAndText getAdvisory() {
    return advisory;
  }
  public void setAdvisory(ITIS_CodesAndText advisory) {
    this.advisory = advisory;
  }
  public ITIS_CodesAndText getWorkZone() {
    return workZone;
  }
  public void setWorkZone(ITIS_CodesAndText workZone) {
    this.workZone = workZone;
  }
  public ITIS_CodesAndText getGenericSign() {
    return genericSign;
  }
  public void setGenericSign(ITIS_CodesAndText genericSign) {
    this.genericSign = genericSign;
  }
  public ITIS_CodesAndText getSpeedLimit() {
    return speedLimit;
  }
  public void setSpeedLimit(ITIS_CodesAndText speedLimit) {
    this.speedLimit = speedLimit;
  }
  public ITIS_CodesAndText getExitService() {
    return exitService;
  }
  public void setExitService(ITIS_CodesAndText exitService) {
    this.exitService = exitService;
  }

}
