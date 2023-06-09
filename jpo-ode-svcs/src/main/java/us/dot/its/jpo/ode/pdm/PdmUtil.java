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
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import us.dot.its.jpo.ode.plugin.j2735.J2735ProbeDataManagment;
import us.dot.its.jpo.ode.snmp.SnmpFourDot1Protocol;

public class PdmUtil {

    private PdmUtil() {
       throw new UnsupportedOperationException();
    }

    public static ScopedPDU createPDU(J2735ProbeDataManagment params) {
        // TODO: switch on SnmpProtocol enum value to decide OID specifics

        ScopedPDU pdu = new ScopedPDU();
        pdu.setType(PDU.SET);

        // Add singular items to PDU
        VariableBinding rsuPDMSampleStart = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_sample_start_value),
            new Integer32(params.getSampleStart())
        );

        VariableBinding rsuPDMSampleEnd = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_sample_end_value),
            new Integer32(params.getSampleEnd())
        );

        VariableBinding rsuPDMDirections = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_directions_value),
            new Integer32(params.getDirections())
        );

        VariableBinding rsuPDMTermChoice = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_term_choice_value),
            new Integer32(params.getTermChoice())
        );

        VariableBinding rsuPDMTermTime = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_term_time_value),
            new Integer32(params.getTermTime())
        );

        VariableBinding rsuPDMTermDistance = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_term_distance_value),
            new Integer32(params.getTermDistance())
        );

        VariableBinding rsuPDMSnapshotChoice = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_snapshot_choice_value),
            new Integer32(params.getSnapshotChoice())
        );

        VariableBinding rsuPDMMinSnapshotTime = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_min_snapshot_time_value),
            new Integer32(params.getMinSnapshotTime())
        );

        VariableBinding rsuPDMMaxSnapshotTime = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_max_snapshot_time_value),
            new Integer32(params.getMaxSnapshotTime())
        );

        VariableBinding rsuPDMMinSnapshotDistance = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_min_snapshot_distance_value),
            new Integer32(params.getMinSnapshotDistance())
        );

        VariableBinding rsuPDMMaxSnapshotDistance = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_max_snapshot_distance_value),
            new Integer32(params.getMaxSnapshotDistance())
        );

        VariableBinding rsuPDMSnapshotMinSpeed = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_snapshot_min_speed_value),
            new Integer32(params.getSnapshotMinSpeed())
        );

        VariableBinding rsuPDMSnapshotMaxSpeed = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_snapshot_max_speed_value),
            new Integer32(params.getSnapshotMaxSpeed())
        );

        VariableBinding rsuPDMTxInterval = new VariableBinding(
            new OID(SnmpFourDot1Protocol.rsu_pdm_tx_interval_value),
            new Integer32(params.getTxInterval())
        );

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

            VariableBinding rsuPDMVSReqTag = new VariableBinding(
                new OID(SnmpFourDot1Protocol.rsu_pdm_vs_req_tag_value.replace("{}", String.valueOf(index))),
                new Integer32(params.getVehicleStatusRequestList()[index].getTag())
            );
            VariableBinding rsuPDMVSReqSubTag = new VariableBinding(
                new OID(SnmpFourDot1Protocol.rsu_pdm_vs_req_sub_tag_value.replace("{}", String.valueOf(index))),
                new Integer32(params.getVehicleStatusRequestList()[index].getSubTag())
            );
            VariableBinding rsuPDMVSReqLessThenValue = new VariableBinding(
                new OID(SnmpFourDot1Protocol.rsu_pdm_vs_req_less_then_value.replace("{}", String.valueOf(index))),
                new Integer32(params.getVehicleStatusRequestList()[index].getLessThenValue())
            );
            VariableBinding rsuPDMVSReqMoreThenValue = new VariableBinding(
                new OID(SnmpFourDot1Protocol.rsu_pdm_vs_req_more_then_value.replace("{}", String.valueOf(index))),
                new Integer32(params.getVehicleStatusRequestList()[index].getMoreThenValue())
            );
            VariableBinding rsuPDMVSReqSendAll = new VariableBinding(
                new OID(SnmpFourDot1Protocol.rsu_pdm_vs_req_send_all_value.replace("{}", String.valueOf(index))),
                new Integer32(params.getVehicleStatusRequestList()[index].getSendAll())
            );
            VariableBinding rsuPDMVSReqStatus = new VariableBinding(
                new OID(SnmpFourDot1Protocol.rsu_pdm_vs_req_status_value.replace("{}", String.valueOf(index))),
                new Integer32(params.getVehicleStatusRequestList()[index].getStatus())
            );

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
