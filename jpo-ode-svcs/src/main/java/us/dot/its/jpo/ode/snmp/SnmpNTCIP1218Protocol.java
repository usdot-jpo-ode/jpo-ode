package us.dot.its.jpo.ode.snmp;

import java.text.ParseException;

import jakarta.xml.bind.DatatypeConverter;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import us.dot.its.jpo.ode.plugin.SNMP;

public class SnmpNTCIP1218Protocol {
    private static String ntcip1218prefix = "1.3.6.1.4.1.1206.4.2.18.";
    private static String srm_prefix = "3.2.1"; // srm (defined in NTCIP1218 Section 5.4.2)

    // variable bindings
    public static VariableBinding getVbRsuMsgRepeatPsid(int index, String rsuId) {
        return SnmpSession.getPEncodedVariableBinding(
            rsu_msg_repeat_psid_oid().concat(".").concat(Integer.toString(index)),
            rsuId
         );
    }

    public static VariableBinding getVbRsuMsgRepeatTxChannel(int index, int channel) {
        return new VariableBinding(
            new OID(rsu_msg_repeat_tx_channel_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(channel)
         );
    }

    public static VariableBinding getVbRsuMsgRepeatTxInterval(int index, int interval) {
        return new VariableBinding(
            new OID(rsu_msg_repeat_tx_interval_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(interval)
         );
    }

    public static VariableBinding getVbRsuMsgRepeatDeliveryStart(int index, String deliveryStart) throws ParseException {
        return new VariableBinding(
            new OID(rsu_msg_repeat_delivery_start_oid().concat(".").concat(Integer.toString(index))),
            new OctetString(DatatypeConverter.parseHexBinary(SNMP.snmpTimestampFromIso(deliveryStart)))
         );
    }

    public static VariableBinding getVbRsuMsgRepeatDeliveryStop(int index, String deliveryStop) throws ParseException {
        return new VariableBinding(
            new OID(rsu_msg_repeat_delivery_stop_oid().concat(".").concat(Integer.toString(index))),
            new OctetString(DatatypeConverter.parseHexBinary(SNMP.snmpTimestampFromIso(deliveryStop)))
         );
    }

    public static VariableBinding getVbRsuMsgRepeatPayload(int index, String payload) {
        return new VariableBinding(
            new OID(rsu_msg_repeat_payload_oid().concat(".").concat(Integer.toString(index))),
            new OctetString(DatatypeConverter.parseHexBinary(payload))
         );
    }

    public static VariableBinding getVbRsuMsgRepeatEnable(int index, int enable) {
        return new VariableBinding(
            new OID(rsu_msg_repeat_enable_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(enable)
         );
    }

    public static VariableBinding getVbRsuMsgRepeatStatus(int index, int status) {
        return new VariableBinding(
            new OID(rsu_msg_repeat_status_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(status)
         );
    }

    public static VariableBinding getVbRsuMsgRepeatStatus(int index) {
        return new VariableBinding(
            new OID(rsu_msg_repeat_status_oid().concat(".").concat(Integer.toString(index)))
         );
    }

    public static VariableBinding getVbRsuMsgRepeatPriority(int index) {
        return new VariableBinding(
            new OID(rsu_msg_repeat_priority_oid().concat(".").concat(Integer.toString(index))),
            new Integer32(6)
         );
    }

    public static VariableBinding getVbRsuMsgRepeatOptions(int index, int options) {
        byte[] val = {(byte) options};
        return new VariableBinding(
            new OID(rsu_msg_repeat_options_oid().concat(".").concat(Integer.toString(index))),
            new OctetString(val)
         );
    }

    // oids
    private static String rsu_msg_repeat_psid_oid() {
        return ntcip1218prefix + srm_prefix + ".2";
    }

    private static String rsu_msg_repeat_tx_channel_oid() {
        return ntcip1218prefix + srm_prefix + ".3";
    }

    private static String rsu_msg_repeat_tx_interval_oid() {
        return ntcip1218prefix + srm_prefix + ".4";
    }

    private static String rsu_msg_repeat_delivery_start_oid() {
        return ntcip1218prefix + srm_prefix + ".5";
    }

    private static String rsu_msg_repeat_delivery_stop_oid() {
        return ntcip1218prefix + srm_prefix + ".6";
    }

    private static String rsu_msg_repeat_payload_oid() {
        return ntcip1218prefix + srm_prefix + ".7";
    }

    private static String rsu_msg_repeat_enable_oid() {
        return ntcip1218prefix + srm_prefix + ".8";
    }

    private static String rsu_msg_repeat_status_oid() {
        return ntcip1218prefix + srm_prefix + ".9";
    }

    private static String rsu_msg_repeat_priority_oid() {
        return ntcip1218prefix + srm_prefix + ".10";
    }

    private static String rsu_msg_repeat_options_oid() {
        return ntcip1218prefix + srm_prefix + ".11";
    }

    // note: pdm is not defined in NTCIP1218
}
