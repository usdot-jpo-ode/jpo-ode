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

import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagement.PdmParameters;
import us.dot.its.jpo.ode.plugin.pdm.PdmVehicleStatusEntry;

public class PdmManagerService {

    private static final Logger logger = LoggerFactory.getLogger(PdmManagerService.class);

    private PdmManagerService() {
    }

    public static ResponseEvent createAndSend(PdmParameters params, SnmpProperties props) {

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

    public static ScopedPDU createPDU(PdmParameters params) {

        if (params == null) {
            return null;
        }

        VariableBinding rsuPDMSampleStart = new VariableBinding(new OID("1.0.15628.4.1.200.1"),
                new Integer32(params.rsuPDMSampleStart));

        VariableBinding rsuPDMSampleEnd = new VariableBinding(new OID("1.0.15628.4.1.200.2"),
                new Integer32(params.rsuPDMSampleEnd));

        VariableBinding rsuPDMDirections = new VariableBinding(new OID("1.0.15628.4.1.200.3"),
                new Integer32(params.rsuPDMDirections));

        VariableBinding rsuPDMTermChoice = new VariableBinding(new OID("1.0.15628.4.1.200.4"),
                new Integer32(params.rsuPDMTermChoice));

        VariableBinding rsuPDMTermTime = new VariableBinding(new OID("1.0.15628.4.1.200.5"),
                new Integer32(params.rsuPDMTermTime));

        VariableBinding rsuPDMTermDistance = new VariableBinding(new OID("1.0.15628.4.1.200.6"),
                new Integer32(params.rsuPDMTermDistance));

        VariableBinding rsuPDMSnapshotChoice = new VariableBinding(new OID("1.0.15628.4.1.200.7"),
                new Integer32(params.rsuPDMSnapshotChoice));

        VariableBinding rsuPDMMinSnapshotTime = new VariableBinding(new OID("1.0.15628.4.1.200.9"),
                new Integer32(params.rsuPDMMinSnapshotTime));

        VariableBinding rsuPDMMaxSnapshotTime = new VariableBinding(new OID("1.0.15628.4.1.200.10"),
                new Integer32(params.rsuPDMMaxSnapshotTime));

        VariableBinding rsuPDMMinSnapshotDistnace = new VariableBinding(new OID("1.0.15628.4.1.200.11"),
                new Integer32(params.rsuPDMMinSnapshotDistnace));

        VariableBinding rsuPDMMaxSnapshotDistnace = new VariableBinding(new OID("1.0.15628.4.1.200.12"),
                new Integer32(params.rsuPDMMaxSnapshotDistnace));

        VariableBinding rsuPDMSnapshotMinSpeed = new VariableBinding(new OID("1.0.15628.4.1.200.13"),
                new Integer32(params.rsuPDMSnapshotMinSpeed));

        VariableBinding rsuPDMSnapshotMaxSpeed = new VariableBinding(new OID("1.0.15628.4.1.200.14"),
                new Integer32(params.rsuPDMSnapshotMaxSpeed));

        VariableBinding rsuPDMTxInterval = new VariableBinding(new OID("1.0.15628.4.1.200.15"),
                new Integer32(params.rsuPDMTxInterval));

        for (PdmVehicleStatusEntry entry : params.rsuPDMVSReqListTable) {
            // TODO
            // create variable binding
            // add it to pdu
        }

        ScopedPDU pdu = new ScopedPDU();
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

        pdu.setType(PDU.SET);

        return pdu;
    }

}
