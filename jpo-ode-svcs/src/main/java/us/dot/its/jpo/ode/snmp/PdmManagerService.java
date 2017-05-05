package us.dot.its.jpo.ode.snmp;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;

public class PdmManagerService {

    private PdmManagerService() {
       throw new UnsupportedOperationException();
    }

    public static ScopedPDU createPDU(J2735ProbeDataManagment params) {

        ScopedPDU pdu = new ScopedPDU();
        pdu.setType(PDU.SET);

        // Add singular items to PDU
        VariableBinding rsuPDMSampleStart = new VariableBinding(new OID("1.0.15628.4.1.200.1"),
                new Integer32(params.getSampleStart()));

        VariableBinding rsuPDMSampleEnd = new VariableBinding(new OID("1.0.15628.4.1.200.2"),
                new Integer32(params.getSampleEnd()));

        VariableBinding rsuPDMDirections = new VariableBinding(new OID("1.0.15628.4.1.200.3"),
                new Integer32(params.getDirections()));

        VariableBinding rsuPDMTermChoice = new VariableBinding(new OID("1.0.15628.4.1.200.4"),
                new Integer32(params.getTermChoice()));

        VariableBinding rsuPDMTermTime = new VariableBinding(new OID("1.0.15628.4.1.200.5"),
                new Integer32(params.getTermTime()));

        VariableBinding rsuPDMTermDistance = new VariableBinding(new OID("1.0.15628.4.1.200.6"),
                new Integer32(params.getTermDistance()));

        VariableBinding rsuPDMSnapshotChoice = new VariableBinding(new OID("1.0.15628.4.1.200.7"),
                new Integer32(params.getSnapshotChoice()));

        VariableBinding rsuPDMMinSnapshotTime = new VariableBinding(new OID("1.0.15628.4.1.200.9"),
                new Integer32(params.getMinSnapshotTime()));

        VariableBinding rsuPDMMaxSnapshotTime = new VariableBinding(new OID("1.0.15628.4.1.200.10"),
                new Integer32(params.getMaxSnapshotTime()));

        VariableBinding rsuPDMMinSnapshotDistnace = new VariableBinding(new OID("1.0.15628.4.1.200.11"),
                new Integer32(params.getMinSnapshotDistance()));

        VariableBinding rsuPDMMaxSnapshotDistnace = new VariableBinding(new OID("1.0.15628.4.1.200.12"),
                new Integer32(params.getMaxSnapshotDistance()));

        VariableBinding rsuPDMSnapshotMinSpeed = new VariableBinding(new OID("1.0.15628.4.1.200.13"),
                new Integer32(params.getSnapshotMinSpeed()));

        VariableBinding rsuPDMSnapshotMaxSpeed = new VariableBinding(new OID("1.0.15628.4.1.200.14"),
                new Integer32(params.getSnapshotMaxSpeed()));

        VariableBinding rsuPDMTxInterval = new VariableBinding(new OID("1.0.15628.4.1.200.15"),
                new Integer32(params.getTxInterval()));

        pdu.add(rsuPDMSampleStart);
        pdu.add(rsuPDMSampleEnd);
        pdu.add(rsuPDMDirections);
        pdu.add(rsuPDMTermChoice);
        pdu.add(rsuPDMTermTime);
        pdu.add(rsuPDMTermDistance);
        pdu.add(rsuPDMSnapshotChoice);
        pdu.add(rsuPDMMinSnapshotTime);
        pdu.add(rsuPDMMaxSnapshotTime);
        pdu.add(rsuPDMMinSnapshotDistnace);
        pdu.add(rsuPDMMaxSnapshotDistnace);
        pdu.add(rsuPDMSnapshotMinSpeed);
        pdu.add(rsuPDMSnapshotMaxSpeed);
        pdu.add(rsuPDMTxInterval);

        // Add request status list items to PDU

        for (int i = 0; i < params.getVehicleStatusRequestList().length; i++) {

            String currentOid = "1.0.15628.4.1.200.15." + i;

            VariableBinding rsuPDMVSReqTag = new VariableBinding(new OID(currentOid + ".1"),
                    new Integer32(params.getVehicleStatusRequestList()[i].getTag()));
            VariableBinding rsuPDMVSReqSubTag = new VariableBinding(new OID(currentOid + ".2"),
                    new Integer32(params.getVehicleStatusRequestList()[i].getSubTag()));
            VariableBinding rsuPDMVSReqLessThenValue = new VariableBinding(new OID(currentOid + ".3"),
                    new Integer32(params.getVehicleStatusRequestList()[i].getLessThenValue()));
            VariableBinding rsuPDMVSReqMoreThenValue = new VariableBinding(new OID(currentOid + ".4"),
                    new Integer32(params.getVehicleStatusRequestList()[i].getMoreThenValue()));
            VariableBinding rsuPDMVSReqSendAll = new VariableBinding(new OID(currentOid + ".5"),
                    new Integer32(params.getVehicleStatusRequestList()[i].getSendAll()));
            VariableBinding rsuPDMVSReqStatus = new VariableBinding(new OID(currentOid + ".6"),
                    new Integer32(params.getVehicleStatusRequestList()[i].getStatus()));

            pdu.add(rsuPDMVSReqTag);
            pdu.add(rsuPDMVSReqSubTag);
            pdu.add(rsuPDMVSReqLessThenValue);
            pdu.add(rsuPDMVSReqMoreThenValue);
            pdu.add(rsuPDMVSReqSendAll);
            pdu.add(rsuPDMVSReqStatus);
        }

        return pdu;
    }

}
