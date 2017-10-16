package us.dot.its.jpo.ode.plugin.j2735;

public enum J2735VehicleType {
   none, // (0), -- Not Equipped, Not known or unavailable
   unknown, // (1), -- Does not fit any other category
   special, // (2), -- Special use
   moto, // (3), -- Motorcycle
   car, // (4), -- Passenger car
   carOther, // (5), -- Four tire single units
   bus, // (6), -- Buses
   axleCnt2, // (7), -- Two axle, six tire single units
   axleCnt3, // (8), -- Three axle, single units
   axleCnt4, // (9), -- Four or more axle, single unit
   axleCnt4Trailer, // (10), -- Four or less axle, single trailer
   axleCnt5Trailer, // (11), -- Five or less axle, single trailer
   axleCnt6Trailer, // (12), -- Six or more axle, single trailer
   axleCnt5MultiTrailer, // (13), -- Five or less axle, multi-trailer
   axleCnt6MultiTrailer, // (14), -- Six axle, multi-trailer
   axleCnt7MultiTrailer // (15),
}
