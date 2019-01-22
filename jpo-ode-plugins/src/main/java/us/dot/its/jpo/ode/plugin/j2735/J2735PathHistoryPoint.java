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

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PathHistoryPoint extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private BigDecimal elevationOffset;
    private BigDecimal heading;
    private BigDecimal latOffset;
    private BigDecimal lonOffset;
    private J2735PositionalAccuracy posAccuracy;
    private BigDecimal speed;
    private BigDecimal timeOffset;

    public BigDecimal getElevationOffset() {
        return elevationOffset;
    }

    public void setElevationOffset(BigDecimal elevationOffset) {
        this.elevationOffset = elevationOffset;
    }

    public BigDecimal getHeading() {
        return heading;
    }

    public void setHeading(BigDecimal heading) {
        this.heading = heading;
    }

    public BigDecimal getLatOffset() {
        return latOffset;
    }

    public void setLatOffset(BigDecimal latOffset) {
        this.latOffset = latOffset;
    }

    public BigDecimal getLonOffset() {
        return lonOffset;
    }

    public void setLonOffset(BigDecimal lonOffset) {
        this.lonOffset = lonOffset;
    }

    public J2735PositionalAccuracy getPosAccuracy() {
        return posAccuracy;
    }

    public void setPosAccuracy(J2735PositionalAccuracy posAccuracy) {
        this.posAccuracy = posAccuracy;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public BigDecimal getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(BigDecimal timeOffset) {
        this.timeOffset = timeOffset;
    }

}
