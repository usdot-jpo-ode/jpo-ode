package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SignalRequesterInfo;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleID;
import us.dot.its.jpo.ode.plugin.j2735.J2735BasicVehicleRole;

public class SignalRequesterInfoBuilder {
    private SignalRequesterInfoBuilder() {
		throw new UnsupportedOperationException();
	}

	public static J2735SignalRequesterInfo genericSignalRequesterInfo(JsonNode requester) {
        J2735SignalRequesterInfo signalRequesterInfo = new J2735SignalRequesterInfo();
		
		JsonNode id = requester.get("id");
		if(id != null)
		{
			J2735VehicleID objVehicleId = new J2735VehicleID();

            JsonNode entityID = id.get("entityID");
			if(entityID != null)
			{
				objVehicleId.setEntityID(entityID.asText());
			}

			JsonNode stationID = id.get("stationID");
			if(stationID != null)
			{
				objVehicleId.setStationID(stationID.asLong());
			}

			signalRequesterInfo.setId(objVehicleId);
		}

		JsonNode request = requester.get("request");
		if(request != null)
		{
			signalRequesterInfo.setRequest(request.asInt());
		}

		JsonNode sequenceNumber = requester.get("sequenceNumber");
		if(sequenceNumber != null)
		{
			signalRequesterInfo.setSequenceNumber(sequenceNumber.asInt());
		}

		JsonNode role = requester.get("role");
		if(role != null)
		{
            J2735BasicVehicleRole enumRole = J2735BasicVehicleRole.valueOf(role.fieldNames().next());
			signalRequesterInfo.setRole(enumRole);
		}

		JsonNode typeData = requester.get("typeData");
		if(typeData != null)
		{
			signalRequesterInfo.setTypeData(RequestorTypeBuilder.genericRequestorType(typeData));
		}
		
		return signalRequesterInfo;
	}
}
