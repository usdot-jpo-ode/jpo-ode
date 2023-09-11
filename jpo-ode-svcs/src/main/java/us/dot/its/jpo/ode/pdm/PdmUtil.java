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
package us.dot.its.jpo.ode.pdm;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.smi.VariableBinding;

import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;
import us.dot.its.jpo.ode.snmp.SnmpFourDot1Protocol;

public class PdmUtil {

    private PdmUtil() {
       throw new UnsupportedOperationException();
    }

    /**
     * Creates a ScopedPDU from a J2735ProbeDataManagment object. Uses the SnmpFourDot1Protocol class to get the OIDs since
     * PDM operations are not defined in NTCIP1218.
     */
    public static ScopedPDU createPDU(J2735ProbeDataManagment params) {
        ScopedPDU pdu = new ScopedPDU();
        pdu.setType(PDU.SET);

        // Add singular items to PDU
        VariableBinding rsuPDMSampleStart = SnmpFourDot1Protocol.getVbRsuPdmSampleStart(params.getSampleStart());
        VariableBinding rsuPDMSampleEnd = SnmpFourDot1Protocol.getVbRsuPdmSampleEnd(params.getSampleEnd());
        VariableBinding rsuPDMDirections = SnmpFourDot1Protocol.getVbRsuPdmDirections(params.getDirections());
        VariableBinding rsuPDMTermChoice = SnmpFourDot1Protocol.getVbRsuPdmTermChoice(params.getTermChoice());
        VariableBinding rsuPDMTermTime = SnmpFourDot1Protocol.getVbRsuPdmTermTime(params.getTermTime());
        VariableBinding rsuPDMTermDistance = SnmpFourDot1Protocol.getVbRsuPdmTermDistance(params.getTermDistance());
        VariableBinding rsuPDMSnapshotChoice = SnmpFourDot1Protocol.getVbRsuPdmSnapshotChoice(params.getSnapshotChoice());
        VariableBinding rsuPDMMinSnapshotTime = SnmpFourDot1Protocol.getVbRsuPdmMinSnapshotTime(params.getMinSnapshotTime());
        VariableBinding rsuPDMMaxSnapshotTime = SnmpFourDot1Protocol.getVbRsuPdmMaxSnapshotTime(params.getMaxSnapshotTime());
        VariableBinding rsuPDMMinSnapshotDistance = SnmpFourDot1Protocol.getVbRsuPdmMinSnapshotDistance(params.getMinSnapshotDistance());
        VariableBinding rsuPDMMaxSnapshotDistance = SnmpFourDot1Protocol.getVbRsuPdmMaxSnapshotDistance(params.getMaxSnapshotDistance());
        VariableBinding rsuPDMSnapshotMinSpeed = SnmpFourDot1Protocol.getVbRsuPdmSnapshotMinSpeed(params.getSnapshotMinSpeed());
        VariableBinding rsuPDMSnapshotMaxSpeed = SnmpFourDot1Protocol.getVbRsuPdmSnapshotMaxSpeed(params.getSnapshotMaxSpeed());
        VariableBinding rsuPDMTxInterval = SnmpFourDot1Protocol.getVbRsuPdmTxInterval(params.getTxInterval());

        pdu.add(rsuPDMSampleStart);
        pdu.add(rsuPDMSampleEnd);
        pdu.add(rsuPDMDirections);
        pdu.add(rsuPDMTermChoice);
        pdu.add(rsuPDMTermTime);
        pdu.add(rsuPDMTermDistance);
        pdu.add(rsuPDMSnapshotChoice);
        pdu.add(rsuPDMMinSnapshotTime);
        pdu.add(rsuPDMMaxSnapshotTime);
        pdu.add(rsuPDMMinSnapshotDistance);
        pdu.add(rsuPDMMaxSnapshotDistance);
        pdu.add(rsuPDMSnapshotMinSpeed);
        pdu.add(rsuPDMSnapshotMaxSpeed);
        pdu.add(rsuPDMTxInterval);

        // Add request status list items to PDU

        for (int index = 0; index < params.getVehicleStatusRequestList().length; index++) {

            VariableBinding rsuPDMVSReqTag = SnmpFourDot1Protocol.getVbRsuPdmVsReqTag(index, params.getVehicleStatusRequestList()[index].getTag());
            VariableBinding rsuPDMVSReqSubTag = SnmpFourDot1Protocol.getVbRsuPdmVsReqSubTag(index, params.getVehicleStatusRequestList()[index].getSubTag());
            VariableBinding rsuPDMVSReqLessThenValue = SnmpFourDot1Protocol.getVbRsuPdmVsReqLessThen(index, params.getVehicleStatusRequestList()[index].getLessThenValue());
            VariableBinding rsuPDMVSReqMoreThenValue = SnmpFourDot1Protocol.getVbRsuPdmVsReqMoreThen(index, params.getVehicleStatusRequestList()[index].getMoreThenValue());
            VariableBinding rsuPDMVSReqSendAll = SnmpFourDot1Protocol.getVbRsuPdmVsReqSendAll(index, params.getVehicleStatusRequestList()[index].getSendAll());
            VariableBinding rsuPDMVSReqStatus = SnmpFourDot1Protocol.getVbRsuPdmVsReqStatus(index, params.getVehicleStatusRequestList()[index].getStatus());

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
