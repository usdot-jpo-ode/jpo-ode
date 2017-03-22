package us.dot.its.jpo.ode.snmp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import us.dot.its.jpo.ode.plugin.j2735.pdm.PDM;

public class PdmManagerService {

    private static final Logger logger = LoggerFactory.getLogger(PdmManagerService.class);

    private PdmManagerService() {
    }

    public static ResponseEvent createAndSend(PDM params, SnmpProperties props) {

        if (null == params || null == props) {
            logger.error("PDM SERVICE - Received null object");
            return null;
        }
        // Initialize the SNMP session
        SnmpSession session = null;
        try {
            session = new SnmpSession(props);
        } catch (IOException e) {
            logger.error("PDM SERVICE - Failed to create SNMP session: {}", e);
            return null;
        }

        // Send the PDU
        ResponseEvent response = null;
        ScopedPDU pdu = createPDU(params);
        try {
            response = session.set(pdu, session.getSnmp(), session.getTransport(), session.getTarget());
        } catch (IOException | NullPointerException e) {
            logger.error("PDM SERVICE - Error while sending PDU: {}", e);
            return null;
        }
        return response;

    }

    public static ScopedPDU createPDU(PDM params) {

        // Filter null request
        if (params == null) {
            return null;
        }

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
