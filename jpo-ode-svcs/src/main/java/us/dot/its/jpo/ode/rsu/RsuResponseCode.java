package us.dot.its.jpo.ode.rsu;

public enum RsuResponseCode {
    SUCCESS, DUPLICATE_MESSAGE, POSSIBLE_SNMP_PROTOCOL_MISMATCH;

    public static RsuResponseCode fromInt(int i) {
        return switch (i) {
            case 0 -> SUCCESS;
            case 5 -> DUPLICATE_MESSAGE;
            case 10 -> POSSIBLE_SNMP_PROTOCOL_MISMATCH;
            default -> null;
        };
    }
}
