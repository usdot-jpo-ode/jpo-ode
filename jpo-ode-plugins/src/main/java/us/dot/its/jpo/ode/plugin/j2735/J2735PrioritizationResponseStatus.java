package us.dot.its.jpo.ode.plugin.j2735;

public enum J2735PrioritizationResponseStatus {
    unknown,            // (0), -- Unknown state
    requested,          // (1), -- This prioritization request was detected by the traffic controller
    processing,         // (2), -- Checking request (request is in queue, other requests are prior)
    watchOtherTraffic,  // (3), -- Cannot give full permission, therefore watch for other traffic. 
                        // Note that other requests may be present
    granted,            // (4), -- Intervention was successful and now prioritization is active
    rejected,           // (5), -- The prioritization or preemption request was rejected by the traffic controller
    maxPresence,        // (6), -- The Request has exceeded maxPresence time. Used when the controller has determined 
                        // that the requester should then back off and request an alternative.
    reserviceLocked     // (7), -- Prior conditions have resulted in a reservice locked event: the controller
                        // requires the passage of time before another similar request will be accepted
 }