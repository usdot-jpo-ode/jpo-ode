package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SignalRequestPackage;

public class SignalRequestPackageBuilder {
    private SignalRequestPackageBuilder()
	{
		throw new UnsupportedOperationException();
	}

	public static J2735SignalRequestPackage genericSignalRequestPackage(JsonNode signalRequest) {
		J2735SignalRequestPackage signalRequestPackage = new J2735SignalRequestPackage();
		
        JsonNode request = signalRequest.get("request");
		if(request != null)
		{
			signalRequestPackage.setRequest(SignalRequestBuilder.genericSignalRequest(request));
		}

        JsonNode minute = signalRequest.get("minute");
		if(minute != null)
		{
			signalRequestPackage.setMinute(minute.asInt());
		}

        JsonNode second = signalRequest.get("second");
		if(second != null)
		{
			signalRequestPackage.setSecond(second.asInt());
		}

        JsonNode duration = signalRequest.get("duration");
		if(duration != null)
		{
			signalRequestPackage.setDuration(duration.asInt());
		}

		return signalRequestPackage;		
	}
}
