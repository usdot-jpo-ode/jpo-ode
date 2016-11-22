package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public enum J2735DSRCmsgID implements Asn1Object{
   reserved, 
   alaCarteMessage, 
   basicSafetyMessage, 
   basicSafetyMessageVerbose, 
   commonSafetyRequest, 
   emergencyVehicleAlert, 
   intersectionCollisionAlert, 
   mapData, 
   nmeaCorrections, 
   probeDataManagement, 
   probeVehicleData, 
   roadSideAlert, 
   rtcmCorrections, 
   signalPhaseAndTimingMessage, 
   signalRequestMessage, 
   signalStatusMessage, 
   travelerInformation
}