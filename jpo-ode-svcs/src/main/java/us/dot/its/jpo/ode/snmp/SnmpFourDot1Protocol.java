package us.dot.its.jpo.ode.snmp;

public class SnmpFourDot1Protocol {
    // 4.1 prefix
    private static String fourdot1prefix = "1.0.15628.4.";

    // srm
    private static String srm_prefix = "1.4.1.";

    public static String rsu_srm_psid_oid = fourdot1prefix + srm_prefix + "2";
    public static String rsu_srm_dsrc_msg_id_oid = fourdot1prefix + srm_prefix + "3";
    public static String rsu_srm_tx_mode_oid = fourdot1prefix + srm_prefix + "4";
    public static String rsu_srm_tx_channel_oid = fourdot1prefix + srm_prefix + "5";
    public static String rsu_srm_tx_interval_oid = fourdot1prefix + srm_prefix + "6";
    public static String rsu_srm_delivery_start_oid = fourdot1prefix + srm_prefix + "7";
    public static String rsu_srm_delivery_stop_oid = fourdot1prefix + srm_prefix + "8";
    public static String rsu_srm_payload_oid = fourdot1prefix + srm_prefix + "9";
    public static String rsu_srm_enable_oid = fourdot1prefix + srm_prefix + "10";
    public static String rsu_srm_status_oid = fourdot1prefix + srm_prefix + "11";

    // pdm
    private static String pdm_prefix = "1.200.";

    public static String rsu_pdm_sample_start_oid = fourdot1prefix + pdm_prefix + "1";
    public static String rsu_pdm_sample_end_oid = fourdot1prefix + pdm_prefix + "2";
    public static String rsu_pdm_directions_oid = fourdot1prefix + pdm_prefix + "3";
    public static String rsu_pdm_term_choice_oid = fourdot1prefix + pdm_prefix + "4";
    public static String rsu_pdm_term_time_oid = fourdot1prefix + pdm_prefix + "5";
    public static String rsu_pdm_term_distance_oid = fourdot1prefix + pdm_prefix + "6";
    public static String rsu_pdm_snapshot_choice_oid = fourdot1prefix + pdm_prefix + "7";
    public static String rsu_pdm_min_snapshot_time_oid = fourdot1prefix + pdm_prefix + "9";
    public static String rsu_pdm_max_snapshot_time_oid = fourdot1prefix + pdm_prefix + "10";
    public static String rsu_pdm_min_snapshot_distance_oid = fourdot1prefix + pdm_prefix + "11";
    public static String rsu_pdm_max_snapshot_distance_oid = fourdot1prefix + pdm_prefix + "12";
    public static String rsu_pdm_snapshot_min_speed_oid = fourdot1prefix + pdm_prefix + "13";
    public static String rsu_pdm_snapshot_max_speed_oid = fourdot1prefix + pdm_prefix + "14";
    public static String rsu_pdm_tx_interval_oid = fourdot1prefix + pdm_prefix + "15";

    private static String pdm_vs_req_prefix = pdm_prefix + "15.";

    public static String rsu_pdm_vs_req_tag_oid = fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "1";
    public static String rsu_pdm_vs_req_sub_tag_oid = fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "2";
    public static String rsu_pdm_vs_req_less_then_oid = fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "3";
    public static String rsu_pdm_vs_req_more_then_oid = fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "4";
    public static String rsu_pdm_vs_req_send_all_oid = fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "5";
    public static String rsu_pdm_vs_req_status_oid = fourdot1prefix + pdm_vs_req_prefix + "{}" + "." + "6";

}
