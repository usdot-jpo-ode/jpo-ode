package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SignalRequest;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionReferenceID;
import us.dot.its.jpo.ode.plugin.j2735.J2735PriorityRequestType;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionAccessPoint;

public class SignalRequestBuilder {
    private SignalRequestBuilder()
	{
		throw new UnsupportedOperationException();
	}

    public static J2735SignalRequest genericSignalRequest(JsonNode request) {
		J2735SignalRequest signalRequest = new J2735SignalRequest();
		
        JsonNode id = request.get("id");
		if(id != null)
		{
			J2735IntersectionReferenceID idObj = new J2735IntersectionReferenceID();

			JsonNode region = id.get("region");
			if (region != null)
			{
				idObj.setRegion(region.asInt());
			}

			JsonNode refId = id.get("id");
			if (refId != null)
			{
				idObj.setId(refId.asInt());
			}

			signalRequest.setId(idObj);
		}

        JsonNode requestId = request.get("requestID");
		if(requestId != null)
		{
            signalRequest.setRequestID(requestId.asInt());
        }

        JsonNode requestType = request.get("requestType");
		if(requestType != null)
		{
            signalRequest.setRequestType(J2735PriorityRequestType.valueOf(requestType.fieldNames().next()));
        }

        JsonNode inBoundLane = request.get("inBoundLane");
		if(inBoundLane != null)
		{
            J2735IntersectionAccessPoint objIntersectionAccessPoint = new J2735IntersectionAccessPoint();

			JsonNode lane = inBoundLane.get("lane");
			if(lane != null)
			{
				objIntersectionAccessPoint.setLane(lane.asInt());
			}

            JsonNode approach = inBoundLane.get("approach");
			if(approach != null)
			{
				objIntersectionAccessPoint.setApproach(approach.asInt());
			}

            JsonNode connection = inBoundLane.get("connection");
			if(connection != null)
			{
				objIntersectionAccessPoint.setConnection(connection.asInt());
			}

			signalRequest.setInBoundLane(objIntersectionAccessPoint);
        }

        JsonNode outBoundLane = request.get("outBoundLane");
		if(outBoundLane != null)
		{
            J2735IntersectionAccessPoint objIntersectionAccessPoint = new J2735IntersectionAccessPoint();

			JsonNode lane = outBoundLane.get("lane");
			if(lane != null)
			{
				objIntersectionAccessPoint.setLane(lane.asInt());
			}

            JsonNode approach = outBoundLane.get("approach");
			if(approach != null)
			{
				objIntersectionAccessPoint.setApproach(approach.asInt());
			}

            JsonNode connection = outBoundLane.get("connection");
			if(connection != null)
			{
				objIntersectionAccessPoint.setConnection(connection.asInt());
			}

			signalRequest.setOutBoundLane(objIntersectionAccessPoint);
        }

		return signalRequest;		
	}
}
