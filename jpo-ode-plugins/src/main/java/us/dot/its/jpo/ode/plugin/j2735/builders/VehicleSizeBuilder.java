package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSize;

public class VehicleSizeBuilder {

    private static final Integer WIDTH_LOWER_BOUND = 0;
    private static final Integer WIDTH_UPPER_BOUND = 1023;
    private static final Integer LENGTH_LOWER_BOUND = 0;
    private static final Integer LENGTH_UPPER_BOUND = 4095;

    public VehicleSizeBuilder(JsonNode size) {
        if (size.width.intValue() < WIDTH_LOWER_BOUND || size.width.intValue() > WIDTH_UPPER_BOUND) {
            throw new IllegalArgumentException("Vehicle width out of bounds [0..1023]");
        }

        if (size.length.intValue() < LENGTH_LOWER_BOUND || size.length.intValue() > LENGTH_UPPER_BOUND) {
            throw new IllegalArgumentException("Vehicle length out of bounds [0..4095]");
        }

        this.genericVehicleSize = new J2735VehicleSize();

        if (size.length.intValue() != 0) {
            this.genericVehicleSize.setLength(size.length.intValue());
        } else {
            this.genericVehicleSize.setLength(null);
        }
        if (size.width.intValue() != 0) {
            this.genericVehicleSize.setWidth(size.width.intValue());
        } else {
            this.genericVehicleSize.setWidth(null);
        }
    }

}
