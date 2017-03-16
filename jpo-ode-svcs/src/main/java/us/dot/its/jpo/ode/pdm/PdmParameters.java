package us.dot.its.jpo.ode.pdm;

import java.util.List;

public class PdmParameters {
    
    public int rsuPDMSampleStart; // 15628.4.1.200.1   INTEGER(0..255)
    public int rsuPDMSampleEnd;   // 15628.4.1.200.2   INTEGER(0..255)
    public int rsuPDMDirections;  // 15628.4.1.200.3   INTEGER(0..65535):16-Bit String
    public int rsuPDMTermChoice;  // 15628.4.1.200.4   INTEGER: 1(Time),2(Distance)
    public int rsuPDMTermTime;    // 15628.4.1.200.5   INTEGER(1..1800)
    public int rsuPDMTermDistance;// 15628.4.1.200.6   INTEGER(1..30000),
    public int rsuPDMSnapshotChoice;    // 15628.4.1.200.7   INTEGER: 1(Time),2(Distance)
    public int rsuPDMMinSnapshotTime;   // 15628.4.1.200.9   INTEGER (0..61) -- units of seconds
    public int rsuPDMMaxSnapshotTime;   // 15628.4.1.200.10  INTEGER (0..61) -- units of seconds
    public int rsuPDMMinSnapshotDistnace; //15628.4.1.200.11 INTEGER (0..1023) -- Units of 1.00 meters
    public int rsuPDMMaxSnapshotDistnace; //15628.4.1.200.12 INTEGER (0..1023) -- Units of 1.00 meters
    public int rsuPDMSnapshotMinSpeed;  // 15628.4.1.200.13  INTEGER (0..31) -- Units of 1.00 m/s
    public int rsuPDMSnapshotMaxSpeed;  // 15628.4.1.200.14  INTEGER (0..31) -- Units of 1.00 m/s
    public int rsuPDMTxInterval;  //15628.4.1.200.15   INTEGER (0..61) -- units of seconds
    
    public List<PdmVehicleStatusEntry> rsuPDMVSReqListTable; // 15628.4.1.200.16
    
    public PdmParameters() {
        // empty constructor
    }

}
