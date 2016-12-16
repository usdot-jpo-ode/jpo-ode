package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.oss.asn1.OpenType;

import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage.PartII.Sequence_;
import us.dot.its.jpo.ode.j2735.dsrc.SpecialVehicleExtensions;
import us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSafetyExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpecialVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;

public class OssBsmPart2Content {

	public static J2735BsmPart2Content genericPart2Content(
			us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage.PartII.Sequence_ seq) {
		
		return buildContent(J2735BsmPart2Content.J2735BsmPart2Id.values()[seq.partII_Id.intValue()],
				seq.partII_Value);
	}

	private static J2735BsmPart2Content buildContent(
			J2735BsmPart2Content.J2735BsmPart2Id id, 
			OpenType value) {
		J2735BsmPart2Content part2Content = new J2735BsmPart2Content();

		part2Content.id = id;
		
		switch (part2Content.id) {
		case specialVehicleExt:
			J2735SpecialVehicleExtensions specVeh = new J2735SpecialVehicleExtensions();
			part2Content.value = specVeh;
			
			SpecialVehicleExtensions sp = (SpecialVehicleExtensions) value.getDecodedValue();

			specVeh.vehicleAlerts = OssEmergencyDetails.genericEmergencyDetails(sp.vehicleAlerts); 
			specVeh.description = OssEventDescription.genericEventDescription(sp.description);
			specVeh.trailers = OssTrailerData.genericTrailerData(sp.trailers);
			break;
		case supplementalVehicleExt:
			part2Content.value = 
				OssSupplementalVehicleExtensions.genericSupplementalVehicleExtensions(
					(SupplementalVehicleExtensions) value.getDecodedValue());

			break;
		case vehicleSafetyExt:
			J2735VehicleSafetyExtensions vehSafety = new J2735VehicleSafetyExtensions();
			part2Content.value = vehSafety;
			
			VehicleSafetyExtensions vse = (VehicleSafetyExtensions) value.getDecodedValue();
			
			vehSafety.events = OssBitString.genericBitString(vse.events);
			vehSafety.lights = OssBitString.genericBitString(vse.lights);
			vehSafety.pathHistory = OssPathHistory.genericPathHistory(vse.pathHistory);
			vehSafety.pathPrediction = OssPathPrediction.genericPathPrediction(vse.pathPrediction);
			
			break;
		}
		return part2Content;
	}

	public static void buildGenericPart2(
			ArrayList<Sequence_> elements, 
			List<J2735BsmPart2Content> partII) {
		if (elements != null) {
			Iterator<Sequence_> iter = elements.iterator();
			
			while (iter.hasNext()) {
				partII.add(OssBsmPart2Content.genericPart2Content(iter.next()));
			}
		}
	}

}
