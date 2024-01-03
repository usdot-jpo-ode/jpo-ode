package us.dot.its.jpo.ode.snmp;

import java.text.ParseException;

import jakarta.xml.bind.DatatypeConverter;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import us.dot.its.jpo.ode.plugin.SNMP;

public class SnmpFourDot1Protocol {
    private static String fourdot1prefix = "1.0.15628.4.";
    private static String srm_prefix = "1.4.1.";
    private static String pdm_prefix = "1.200.";
    private static String pdm_vs_req_prefix = pdm_prefix + "15.";

    // variable bindings
    public static VariableBinding getVbRsuSrmPsid(int index, String rsuId) {
        return SnmpSession.getPEncodedVariableBinding(
            rsu_srm_psid_oid().concat(".").concat(Integer.toString(index)),
            rsuId
        );
    }

    public static VariableBinding getVbRsuSrmDsrcMsgId(int index, int msgId) {
        return new VariableBinding(
            new OID(rsu_srm_dsrc_msg_id_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(msgId)
        );
    }

    public static VariableBinding getVbRsuSrmTxMode(int index, int mode) {
        return new VariableBinding(
            new OID(rsu_srm_tx_mode_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(mode)
        );
    }

    public static VariableBinding getVbRsuSrmTxChannel(int index, int channel) {
        return new VariableBinding(
            new OID(rsu_srm_tx_channel_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(channel)
        );
    }

    public static VariableBinding getVbRsuSrmTxInterval(int index, int interval) {
        return new VariableBinding(
            new OID(rsu_srm_tx_interval_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(interval)
        );
    }

    public static VariableBinding getVbRsuSrmDeliveryStart(int index, String deliveryStart) throws ParseException {
        return new VariableBinding(
            new OID(rsu_srm_delivery_start_oid().concat(".").concat(Integer.toString(index))),
            new OctetString(DatatypeConverter.parseHexBinary(SNMP.snmpTimestampFromIso(deliveryStart)))
        );
    }

    public static VariableBinding getVbRsuSrmDeliveryStop(int index, String deliveryStop) throws ParseException {
        return new VariableBinding(
            new OID(rsu_srm_delivery_stop_oid().concat(".").concat(Integer.toString(index))),
            new OctetString(DatatypeConverter.parseHexBinary(SNMP.snmpTimestampFromIso(deliveryStop)))
        );
    }

    public static VariableBinding getVbRsuSrmPayload(int index, String payload) {
        return new VariableBinding(
            new OID(rsu_srm_payload_oid().concat(".").concat(Integer.toString(index))),
            new OctetString(DatatypeConverter.parseHexBinary(payload))
        );
    }

    public static VariableBinding getVbRsuSrmEnable(int index, int enable) {
        return new VariableBinding(
            new OID(rsu_srm_enable_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(enable)
        );
    }

    public static VariableBinding getVbRsuSrmStatus(int index, int status) {
        return new VariableBinding(
            new OID(rsu_srm_status_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(status)
        );
    }

    public static VariableBinding getVbRsuSrmStatus(int index) {
        return new VariableBinding(
            new OID(rsu_srm_status_oid().concat(".").concat(Integer.toString(index))
            )
        );
    }

    public static VariableBinding getVbRsuPdmSampleStart(int sampleStart) {
        return new VariableBinding(
            new OID(rsu_pdm_sample_start_oid()),
            new Integer32(sampleStart)
        );
    }

    public static VariableBinding getVbRsuPdmSampleEnd(int sampleEnd) {
        return new VariableBinding(
            new OID(rsu_pdm_sample_end_oid()),
            new Integer32(sampleEnd)
        );
    }

    public static VariableBinding getVbRsuPdmDirections(int directions) {
        return new VariableBinding(
            new OID(rsu_pdm_directions_oid()),
            new Integer32(directions)
        );
    }

    public static VariableBinding getVbRsuPdmTermChoice(int termChoice) {
        return new VariableBinding(
            new OID(rsu_pdm_term_choice_oid()),
            new Integer32(termChoice)
        );
    }

    public static VariableBinding getVbRsuPdmTermTime(int termTime) {
        return new VariableBinding(
            new OID(rsu_pdm_term_time_oid()),
            new Integer32(termTime)
        );
    }

    public static VariableBinding getVbRsuPdmTermDistance(int termDistance) {
        return new VariableBinding(
            new OID(rsu_pdm_term_distance_oid()),
            new Integer32(termDistance)
        );
    }

    public static VariableBinding getVbRsuPdmSnapshotChoice(int snapshotChoice) {
        return new VariableBinding(
            new OID(rsu_pdm_snapshot_choice_oid()),
            new Integer32(snapshotChoice)
        );
    }

    public static VariableBinding getVbRsuPdmMinSnapshotTime(int minSnapshotTime) {
        return new VariableBinding(
            new OID(rsu_pdm_min_snapshot_time_oid()),
            new Integer32(minSnapshotTime)
        );
    }

    public static VariableBinding getVbRsuPdmMaxSnapshotTime(int maxSnapshotTime) {
        return new VariableBinding(
            new OID(rsu_pdm_max_snapshot_time_oid()),
            new Integer32(maxSnapshotTime)
        );
    }

    public static VariableBinding getVbRsuPdmMinSnapshotDistance(int minSnapshotDistance) {
        return new VariableBinding(
            new OID(rsu_pdm_min_snapshot_distance_oid()),
            new Integer32(minSnapshotDistance)
        );
    }

    public static VariableBinding getVbRsuPdmMaxSnapshotDistance(int maxSnapshotDistance) {
        return new VariableBinding(
            new OID(rsu_pdm_max_snapshot_distance_oid()),
            new Integer32(maxSnapshotDistance)
        );
    }

    public static VariableBinding getVbRsuPdmSnapshotMinSpeed(int snapshotMinSpeed) {
        return new VariableBinding(
            new OID(rsu_pdm_snapshot_min_speed_oid()),
            new Integer32(snapshotMinSpeed)
        );
    }

    public static VariableBinding getVbRsuPdmSnapshotMaxSpeed(int snapshotMaxSpeed) {
        return new VariableBinding(
            new OID(rsu_pdm_snapshot_max_speed_oid()),
            new Integer32(snapshotMaxSpeed)
        );
    }

    public static VariableBinding getVbRsuPdmTxInterval(int txInterval) {
        return new VariableBinding(
            new OID(rsu_pdm_tx_interval_oid()),
            new Integer32(txInterval)
        );
    }

    public static VariableBinding getVbRsuPdmVsReqTag(int index, int tag) {
        return new VariableBinding(
            new OID(rsu_pdm_vs_req_tag_oid().replace("{}", String.valueOf(index))),
            new Integer32(tag)
        );
    }

    public static VariableBinding getVbRsuPdmVsReqSubTag(int index, int subTag) {
        return new VariableBinding(
            new OID(rsu_pdm_vs_req_sub_tag_oid().replace("{}", String.valueOf(index))),
            new Integer32(subTag)
        );
    }

    public static VariableBinding getVbRsuPdmVsReqLessThen(int index, int lessThen) {
        return new VariableBinding(
            new OID(rsu_pdm_vs_req_less_then_oid().replace("{}", String.valueOf(index))),
            new Integer32(lessThen)
        );
    }

    public static VariableBinding getVbRsuPdmVsReqMoreThen(int index, int moreThen) {
        return new VariableBinding(
            new OID(rsu_pdm_vs_req_more_then_oid().replace("{}", String.valueOf(index))),
            new Integer32(moreThen)
        );
    }

    public static VariableBinding getVbRsuPdmVsReqSendAll(int index, int sendAll) {
        return new VariableBinding(
            new OID(rsu_pdm_vs_req_send_all_oid().replace("{}", String.valueOf(index))),
            new Integer32(sendAll)
        );
    }

    public static VariableBinding getVbRsuPdmVsReqStatus(int index, int status) {
        return new VariableBinding(
            new OID(rsu_pdm_vs_req_status_oid().replace("{}", String.valueOf(index))),
            new Integer32(status)
        );
    }

    // oids
    private static String rsu_srm_psid_oid() {
        return fourdot1prefix + srm_prefix + "2";
    }

    private static String rsu_srm_dsrc_msg_id_oid() {
        return fourdot1prefix + srm_prefix + "3";
    }

    private static String rsu_srm_tx_mode_oid() {
        return fourdot1prefix + srm_prefix + "4";
    }

    private static String rsu_srm_tx_channel_oid() {
        return fourdot1prefix + srm_prefix + "5";
    }

    private static String rsu_srm_tx_interval_oid() {
        return fourdot1prefix + srm_prefix + "6";
    }

    private static String rsu_srm_delivery_start_oid() {
        return fourdot1prefix + srm_prefix + "7";
    }

    private static String rsu_srm_delivery_stop_oid() {
        return fourdot1prefix + srm_prefix + "8";
    }

    private static String rsu_srm_payload_oid() {
        return fourdot1prefix + srm_prefix + "9";
    }

    private static String rsu_srm_enable_oid() {
        return fourdot1prefix + srm_prefix + "10";
    }

    private static String rsu_srm_status_oid() {
        return fourdot1prefix + srm_prefix + "11";
    }
    
    private static String rsu_pdm_sample_start_oid() {
        return fourdot1prefix + pdm_prefix + "1";
    }

    private static String rsu_pdm_sample_end_oid() {
        return fourdot1prefix + pdm_prefix + "2";
    }

    private static String rsu_pdm_directions_oid() {
        return fourdot1prefix + pdm_prefix + "3";
    }

    private static String rsu_pdm_term_choice_oid() {
        return fourdot1prefix + pdm_prefix + "4";
    }

    private static String rsu_pdm_term_time_oid() {
        return fourdot1prefix + pdm_prefix + "5";
    }

    private static String rsu_pdm_term_distance_oid() {
        return fourdot1prefix + pdm_prefix + "6";
    }

    private static String rsu_pdm_snapshot_choice_oid() {
        return fourdot1prefix + pdm_prefix + "7";
    }

    private static String rsu_pdm_min_snapshot_time_oid() {
        return fourdot1prefix + pdm_prefix + "9";
    }

    private static String rsu_pdm_max_snapshot_time_oid() {
        return fourdot1prefix + pdm_prefix + "10";
    }

    private static String rsu_pdm_min_snapshot_distance_oid() {
        return fourdot1prefix + pdm_prefix + "11";
    }

    private static String rsu_pdm_max_snapshot_distance_oid() {
        return fourdot1prefix + pdm_prefix + "12";
    }

    private static String rsu_pdm_snapshot_min_speed_oid() {
        return fourdot1prefix + pdm_prefix + "13";
    }

    private static String rsu_pdm_snapshot_max_speed_oid() {
        return fourdot1prefix + pdm_prefix + "14";
    }

    private static String rsu_pdm_tx_interval_oid() {
        return fourdot1prefix + pdm_prefix + "15";
    }

    private static String rsu_pdm_vs_req_tag_oid() {
        return fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "1";
    }

    private static String rsu_pdm_vs_req_sub_tag_oid() {
        return fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "2";
    }

    private static String rsu_pdm_vs_req_less_then_oid() {
        return fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "3";
    }

    private static String rsu_pdm_vs_req_more_then_oid() {
        return fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "4";
    }

    private static String rsu_pdm_vs_req_send_all_oid() {
        return fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "5";
    }

    private static String rsu_pdm_vs_req_status_oid() {
        return fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "6";
    }
}
