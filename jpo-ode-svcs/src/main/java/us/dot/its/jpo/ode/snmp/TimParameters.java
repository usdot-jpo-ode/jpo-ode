package us.dot.its.jpo.ode.snmp;

/**
 * Container class for all parameters required by the RSU to handle an SNMP PDU.
 */
public class TimParameters {

    String rsuSRMPsid;
    int rsuSRMDsrcMsgId;
    int rsuSRMTxMode;
    int rsuSRMTxChannel;
    int rsuSRMTxInterval;
    String rsuSRMDeliveryStart;
    String rsuSRMDeliveryStop;
    String rsuSRMPayload;
    int rsuSRMEnable;
    int rsuSRMStatus;

    public TimParameters(String rsuSRMPsid, int rsuSRMDsrcMsgId, int rsuSRMTxMode, int rsuSRMTxChannel,
            int rsuSRMTxInterval, String rsuSRMDeliveryStart, String rsuSRMDeliveryStop, String rsuSRMPayload,
            int rsuSRMEnable, int rsuSRMStatus) {

        this.rsuSRMPsid = rsuSRMPsid;
        this.rsuSRMDsrcMsgId = rsuSRMDsrcMsgId;
        this.rsuSRMTxMode = rsuSRMTxMode;
        this.rsuSRMTxChannel = rsuSRMTxChannel;
        this.rsuSRMTxInterval = rsuSRMTxInterval;
        this.rsuSRMDeliveryStart = rsuSRMDeliveryStart;
        this.rsuSRMDeliveryStop = rsuSRMDeliveryStop;
        this.rsuSRMPayload = rsuSRMPayload;
        this.rsuSRMEnable = rsuSRMEnable;
        this.rsuSRMStatus = rsuSRMStatus;
    }
}
