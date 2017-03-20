package us.dot.its.jpo.ode.plugin.j2735.pdm;

public class PdmParameters {

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

    private VehicleStatusRequest[] vehicleStatusRequestList;

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

    public VehicleStatusRequest[] getVehicleStatusRequestList() {
        return vehicleStatusRequestList;
    }

    public void setVehicleStatusRequestList(VehicleStatusRequest[] vehicleStatusRequestList) {
        this.vehicleStatusRequestList = vehicleStatusRequestList;
    }

}
