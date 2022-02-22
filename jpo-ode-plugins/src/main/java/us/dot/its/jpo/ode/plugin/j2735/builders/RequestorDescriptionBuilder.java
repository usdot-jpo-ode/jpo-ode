package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RequestorDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleID;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransitVehicleOccupancy;
import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransitVehicleStatusNames;

public class RequestorDescriptionBuilder {
    private RequestorDescriptionBuilder()
	{
		throw new UnsupportedOperationException();
	}

    public static J2735RequestorDescription genericRequestorDescription(JsonNode requestor) {
        J2735RequestorDescription requestorDescription = new J2735RequestorDescription();

        System.out.println("RequestorDescriptionBuilder: Configuring id...");

        JsonNode id = requestor.get("id");
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

			requestorDescription.setId(objVehicleId);
        }

        System.out.println("RequestorDescriptionBuilder: Configuring type...");

        JsonNode type = requestor.get("type");
		if(type != null)
		{
            requestorDescription.setType(RequestorTypeBuilder.genericRequestorType(type));
        }

        System.out.println("RequestorDescriptionBuilder: Configuring position...");

        JsonNode position = requestor.get("position");
		if(position != null)
		{
            requestorDescription.setPosition(RequestorPositionVectorBuilder.genericRequestorPositionVector(position));
        }

        System.out.println("RequestorDescriptionBuilder: Configuring name...");

        JsonNode name = requestor.get("name");
		if(name != null)
		{
            requestorDescription.setName(name.asText());
        }

        System.out.println("RequestorDescriptionBuilder: Configuring routeName...");

        JsonNode routeName = requestor.get("routeName");
		if(routeName != null)
		{
            requestorDescription.setRouteName(routeName.asText());
        }

        System.out.println("RequestorDescriptionBuilder: Configuring transitStatus...");

        JsonNode transitStatus = requestor.get("transitStatus");
		if(transitStatus != null)
		{
            J2735BitString transitStatusObj = BitStringBuilder.genericBitString(transitStatus, J2735TransitVehicleStatusNames.values());
            requestorDescription.setTransitStatus(transitStatusObj);
        }

        System.out.println("RequestorDescriptionBuilder: Configuring transitOccupancy...");

        JsonNode transitOccupancy = requestor.get("transitOccupancy");
		if(transitOccupancy != null)
		{
            requestorDescription.setTransitOccupancy(J2735TransitVehicleOccupancy.valueOf(transitOccupancy.fieldNames().next()));
        }

        System.out.println("RequestorDescriptionBuilder: Configuring transitSchedule...");

        JsonNode transitSchedule = requestor.get("transitSchedule");
		if(transitSchedule != null)
		{
            requestorDescription.setTransitSchedule(transitSchedule.asInt());
        }

        return requestorDescription;
    }
}
