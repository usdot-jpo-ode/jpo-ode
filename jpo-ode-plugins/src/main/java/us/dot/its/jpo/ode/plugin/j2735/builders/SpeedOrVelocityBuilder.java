package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

public class SpeedOrVelocityBuilder {

    private SpeedOrVelocityBuilder() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericSpeed(JsonNode speed) {
        return genericSpeedOrVelocity(speed.asInt());
    }

    public static BigDecimal genericVelocity(JsonNode velocity) {
        return genericSpeedOrVelocity(velocity.asInt());
    }

    private static BigDecimal genericSpeedOrVelocity(int speedOrVelocity) {

        if (speedOrVelocity < 0 || speedOrVelocity > 8191) {
            throw new IllegalArgumentException("Speed or velocity out of bounds");
        }

        BigDecimal result = null;

        if (speedOrVelocity != 8191) {
            result = BigDecimal.valueOf(speedOrVelocity * (long) 2, 2);
        }

        return result;

    }
}
