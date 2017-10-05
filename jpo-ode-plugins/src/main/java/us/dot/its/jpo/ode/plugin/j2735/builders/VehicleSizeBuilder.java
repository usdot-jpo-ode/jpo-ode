package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSize;

public class VehicleSizeBuilder {

   private static final Integer WIDTH_LOWER_BOUND = 0;
   private static final Integer WIDTH_UPPER_BOUND = 1023;
   private static final Integer LENGTH_LOWER_BOUND = 0;
   private static final Integer LENGTH_UPPER_BOUND = 4095;

   public static J2735VehicleSize genericVehicleSizeBuilder(JsonNode vehicleSize) {
       int size = vehicleSize.get("width").intValue();
       
        if (size < WIDTH_LOWER_BOUND || size > WIDTH_UPPER_BOUND) {
            throw new IllegalArgumentException("Vehicle width out of bounds [0..1023]");
        }

        int length = vehicleSize.get("length").intValue();
        if (length < LENGTH_LOWER_BOUND || length > LENGTH_UPPER_BOUND) {
            throw new IllegalArgumentException("Vehicle length out of bounds [0..4095]");
        }

        J2735VehicleSize genericVehicleSize = new J2735VehicleSize();

        if (length != 0) {
            genericVehicleSize.setLength(length);
        } else {
            genericVehicleSize.setLength(null);
        }
        
        int width = vehicleSize.get("width").intValue();
        if (width != 0) {
            genericVehicleSize.setWidth(width);
        } else {
            genericVehicleSize.setWidth(null);
        }
        
        return genericVehicleSize;
    }

}
