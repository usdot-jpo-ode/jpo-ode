package us.dot.its.jpo.ode.plugin.j2735.builders;

import javax.xml.transform.Source;

import com.fasterxml.jackson.databind.JsonNode;

import org.codehaus.groovy.runtime.powerassert.SourceText;

import us.dot.its.jpo.ode.plugin.j2735.J2735SRM;
import us.dot.its.jpo.ode.plugin.j2735.J2735SignalRequestList;

public class SRMBuilder {

    private SRMBuilder()
	{
		throw new UnsupportedOperationException();
	}

	public static J2735SRM genericSRM(JsonNode SRMMessage) {
		J2735SRM genericSRM = new J2735SRM();

		JsonNode timeStamp = SRMMessage.get("timeStamp");
		if(timeStamp != null)
		{
			genericSRM.setTimeStamp(timeStamp.asInt());
		}

		JsonNode second = SRMMessage.get("second");
		if(second != null)
		{
			genericSRM.setSecond(second.asInt());
		}

        JsonNode sequenceNumber = SRMMessage.get("sequenceNumber");
		if(sequenceNumber != null)
		{
			genericSRM.setSequenceNumber(sequenceNumber.asInt());
		}

		JsonNode requests = SRMMessage.get("requests");
		if(requests != null)
		{
			J2735SignalRequestList signalRequestList = SignalRequestListBuilder.genericSignalRequestList(requests);
			if (signalRequestList.getRequests().size() > 0) {
				genericSRM.setRequests(signalRequestList);	
			}
			else {
				genericSRM.setRequests(null);	
			}
		}
		
        JsonNode requestor = SRMMessage.get("requestor");
		if(requestor != null)
		{
			genericSRM.setRequestor(RequestorDescriptionBuilder.genericRequestorDescription(requestor));	
		}

		return genericSRM;		
	}
}
