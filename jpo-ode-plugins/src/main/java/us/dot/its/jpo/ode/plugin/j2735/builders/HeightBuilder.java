package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleHeight;

public class HeightBuilder {

    private HeightBuilder() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericHeight(VehicleHeight height) {

        if (height.asInt() < 0 || height.asInt() > 127) {
            throw new IllegalArgumentException("Vehicle height out of bounds");
        }

        return BigDecimal.valueOf(height.asInt() * (long) 5, 2);
    }

}
