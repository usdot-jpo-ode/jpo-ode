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

import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PathHistory extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735FullPositionVector initialPosition;
    private J2735BitString currGNSSstatus;
    private List<J2735PathHistoryPoint> crumbData;

    public J2735FullPositionVector getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(J2735FullPositionVector initialPosition) {
        this.initialPosition = initialPosition;
    }

    public J2735BitString getCurrGNSSstatus() {
        return currGNSSstatus;
    }

    public void setCurrGNSSstatus(J2735BitString currGNSSstatus) {
        this.currGNSSstatus = currGNSSstatus;
    }

    public List<J2735PathHistoryPoint> getCrumbData() {
        return crumbData;
    }

    public void setCrumbData(List<J2735PathHistoryPoint> crumbData) {
        this.crumbData = crumbData;
    }

}
