package us.dot.its.jpo.ode.uper;

import lombok.Getter;
import us.dot.its.jpo.ode.util.CodecUtils;

@Getter
public enum SupportedMessageType {
    BSM("0014"),
    TIM("001f"),
    SPAT("0013"),
    SSM("001e"),
    SRM("001d"),
    MAP("0012"),
    PSM("0020"),
    SDSM("0029"),
    RTCM("001c"),
    RSM("0021");

    private final String startFlag;
    private final byte[] startFlagBytes;

    SupportedMessageType(String startFlag) {
        this.startFlag = startFlag;
        this.startFlagBytes = CodecUtils.fromHex(startFlag);
    }
}
