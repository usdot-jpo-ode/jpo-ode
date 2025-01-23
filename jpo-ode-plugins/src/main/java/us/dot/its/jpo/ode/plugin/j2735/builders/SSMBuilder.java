package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SSM;

public class SSMBuilder {
    private SSMBuilder()
	{
		throw new UnsupportedOperationException();
	}
	public static J2735SSM genericSSM(JsonNode SSMMessage) {
		J2735SSM genericSSM = new J2735SSM();

		JsonNode timeStamp = SSMMessage.get("timeStamp");
		if(timeStamp != null)
		{
			genericSSM.setTimeStamp(timeStamp.asInt());
		}
		
		JsonNode second = SSMMessage.get("second");
		if(second != null)
		{
			genericSSM.setSecond(second.asInt());
		}

        JsonNode sequenceNumber = SSMMessage.get("sequenceNumber");
		if(sequenceNumber != null)
		{
			genericSSM.setSequenceNumber(sequenceNumber.asInt());
		}
		
		JsonNode status = SSMMessage.get("status");
		if(status != null)
		{
			genericSSM.setStatus(SignalStatusListBuilder.genericSignalStatusList(status));				
		}
		return genericSSM;		
	}
}
