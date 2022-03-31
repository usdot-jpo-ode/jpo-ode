package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SignalStatusPackage;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionAccessPoint;
import us.dot.its.jpo.ode.plugin.j2735.J2735PrioritizationResponseStatus;

public class SignalStatusPackageBuilder {
    private SignalStatusPackageBuilder() {
		throw new UnsupportedOperationException();
	}

    public static J2735SignalStatusPackage genericSignalStatusPackage(JsonNode sigStatusNode) {
        J2735SignalStatusPackage signalStatusPackage = new J2735SignalStatusPackage();

        JsonNode requester = sigStatusNode.get("requester");
		if(requester != null)
		{
			signalStatusPackage.setRequester(SignalRequesterInfoBuilder.genericSignalRequesterInfo(requester));;
		}

        JsonNode inboundOn = sigStatusNode.get("inboundOn");
		if(inboundOn != null)
		{
			J2735IntersectionAccessPoint objIntersectionAccessPoint = new J2735IntersectionAccessPoint();

			JsonNode lane = inboundOn.get("lane");
			if(lane != null)
			{
				objIntersectionAccessPoint.setLane(lane.asInt());
			}

            JsonNode approach = inboundOn.get("approach");
			if(approach != null)
			{
				objIntersectionAccessPoint.setApproach(approach.asInt());
			}

            JsonNode connection = inboundOn.get("connection");
			if(connection != null)
			{
				objIntersectionAccessPoint.setConnection(connection.asInt());
			}

			signalStatusPackage.setInboundOn(objIntersectionAccessPoint);
		}

        JsonNode outboundOn = sigStatusNode.get("outboundOn");
		if(outboundOn != null)
		{
			J2735IntersectionAccessPoint objIntersectionAccessPoint = new J2735IntersectionAccessPoint();

			JsonNode lane = outboundOn.get("lane");
			if(lane != null)
			{
				objIntersectionAccessPoint.setLane(lane.asInt());
			}

            JsonNode approach = outboundOn.get("approach");
			if(approach != null)
			{
				objIntersectionAccessPoint.setApproach(approach.asInt());
			}

            JsonNode connection = outboundOn.get("connection");
			if(connection != null)
			{
				objIntersectionAccessPoint.setConnection(connection.asInt());
			}

			signalStatusPackage.setOutboundOn(objIntersectionAccessPoint);
		}

        JsonNode minute = sigStatusNode.get("minute");
		if(minute != null)
		{
			signalStatusPackage.setMinute(minute.asInt());
		}

        JsonNode second = sigStatusNode.get("second");
		if(second != null)
		{
			signalStatusPackage.setSecond(second.asInt());
		}

        JsonNode duration = sigStatusNode.get("duration");
		if(duration != null)
		{
			signalStatusPackage.setDuration(duration.asInt());
		}

        JsonNode status = sigStatusNode.get("status");
		if(status != null)
		{
            J2735PrioritizationResponseStatus enumStatus = J2735PrioritizationResponseStatus.valueOf(status.fieldNames().next());
			signalStatusPackage.setStatus(enumStatus);
		}

        return signalStatusPackage;
    }
}
