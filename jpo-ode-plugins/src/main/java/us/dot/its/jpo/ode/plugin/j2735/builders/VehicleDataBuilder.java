package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleData;

public class VehicleDataBuilder {
    
    private VehicleDataBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735VehicleData genericVehicleData(JsonNode vehicleData) {
        J2735VehicleData vd = new J2735VehicleData();

        if (vehicleData.bumpers != null) {
            vd.setBumpers(BumperHeightsBuilder.genericBumperHeights(vehicleData.bumpers));
        }
        if (vehicleData.height != null) {
            vd.setHeight(HeightBuilder.genericHeight(vehicleData.height));
        }
        if (vehicleData.mass != null) {
            vd.setMass(MassOrWeightBuilder.genericMass(vehicleData.mass));
        }
        if (vehicleData.trailerWeight != null) {
            vd.setTrailerWeight(MassOrWeightBuilder.genericWeight(vehicleData.trailerWeight));
        }

        return vd;
    }

}
