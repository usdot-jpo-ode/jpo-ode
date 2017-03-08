package us.dot.its.jpo.ode.snmp;

import java.text.ParseException;
import java.time.ZonedDateTime;

import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

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
            int rsuSRMEnable, int rsuSRMStatus) throws ParseException {

        this.rsuSRMPsid = rsuSRMPsid;
        this.rsuSRMDsrcMsgId = rsuSRMDsrcMsgId;
        this.rsuSRMTxMode = rsuSRMTxMode;
        this.rsuSRMTxChannel = rsuSRMTxChannel;
        this.rsuSRMTxInterval = rsuSRMTxInterval;
        this.rsuSRMPayload = rsuSRMPayload;
        this.rsuSRMEnable = rsuSRMEnable;
        this.rsuSRMStatus = rsuSRMStatus;

        ZonedDateTime zStart = DateTimeUtils.isoDateTime(rsuSRMDeliveryStart);
        byte[] bStart = new byte[6];
        bStart[0] = (byte) zStart.getMonthValue();
        bStart[1] = (byte) zStart.getDayOfMonth();
        bStart[2] = (byte) (zStart.getYear()/100);
        bStart[3] = (byte) (zStart.getYear()%100);
        bStart[4] = (byte) zStart.getHour();
        bStart[5] = (byte) zStart.getMinute();
        this.rsuSRMDeliveryStart = CodecUtils.toHex(bStart);

        ZonedDateTime zStop = DateTimeUtils.isoDateTime(rsuSRMDeliveryStop);
        byte[] bStop = new byte[6];
        bStop[0] = (byte) zStop.getMonthValue();
        bStop[1] = (byte) zStop.getDayOfMonth();
        bStop[2] = (byte) (zStop.getYear()/100);
        bStop[3] = (byte) (zStop.getYear()%100);
        bStop[4] = (byte) zStop.getHour();
        bStop[5] = (byte) zStop.getMinute();
        this.rsuSRMDeliveryStop = CodecUtils.toHex(bStop);
    }
}
