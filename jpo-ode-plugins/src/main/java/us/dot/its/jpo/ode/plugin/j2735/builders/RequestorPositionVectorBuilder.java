package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735RequestorPositionVector;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

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
			OdePosition3D positionObj = new OdePosition3D();

            if(position.get("lat") != null)
			{
				positionObj.setLatitude(LatitudeBuilder.genericLatitude(position.get("lat")));
				
			}
			
			if(position.get("long") != null)
			{
				positionObj.setLongitude(LongitudeBuilder.genericLongitude(position.get("long")));
			}
			
			if(position.get("elevation") != null)
			{
				positionObj.setElevation(ElevationBuilder.genericElevation(position.get("elevation")));
			}

			requestorPositionVector.setPosition(positionObj);
		}

        JsonNode heading = positionVector.get("heading");
		if (heading != null)
		{
            requestorPositionVector.setHeading(HeadingBuilder.genericHeading(heading));
        }

        JsonNode speed = positionVector.get("speed");
		if (speed != null)
		{
            requestorPositionVector.setSpeed(TransmissionAndSpeedBuilder.genericTransmissionAndSpeed(speed));
        }

        return requestorPositionVector;
    }
}
