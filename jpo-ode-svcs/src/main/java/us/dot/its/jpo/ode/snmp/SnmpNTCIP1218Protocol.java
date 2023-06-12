package us.dot.its.jpo.ode.snmp;

public class SnmpNTCIP1218Protocol {
    // ntcip1218 prefix
    private static String ntcip1218prefix = "1.3.6.1.4.1.1206.4.2.18.";

    // srm (defined in NTCIP1218 Section 5.4.2)
    private static String srm_prefix = "3.2.1";

    public static String rsu_msg_repeat_psid_oid = ntcip1218prefix + srm_prefix + ".2"; // Section 5.4.2.2
    public static String rsu_msg_repeat_tx_channel_oid = ntcip1218prefix + srm_prefix + ".3"; // Section 5.4.2.3
    public static String rsu_msg_repeat_tx_interval_oid = ntcip1218prefix + srm_prefix + ".4"; // Section 5.4.2.4
    public static String rsu_msg_repeat_delivery_start_oid = ntcip1218prefix + srm_prefix + ".5"; // Section 5.4.2.4
    public static String rsu_msg_repeat_delivery_stop_oid = ntcip1218prefix + srm_prefix + ".6"; // Section 5.4.2.6
    public static String rsu_msg_repeat_payload_oid = ntcip1218prefix + srm_prefix + ".7"; // Section 5.4.2.7
    public static String rsu_msg_repeat_enable_oid = ntcip1218prefix + srm_prefix + ".8"; // Section 5.4.2.8
    public static String rsu_msg_repeat_status_oid = ntcip1218prefix + srm_prefix + ".9"; // Section 5.4.2.9
    public static String rsu_msg_repeat_priority_oid = ntcip1218prefix + srm_prefix + ".10"; // Section 5.4.2.10
    public static String rsu_msg_repeat_options_oid = ntcip1218prefix + srm_prefix + ".11"; // Section 5.4.2.11

    // pdm is not defined in NTCIP1218
}
