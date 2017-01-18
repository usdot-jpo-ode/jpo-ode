package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.OpenType;
import com.oss.asn1.PERUnalignedCoder;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage.PartII.Sequence_;
import us.dot.its.jpo.ode.j2735.dsrc.SpecialVehicleExtensions;
import us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSafetyExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735ExteriorLights;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpecialVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleEventFlags;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;

public class OssBsmPart2Content {

    public static class OssBsmPart2Exception extends Exception {

        private static final long serialVersionUID = 7318127023245642955L;

        public OssBsmPart2Exception(String msg) {
            super(msg);
        }

        public OssBsmPart2Exception(String msg, Exception e) {
            super(msg, e);
        }

    }

    public static J2735BsmPart2Content genericPart2Content(
            us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage.PartII.Sequence_ seq) throws OssBsmPart2Exception {

        return buildContent(J2735BsmPart2Content.J2735BsmPart2Id.values()[seq.partII_Id.intValue()], seq.partII_Value);
    }

    private static J2735BsmPart2Content buildContent(J2735BsmPart2Content.J2735BsmPart2Id id, OpenType value)
            throws OssBsmPart2Exception {
        J2735BsmPart2Content part2Content = new J2735BsmPart2Content();

        part2Content.id = id;

        PERUnalignedCoder coder = J2735.getPERUnalignedCoder();

        switch (part2Content.id) {
        case specialVehicleExt:
            J2735SpecialVehicleExtensions specVeh = new J2735SpecialVehicleExtensions();
            part2Content.value = specVeh;

            SpecialVehicleExtensions sp = null;
            if (value.getDecodedValue() != null) {
                sp = (SpecialVehicleExtensions) value.getDecodedValue();
            } else if (value.getEncodedValueAsStream() != null) {
                sp = new SpecialVehicleExtensions();
                try {
                    coder.decode(value.getEncodedValueAsStream(), sp);
                } catch (DecodeFailedException | DecodeNotSupportedException e) {
                    throw new OssBsmPart2Exception("Error decoding OpenType value", e);
                }
            } else {
                throw new OssBsmPart2Exception("No OpenType value");
            }

            if (sp.hasVehicleAlerts()) {
                specVeh.vehicleAlerts = OssEmergencyDetails.genericEmergencyDetails(sp.vehicleAlerts);
            }
            if (sp.hasDescription()) {
                specVeh.description = OssEventDescription.genericEventDescription(sp.description);
            }
            if (sp.hasTrailers()) {
                specVeh.trailers = OssTrailerData.genericTrailerData(sp.trailers);
            }
            break;
        case supplementalVehicleExt:
            J2735SupplementalVehicleExtensions supVeh = new J2735SupplementalVehicleExtensions();
            part2Content.value = supVeh;
            
            SupplementalVehicleExtensions sve = null;
            if (value.getDecodedValue() != null) {
                sve = (SupplementalVehicleExtensions) value.getDecodedValue();
            } else if (value.getEncodedValueAsStream() != null) {
                sve = new SupplementalVehicleExtensions();
                try {
                    coder.decode(value.getEncodedValueAsStream(), sve);
                } catch (DecodeFailedException | DecodeNotSupportedException e) {
                    throw new OssBsmPart2Exception("Error decoding OpenType value", e);
                }
            } else {
                throw new OssBsmPart2Exception("No OpenType value");
            }
            part2Content.value = OssSupplementalVehicleExtensions
                    .genericSupplementalVehicleExtensions(sve);

            break;
        case vehicleSafetyExt:
            J2735VehicleSafetyExtensions vehSafety = new J2735VehicleSafetyExtensions();
            part2Content.value = vehSafety;

            VehicleSafetyExtensions vse = null;
            if (value.getDecodedValue() != null) {
                vse = (VehicleSafetyExtensions) value.getDecodedValue();
            } else if (value.getEncodedValueAsStream() != null) {
                vse = new VehicleSafetyExtensions();
                try {
                    coder.decode(value.getEncodedValueAsStream(), vse);
                } catch (DecodeFailedException | DecodeNotSupportedException e) {
                    throw new OssBsmPart2Exception("Error decoding OpenType value", e);
                }
            } else {
                throw new OssBsmPart2Exception("No OpenType value");
            }

            if (vse.hasEvents()) {
                J2735VehicleEventFlags eventFlags = new J2735VehicleEventFlags();
                for (int i = 0; i < vse.getEvents().getSize(); i++) {
                    String flagName = vse.getEvents().getNamedBits().getMemberName(i);
                    Boolean flagStatus = vse.getEvents().getBit(vse.getEvents().getSize() - i - 1);

                    if (flagName != null) {
                        eventFlags.put(flagName, flagStatus);
                    }
                }
                vehSafety.events = eventFlags;
            }
            if (vse.hasLights()) {
                
                J2735ExteriorLights exteriorLights = new J2735ExteriorLights();
                
                for (int i = 0; i < vse.getLights().getSize(); i++) {
                    String lightName = vse.getLights().getNamedBits().getMemberName(i);
                    //Boolean lightStatus = vse.getLights().getBit(vse.getLights().getSize() - i - 1);
                    Boolean lightStatus = vse.getLights().getBit(i);

                    if (lightName != null) {
                        exteriorLights.put(lightName, lightStatus);
                    }
                }
                
                vehSafety.lights = exteriorLights;
                
            }
            if (vse.hasPathHistory()) {
                vehSafety.pathHistory = OssPathHistory.genericPathHistory(vse.pathHistory);
            }
            if (vse.hasPathPrediction()) {
                vehSafety.pathPrediction = OssPathPrediction.genericPathPrediction(vse.pathPrediction);
            }

            break;
        }
        return part2Content;
    }

    public static void buildGenericPart2(ArrayList<Sequence_> elements, List<J2735BsmPart2Content> partII)
            throws OssBsmPart2Exception {
        if (elements != null) {
            Iterator<Sequence_> iter = elements.iterator();

            while (iter.hasNext()) {
                partII.add(OssBsmPart2Content.genericPart2Content(iter.next()));
            }
        }
    }

}
