package us.dot.its.jpo.ode.pdm;

public class PdmVehicleStatusEntry {
    public int rsuPDMVSReqTag;   // 15628.4.1.200.16.1.1    INTEGER (0..28)
    public int rsuPDMVSReqSubTag; // 15628.4.1.200.16.1.2   INTEGER (1..15)
    public int rsuPDMVSReqLessThenValue; // 15628.4.1.200.16.1.3  INTEGER (-32767..32767)
    public int rsuPDMVSReqMoreThenValue; // 15628.4.1.200.16.1.4  INTEGER (-32767..32767)
    public int rsuPDMVSReqSendAll; // 15628.4.1.200.16.1.5  INTEGER (0|1)
    public int rsuPDMVSReqStatus; // 15628.4.1.200.16.1.6   INTEGER (0|1)

    public PdmVehicleStatusEntry() {
        // empty constructor
    }
}