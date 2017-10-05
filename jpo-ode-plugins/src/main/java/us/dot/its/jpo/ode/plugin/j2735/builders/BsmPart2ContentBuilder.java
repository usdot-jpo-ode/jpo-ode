package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735ExteriorLights;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpecialVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleEventFlags;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;

public class BsmPart2ContentBuilder {

	private static final String DECODING_ERROR = "Error decoding OpenType value";
	private static final String NO_OPEN_TYPE = "No OpenType value";

	private BsmPart2ContentBuilder() {
	   throw new UnsupportedOperationException();
	}

	public static class BsmPart2ContentBuilderException extends Exception {

		private static final long serialVersionUID = 7318127023245642955L;

		public BsmPart2ContentBuilderException(String msg) {
			super(msg);
		}

		public BsmPart2ContentBuilderException(String msg, Exception e) {
			super(msg, e);
		}

	}

	public static J2735BsmPart2Content genericPart2Content(JsonNode bsmPart2Seq) {

		return buildContent(J2735BsmPart2Content.J2735BsmPart2Id.values()[bsmPart2Seq.partII_Id.intValue()], bsmPart2Seq.partII_Value);
	}

	private static J2735BsmPart2Content buildContent(
	   J2735BsmPart2Content.J2735BsmPart2Id id, JsonNode openType)
			throws BsmPart2ContentBuilderException {
		J2735BsmPart2Content part2Content = new J2735BsmPart2Content();

		part2Content.setId(id);

		PERUnalignedCoder coder = J2735.getPERUnalignedCoder();

		switch (part2Content.getId()) {
		case SPECIALVEHICLEEXT:
			evaluateSpecialVehicleExt(part2Content, openType, coder);
			break;
		case SUPPLEMENTALVEHICLEEXT:
			evaluateSupplementalVehicleExt(part2Content, openType, coder);
			break;
		case VEHICLESAFETYEXT:
			evaluateVehicleSafetyExt(part2Content, openType, coder);
			break;
		}
		return part2Content;
	}

	private static void evaluateVehicleSafetyExt(J2735BsmPart2Content part2Content, JsonNode openType,
			PERUnalignedCoder coder) throws BsmPart2ContentBuilderException {
		J2735VehicleSafetyExtensions vehSafety = new J2735VehicleSafetyExtensions();
		part2Content.setValue(vehSafety);

		VehicleSafetyExtensions vse;
		if (openType.getDecodedValue() != null) {
			vse = (VehicleSafetyExtensions) openType.getDecodedValue();
		} else if (openType.getEncodedValueAsStream() != null) {
			vse = new VehicleSafetyExtensions();
			try {
				coder.decode(openType.getEncodedValueAsStream(), vse);
			} catch (DecodeFailedException | DecodeNotSupportedException e) {
				throw new BsmPart2ContentBuilderException(DECODING_ERROR, e);
			}
		} else {
			throw new BsmPart2ContentBuilderException(NO_OPEN_TYPE);
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

	private static void evaluateSpecialVehicleExt(J2735BsmPart2Content part2Content, JsonNode openType,
			PERUnalignedCoder coder) throws BsmPart2ContentBuilderException {
		J2735SpecialVehicleExtensions specVeh = new J2735SpecialVehicleExtensions();
		part2Content.setValue(specVeh);

		SpecialVehicleExtensions sp;
		if (openType.getDecodedValue() != null) {
			sp = (SpecialVehicleExtensions) openType.getDecodedValue();
		} else if (openType.getEncodedValueAsStream() != null) {
			sp = new SpecialVehicleExtensions();
			try {
				coder.decode(openType.getEncodedValueAsStream(), sp);
			} catch (DecodeFailedException | DecodeNotSupportedException e) {
				throw new BsmPart2ContentBuilderException(DECODING_ERROR, e);
			}
		} else {
			throw new BsmPart2ContentBuilderException(NO_OPEN_TYPE);
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

	private static void evaluateSupplementalVehicleExt(J2735BsmPart2Content part2Content, JsonNode openType,
			PERUnalignedCoder coder) throws BsmPart2ContentBuilderException {
		J2735SupplementalVehicleExtensions supVeh = new J2735SupplementalVehicleExtensions();
		part2Content.setValue(supVeh);

		SupplementalVehicleExtensions sve;
		if (openType.getDecodedValue() != null) {
			sve = (SupplementalVehicleExtensions) openType.getDecodedValue();
		} else if (openType.getEncodedValueAsStream() != null) {
			sve = new SupplementalVehicleExtensions();
			try {
				coder.decode(openType.getEncodedValueAsStream(), sve);
			} catch (DecodeFailedException | DecodeNotSupportedException e) {
				throw new BsmPart2ContentBuilderException(DECODING_ERROR, e);
			}
		} else {
			throw new BsmPart2ContentBuilderException(NO_OPEN_TYPE);
		}
		part2Content.setValue(SupplementalVehicleExtensionsBuilder.genericSupplementalVehicleExtensions(sve));

	}

	public static void buildGenericPart2(List<Sequence_> elements, List<J2735BsmPart2Content> partII)
			throws BsmPart2ContentBuilderException {
		if (elements != null) {
			Iterator<Sequence_> iter = elements.iterator();

			while (iter.hasNext()) {
				partII.add(BsmPart2ContentBuilder.genericPart2Content(iter.next()));
			}
		}
	}

}
