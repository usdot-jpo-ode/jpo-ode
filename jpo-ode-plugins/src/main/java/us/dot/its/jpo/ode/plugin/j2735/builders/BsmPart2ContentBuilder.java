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
   
   public static final int eventHazardLights = 0;
   public static final int eventStopLineViolation = 1;
   public static final int eventABSactivated = 2;
   public static final int eventTractionControlLoss = 3;
   public static final int eventStabilityControlactivated = 4;
   public static final int eventHazardousMaterials = 5;
   public static final int eventReserved1 = 6;
   public static final int eventHardBraking = 7;
   public static final int eventLightsChanged = 8;
   public static final int eventWipersChanged = 9;
   public static final int eventFlatTire = 10;
   public static final int eventDisabledVehicle = 11;
   public static final int eventAirBagDeployment = 12;
   
   
   new MemberListElement[] {
         new MemberListElement (
             "eventHazardLights",
             0
         ),
         new MemberListElement (
             "eventStopLineViolation",
             1
         ),
         new MemberListElement (
             "eventABSactivated",
             2
         ),
         new MemberListElement (
             "eventTractionControlLoss",
             3
         ),
         new MemberListElement (
             "eventStabilityControlactivated",
             4
         ),
         new MemberListElement (
             "eventHazardousMaterials",
             5
         ),
         new MemberListElement (
             "eventReserved1",
             6
         ),
         new MemberListElement (
             "eventHardBraking",
             7
         ),
         new MemberListElement (
             "eventLightsChanged",
             8
         ),
         new MemberListElement (
             "eventWipersChanged",
             9
         ),
         new MemberListElement (
             "eventFlatTire",
             10
         ),
         new MemberListElement (
             "eventDisabledVehicle",
             11
         ),
         new MemberListElement (
             "eventAirBagDeployment",
             12

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

		return buildContent(J2735BsmPart2Content.J2735BsmPart2Id.values()[partII_Id], part2Node);
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

//		VehicleSafetyExtensions vse;
//		if (openType.getDecodedValue() != null) {
//			vse = (VehicleSafetyExtensions) openType.getDecodedValue();
//		} else if (openType.getEncodedValueAsStream() != null) {
//			vse = new VehicleSafetyExtensions();
//			try {
//				coder.decode(openType.getEncodedValueAsStream(), vse);
//			} catch (DecodeFailedException | DecodeNotSupportedException e) {
//				throw new BsmPart2ContentBuilderException(DECODING_ERROR, e);
//			}
//		} else {
//			throw new BsmPart2ContentBuilderException(NO_OPEN_TYPE);
//		}

		
		
		// TODO use xml --> json array code here
		
//		private void setEncodings(JsonNode encodings) {
//	      if (encodings.isArray()) {
//	         Iterator<JsonNode> elements = encodings.elements();
//
//	         while (elements.hasNext()) {
//	            JsonNode element = elements.next();
//	            this.encodings.add(new Asn1Encoding(element.get("elementName").asText(),
//	                  element.get("elementType").asText(), EncodingRule.valueOf(element.get("encodingRule").asText())));
//	         }
//	      }
//	   }
		
		if (vse.get("events") != null) {
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
