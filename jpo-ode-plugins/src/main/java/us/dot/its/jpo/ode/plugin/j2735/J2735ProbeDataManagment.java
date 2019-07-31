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

import us.dot.its.jpo.ode.model.OdeObject;

public class J2735ProbeDataManagment extends OdeObject {

    private static final long serialVersionUID = 6638057737585466892L;
    
    private int sampleStart;
    private int sampleEnd;
    private int directions;
    private int termChoice;
    private int termTime;
    private int termDistance;
    private int snapshotChoice;
    private int minSnapshotTime;
    private int maxSnapshotTime;
    private int minSnapshotDistance;
    private int maxSnapshotDistance;
    private int snapshotMinSpeed;
    private int snapshotMaxSpeed;
    private int txInterval;

    private J2735VehicleStatusRequest[] vehicleStatusRequestList;

    public int getSampleStart() {
        return sampleStart;
    }

    public void setSampleStart(int sampleStart) {
        this.sampleStart = sampleStart;
    }

    public int getSampleEnd() {
        return sampleEnd;
    }

    public void setSampleEnd(int sampleEnd) {
        this.sampleEnd = sampleEnd;
    }

    public int getDirections() {
        return directions;
    }

    public void setDirections(int directions) {
        this.directions = directions;
    }

    public int getTermChoice() {
        return termChoice;
    }

    public void setTermChoice(int termChoice) {
        this.termChoice = termChoice;
    }

    public int getTermTime() {
        return termTime;
    }

    public void setTermTime(int termTime) {
        this.termTime = termTime;
    }

    public int getTermDistance() {
        return termDistance;
    }

    public void setTermDistance(int termDistance) {
        this.termDistance = termDistance;
    }

    public int getSnapshotChoice() {
        return snapshotChoice;
    }

    public void setSnapshotChoice(int snapshotChoice) {
        this.snapshotChoice = snapshotChoice;
    }

    public int getMinSnapshotTime() {
        return minSnapshotTime;
    }

    public void setMinSnapshotTime(int minSnapshotTime) {
        this.minSnapshotTime = minSnapshotTime;
    }

    public int getMaxSnapshotTime() {
        return maxSnapshotTime;
    }

    public void setMaxSnapshotTime(int maxSnapshotTime) {
        this.maxSnapshotTime = maxSnapshotTime;
    }

    public int getMinSnapshotDistance() {
        return minSnapshotDistance;
    }

    public void setMinSnapshotDistance(int minSnapshotDistance) {
        this.minSnapshotDistance = minSnapshotDistance;
    }

    public int getMaxSnapshotDistance() {
        return maxSnapshotDistance;
    }

    public void setMaxSnapshotDistance(int maxSnapshotDistance) {
        this.maxSnapshotDistance = maxSnapshotDistance;
    }

    public int getSnapshotMinSpeed() {
        return snapshotMinSpeed;
    }

    public void setSnapshotMinSpeed(int snapshotMinSpeed) {
        this.snapshotMinSpeed = snapshotMinSpeed;
    }

    public int getSnapshotMaxSpeed() {
        return snapshotMaxSpeed;
    }

    public void setSnapshotMaxSpeed(int snapshotMaxSpeed) {
        this.snapshotMaxSpeed = snapshotMaxSpeed;
    }

    public int getTxInterval() {
        return txInterval;
    }

    public void setTxInterval(int txInterval) {
        this.txInterval = txInterval;
    }

    public J2735VehicleStatusRequest[] getVehicleStatusRequestList() {
        return vehicleStatusRequestList;
    }

    public void setVehicleStatusRequestList(J2735VehicleStatusRequest[] vehicleStatusRequestList) {
        this.vehicleStatusRequestList = vehicleStatusRequestList;
    }

}
