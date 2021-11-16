package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RequestorPositionVector;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class RequestorPositionVectorBuilder {
    private RequestorPositionVectorBuilder()
	{
		throw new UnsupportedOperationException();
	}

    public static J2735RequestorPositionVector genericRequestorPositionVector(JsonNode positionVector) {
        J2735RequestorPositionVector requestorPositionVector = new J2735RequestorPositionVector();

        JsonNode position = positionVector.get("position");
		if (position != null)
		{
			J2735Position3D positionObj = new J2735Position3D();

            if(position.get("lat") != null)
			{
				positionObj.setLat(position.get("lat").asInt());
			}
			
			if(position.get("long") != null)
			{
				positionObj.setLon(position.get("long").asInt());
			}
			
			if(position.get("elevation") != null)
			{
				positionObj.setElevation(position.get("elevation").asInt());
			}

			requestorPositionVector.setPosition(positionObj);
		}

        JsonNode heading = positionVector.get("heading");
		if (heading != null)
		{
            requestorPositionVector.setHeading(heading.asInt());
        }

        JsonNode speed = positionVector.get("speed");
		if (speed != null)
		{
            requestorPositionVector.setSpeed(TransmissionAndSpeedBuilder.genericTransmissionAndSpeed(speed));
        }

        return requestorPositionVector;
    }
}
