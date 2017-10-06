package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Content.J2735BsmPart2Id;
import us.dot.its.jpo.ode.plugin.j2735.builders.BsmPart2ContentBuilder.BsmPart2ContentBuilderException;
import us.dot.its.jpo.ode.plugin.j2735.J2735ExteriorLights;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpecialVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleEventFlags;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;

public class BsmPart2ContentBuilder {
   
//   public static final int eventHazardLights = 0;
//   public static final int eventStopLineViolation = 1;
//   public static final int eventABSactivated = 2;
//   public static final int eventTractionControlLoss = 3;
//   public static final int eventStabilityControlactivated = 4;
//   public static final int eventHazardousMaterials = 5;
//   public static final int eventReserved1 = 6;
//   public static final int eventHardBraking = 7;
//   public static final int eventLightsChanged = 8;
//   public static final int eventWipersChanged = 9;
//   public static final int eventFlatTire = 10;
//   public static final int eventDisabledVehicle = 11;
//   public static final int eventAirBagDeployment = 12;
//   
//   public enum VehicleEventFlags {
//      eventHazardLights(0),
//      eventStopLineViolation(1),
//      eventABSactivated(2),
//      eventTractionControlLoss(3),
//      eventStabilityControlactivated(4),
//      eventHazardousMaterials(5),
//      eventReserved1(6),
//      eventHardBraking(7),
//      eventLightsChanged(8),
//      eventWipersChanged(9)
//      eventFlatTire(10),
//      eventDisabledVehicle(11),
//      eventAirBagDeployment(12)
//   }
   

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

	public static J2735BsmPart2Content genericPart2Content(JsonNode bsmPart2Seq) throws BsmPart2ContentBuilderException {
	   
	   int partII_Id = bsmPart2Seq.get("partII-Id").asInt();
	   
	   String partII_valuestr = bsmPart2Seq.get("partII-Id").get("partII-Value").asText();
	   
	   JsonNode part2Node = bsmPart2Seq.get("partII-Id").get("partII-Value").get(partII_valuestr);

<<<<<<< HEAD
		return buildContent(J2735BsmPart2Content.J2735BsmPart2Id.values()[partII_Id], part2Node);
=======
		return buildContent(J2735BsmPart2Content.J2735BsmPart2Id.values()[bsmPart2Seq.partII_Id.asInt()], bsmPart2Seq.partII_Value);
>>>>>>> 6ccd666a85548f9c5bec2d2c33c7652b294cdc30
	}

	private static J2735BsmPart2Content buildContent(
	   J2735BsmPart2Content.J2735BsmPart2Id id, JsonNode openType)
			throws BsmPart2ContentBuilderException {
	   
		J2735BsmPart2Content part2Content = new J2735BsmPart2Content();
		part2Content.setId(id);

		switch (id) {
		case VEHICLESAFETYEXT:
         evaluateVehicleSafetyExt(part2Content, openType);
         break;
		case SPECIALVEHICLEEXT:
			evaluateSpecialVehicleExt(part2Content, openType);
			break;
		case SUPPLEMENTALVEHICLEEXT:
			evaluateSupplementalVehicleExt(part2Content, openType);
			break;
		}
		return part2Content;
	}

	private static void evaluateVehicleSafetyExt(J2735BsmPart2Content part2Content, JsonNode openType) throws BsmPart2ContentBuilderException {
		J2735VehicleSafetyExtensions vehSafety = new J2735VehicleSafetyExtensions();
		part2Content.setValue(vehSafety);
		
		if (openType.get("events") != null) {

		   char[] eventBits = openType.get("events").asText().toCharArray();
		   
			J2735VehicleEventFlags eventFlags = new J2735VehicleEventFlags();
			
         eventFlags.put("eventHazardLights", (eventBits[12] == '1') ? true: false );
         eventFlags.put("eventStopLineViolation", (eventBits[11] == '1') ? true: false );
         eventFlags.put("eventABSactivated", (eventBits[10] == '1') ? true: false );
         eventFlags.put("eventTractionControlLoss", (eventBits[9] == '1') ? true: false );
         eventFlags.put("eventStabilityControlactivated", (eventBits[8] == '1') ? true: false );
         eventFlags.put("eventHazardousMaterials", (eventBits[7] == '1') ? true: false );
         eventFlags.put("eventReserved1", (eventBits[6] == '1') ? true: false );
         eventFlags.put("eventHardBraking", (eventBits[5] == '1') ? true: false );
         eventFlags.put("eventLightsChanged", (eventBits[4] == '1') ? true: false );
         eventFlags.put("eventWipersChanged", (eventBits[3] == '1') ? true: false );
         eventFlags.put("eventFlatTire", (eventBits[2] == '1') ? true: false );
         eventFlags.put("eventDisabledVehicle", (eventBits[1] == '1') ? true: false );
         eventFlags.put("eventWipersCeventAirBagDeploymenthanged", (eventBits[0] == '1') ? true: false );
         
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

	private static void evaluateSpecialVehicleExt(J2735BsmPart2Content part2Content, JsonNode openType) throws BsmPart2ContentBuilderException {
		J2735SpecialVehicleExtensions specVeh = new J2735SpecialVehicleExtensions();
		part2Content.setValue(specVeh);

//		SpecialVehicleExtensions sp;
//		if (openType.getDecodedValue() != null) {
//			sp = (SpecialVehicleExtensions) openType.getDecodedValue();
//		} else if (openType.getEncodedValueAsStream() != null) {
//			sp = new SpecialVehicleExtensions();
//			try {
//				coder.decode(openType.getEncodedValueAsStream(), sp);
//			} catch (DecodeFailedException | DecodeNotSupportedException e) {
//				throw new BsmPart2ContentBuilderException(DECODING_ERROR, e);
//			}
//		} else {
//			throw new BsmPart2ContentBuilderException(NO_OPEN_TYPE);
//		}

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

	private static void evaluateSupplementalVehicleExt(J2735BsmPart2Content part2Content, JsonNode openType) throws BsmPart2ContentBuilderException {
		J2735SupplementalVehicleExtensions supVeh = new J2735SupplementalVehicleExtensions();
		part2Content.setValue(supVeh);

//		SupplementalVehicleExtensions sve;
//		if (openType.getDecodedValue() != null) {
//			sve = (SupplementalVehicleExtensions) openType.getDecodedValue();
//		} else if (openType.getEncodedValueAsStream() != null) {
//			sve = new SupplementalVehicleExtensions();
//			try {
//				coder.decode(openType.getEncodedValueAsStream(), sve);
//			} catch (DecodeFailedException | DecodeNotSupportedException e) {
//				throw new BsmPart2ContentBuilderException(DECODING_ERROR, e);
//			}
//		} else {
//			throw new BsmPart2ContentBuilderException(NO_OPEN_TYPE);
//		}
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
