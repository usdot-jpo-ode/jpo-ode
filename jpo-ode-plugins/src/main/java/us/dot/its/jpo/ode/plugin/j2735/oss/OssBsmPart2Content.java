package us.dot.its.jpo.ode.plugin.j2735.oss;

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

	private static final String DECODING_ERROR = "Error decoding OpenType value";
	private static final String NO_OPEN_TYPE = "No OpenType value";

	private OssBsmPart2Content() {
	}

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

		part2Content.setId(id);

		PERUnalignedCoder coder = J2735.getPERUnalignedCoder();

		switch (part2Content.getId()) {
		case SPECIALVEHICLEEXT:
			evaluateSpecialVehicleExt(part2Content, value, coder);
			break;
		case SUPPLEMENTALVEHICLEEXT:
			evaluateSupplementalVehicleExt(part2Content, value, coder);
			break;
		case VEHICLESAFETYEXT:
			evaluateVehicleSafetyExt(part2Content, value, coder);
			break;
		}
		return part2Content;
	}

	private static void evaluateVehicleSafetyExt(J2735BsmPart2Content part2Content, OpenType value,
			PERUnalignedCoder coder) throws OssBsmPart2Exception {
		J2735VehicleSafetyExtensions vehSafety = new J2735VehicleSafetyExtensions();
		part2Content.setValue(vehSafety);

		VehicleSafetyExtensions vse;
		if (value.getDecodedValue() != null) {
			vse = (VehicleSafetyExtensions) value.getDecodedValue();
		} else if (value.getEncodedValueAsStream() != null) {
			vse = new VehicleSafetyExtensions();
			try {
				coder.decode(value.getEncodedValueAsStream(), vse);
			} catch (DecodeFailedException | DecodeNotSupportedException e) {
				throw new OssBsmPart2Exception(DECODING_ERROR, e);
			}
		} else {
			throw new OssBsmPart2Exception(NO_OPEN_TYPE);
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
			vehSafety.setEvents(eventFlags);
		}
		if (vse.hasLights()) {

			J2735ExteriorLights exteriorLights = new J2735ExteriorLights();

			for (int i = 0; i < vse.getLights().getSize(); i++) {
				String lightName = vse.getLights().getNamedBits().getMemberName(i);
				Boolean lightStatus = vse.getLights().getBit(i);

				if (lightName != null) {
					exteriorLights.put(lightName, lightStatus);
				}
			}

			vehSafety.setLights(exteriorLights);

		}
		if (vse.hasPathHistory()) {
			vehSafety.setPathHistory(OssPathHistory.genericPathHistory(vse.pathHistory));
		}
		if (vse.hasPathPrediction()) {
			vehSafety.setPathPrediction(OssPathPrediction.genericPathPrediction(vse.pathPrediction));
		}

	}

	private static void evaluateSpecialVehicleExt(J2735BsmPart2Content part2Content, OpenType value,
			PERUnalignedCoder coder) throws OssBsmPart2Exception {
		J2735SpecialVehicleExtensions specVeh = new J2735SpecialVehicleExtensions();
		part2Content.setValue(specVeh);

		SpecialVehicleExtensions sp;
		if (value.getDecodedValue() != null) {
			sp = (SpecialVehicleExtensions) value.getDecodedValue();
		} else if (value.getEncodedValueAsStream() != null) {
			sp = new SpecialVehicleExtensions();
			try {
				coder.decode(value.getEncodedValueAsStream(), sp);
			} catch (DecodeFailedException | DecodeNotSupportedException e) {
				throw new OssBsmPart2Exception(DECODING_ERROR, e);
			}
		} else {
			throw new OssBsmPart2Exception(NO_OPEN_TYPE);
		}

		if (sp.hasVehicleAlerts()) {
			specVeh.setVehicleAlerts(OssEmergencyDetails.genericEmergencyDetails(sp.vehicleAlerts));
		}
		if (sp.hasDescription()) {
			specVeh.setDescription(OssEventDescription.genericEventDescription(sp.description));
		}
		if (sp.hasTrailers()) {
			specVeh.setTrailers(OssTrailerData.genericTrailerData(sp.trailers));
		}
	}

	private static void evaluateSupplementalVehicleExt(J2735BsmPart2Content part2Content, OpenType value,
			PERUnalignedCoder coder) throws OssBsmPart2Exception {
		J2735SupplementalVehicleExtensions supVeh = new J2735SupplementalVehicleExtensions();
		part2Content.setValue(supVeh);

		SupplementalVehicleExtensions sve;
		if (value.getDecodedValue() != null) {
			sve = (SupplementalVehicleExtensions) value.getDecodedValue();
		} else if (value.getEncodedValueAsStream() != null) {
			sve = new SupplementalVehicleExtensions();
			try {
				coder.decode(value.getEncodedValueAsStream(), sve);
			} catch (DecodeFailedException | DecodeNotSupportedException e) {
				throw new OssBsmPart2Exception(DECODING_ERROR, e);
			}
		} else {
			throw new OssBsmPart2Exception(NO_OPEN_TYPE);
		}
		part2Content.setValue(OssSupplementalVehicleExtensions.genericSupplementalVehicleExtensions(sve));

	}

	public static void buildGenericPart2(List<Sequence_> elements, List<J2735BsmPart2Content> partII)
			throws OssBsmPart2Exception {
		if (elements != null) {
			Iterator<Sequence_> iter = elements.iterator();

			while (iter.hasNext()) {
				partII.add(OssBsmPart2Content.genericPart2Content(iter.next()));
			}
		}
	}

}
