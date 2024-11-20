package us.dot.its.jpo.ode.uper;

import lombok.Getter;

@Getter
public enum SupportedMessageType {
    BSM("0014"),
    TIM("001f"),
    SPAT("0013"),
    SSM("001e"),
    SRM("001d"),
    MAP("0012"),
    PSM("0020");

    private final String startFlag;

    SupportedMessageType(String startFlag) {
        this.startFlag = startFlag;
    }
}
