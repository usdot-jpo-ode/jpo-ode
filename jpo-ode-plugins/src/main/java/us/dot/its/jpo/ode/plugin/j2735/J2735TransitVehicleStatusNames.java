package us.dot.its.jpo.ode.plugin.j2735;

public enum J2735TransitVehicleStatusNames {
    loading, // (0), -- parking and unable to move at this time
    anADAuse, // (1), -- an ADA access is in progress (wheelchairs, kneeling, etc.)
    aBikeLoad, // (2), -- loading of a bicycle is in progress
    doorOpen, // (3), -- a vehicle door is open for passenger access
    charging, // (4), -- a vehicle is connected to charging point
    atStopLine
}
