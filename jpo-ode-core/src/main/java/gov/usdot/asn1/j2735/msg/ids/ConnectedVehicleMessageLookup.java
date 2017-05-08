package gov.usdot.asn1.j2735.msg.ids;

import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.MapData;
import us.dot.its.jpo.ode.j2735.dsrc.SPAT;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.j2735.semi.AdvisorySituationData;
import us.dot.its.jpo.ode.j2735.semi.AdvisorySituationDataDistribution;
import us.dot.its.jpo.ode.j2735.semi.DataAcceptance;
import us.dot.its.jpo.ode.j2735.semi.DataConfirmation;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
import us.dot.its.jpo.ode.j2735.semi.DataRequest;
import us.dot.its.jpo.ode.j2735.semi.DataSubscriptionCancel;
import us.dot.its.jpo.ode.j2735.semi.DataSubscriptionRequest;
import us.dot.its.jpo.ode.j2735.semi.DataSubscriptionResponse;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationData;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationDataAcceptance;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationDataBundle;
import us.dot.its.jpo.ode.j2735.semi.ObjectDiscoveryData;
import us.dot.its.jpo.ode.j2735.semi.ObjectDiscoveryDataRequest;
import us.dot.its.jpo.ode.j2735.semi.ObjectRegistrationData;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.oss.asn1.AbstractData;

public class ConnectedVehicleMessageLookup {

	private static Map<ConnectedVehicleMessageID, AbstractData> messageMap = new HashMap<ConnectedVehicleMessageID, AbstractData>();

	private static final AbstractData objectList[] = {
			new AdvisorySituationData(),
			new AdvisorySituationDataDistribution(),
			new BasicSafetyMessage(),
			new DataAcceptance(),
			new DataConfirmation(),
			new DataReceipt(),
			new DataRequest(),
			new DataSubscriptionCancel(),
			new DataSubscriptionRequest(),
			new DataSubscriptionResponse(),
			new IntersectionSituationData(),
			new IntersectionSituationDataAcceptance(),
			new IntersectionSituationDataBundle(),
			new MapData(),
			new ObjectDiscoveryData(),
			new ObjectDiscoveryDataRequest(),
			new ObjectRegistrationData(),
			new ServiceRequest(),
			new ServiceResponse(),
			new SPAT(),
			new TravelerInformation(),
			new VehSitDataMessage(),
	};
	
	private static Map<String, AbstractData> objectMap = new HashMap<>(objectList.length);

	static {	
		// vehicle situation data trust establishment
		messageMap.put(new SEMIMessageID(SemiDialogID.vehSitData.longValue(), SemiSequenceID.svcReq.longValue(), 1), new ServiceRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.vehSitData.longValue(), SemiSequenceID.svcResp.longValue(), 1), new ServiceResponse());
		// vehicle situation data message
		messageMap.put(new SEMIMessageID(SemiDialogID.vehSitData.longValue(), SemiSequenceID.data.longValue()), new VehSitDataMessage());
		
		// subscription request trust establishment
		messageMap.put(new SEMIMessageID(SemiDialogID.dataSubscription.longValue(), SemiSequenceID.svcReq.longValue(), 1), new ServiceRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.dataSubscription.longValue(), SemiSequenceID.svcResp.longValue(), 1), new ServiceResponse());
		// subscription message
		messageMap.put(new SEMIMessageID(SemiDialogID.dataSubscription.longValue(), SemiSequenceID.subscriptionReq.longValue(), 1), new DataSubscriptionRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.dataSubscription.longValue(), SemiSequenceID.subscriptinoResp.longValue(), 1), new DataSubscriptionResponse());
		messageMap.put(new SEMIMessageID(SemiDialogID.dataSubscription.longValue(), SemiSequenceID.subscriptionCancel.longValue()), new DataSubscriptionCancel());
		
		// advisory situation data trust establishment
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDataDep.longValue(), SemiSequenceID.svcReq.longValue(), 1), new ServiceRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDataDep.longValue(), SemiSequenceID.svcResp.longValue(), 1), new ServiceResponse());
		// advisory situation data message
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDataDep.longValue(), SemiSequenceID.data.longValue(), 2), new AdvisorySituationData());
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDataDep.longValue(), SemiSequenceID.dataConf.longValue()), new DataConfirmation());
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDataDep.longValue(), SemiSequenceID.accept.longValue()), new DataAcceptance());
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDataDep.longValue(), SemiSequenceID.receipt.longValue()), new DataReceipt());
		
		// RSE advisory situation data query trust establishment
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDatDist.longValue(), SemiSequenceID.svcReq.longValue(), 1), new ServiceRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDatDist.longValue(), SemiSequenceID.svcResp.longValue(), 1), new ServiceResponse());
		// RSE advisory situation data query message
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDatDist.longValue(), SemiSequenceID.dataReq.longValue(), 1), new DataRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDatDist.longValue(), SemiSequenceID.data.longValue()), new AdvisorySituationDataDistribution());
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDatDist.longValue(), SemiSequenceID.accept.longValue()), new DataAcceptance());
		messageMap.put(new SEMIMessageID(SemiDialogID.advSitDatDist.longValue(), SemiSequenceID.receipt.longValue()), new DataReceipt());
		
		// intersection situation data deposit trust establishment
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataDep.longValue(), SemiSequenceID.svcReq.longValue(), 1), new ServiceRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataDep.longValue(), SemiSequenceID.svcResp.longValue(), 1), new ServiceResponse());
		// intersection situation data deposit message
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataDep.longValue(), SemiSequenceID.data.longValue(), 1), new IntersectionSituationData());
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataDep.longValue(), SemiSequenceID.accept.longValue()), new IntersectionSituationDataAcceptance());
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataDep.longValue(), SemiSequenceID.receipt.longValue()), new DataReceipt());
		
		// intersection situation data query trust establishment
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataQuery.longValue(), SemiSequenceID.svcReq.longValue(), 1), new ServiceRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataQuery.longValue(), SemiSequenceID.svcResp.longValue(), 1), new ServiceResponse());
		// intersection situation data query message
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataQuery.longValue(), SemiSequenceID.dataReq.longValue(), 1), new DataRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataQuery.longValue(), SemiSequenceID.data.longValue()), new IntersectionSituationDataBundle());
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataQuery.longValue(), SemiSequenceID.accept.longValue()), new DataAcceptance());
		messageMap.put(new SEMIMessageID(SemiDialogID.intersectionSitDataQuery.longValue(), SemiSequenceID.receipt.longValue()), new DataReceipt());
		
		// object registration trust establishment
		messageMap.put(new SEMIMessageID(SemiDialogID.objReg.longValue(), SemiSequenceID.svcReq.longValue(), 1), new ServiceRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.objReg.longValue(), SemiSequenceID.svcResp.longValue(), 1), new ServiceResponse());
		// object registration message
		messageMap.put(new SEMIMessageID(SemiDialogID.objReg.longValue(), SemiSequenceID.data.longValue()), new ObjectRegistrationData());
		messageMap.put(new SEMIMessageID(SemiDialogID.objReg.longValue(), SemiSequenceID.dataConf.longValue()), new DataConfirmation());
		messageMap.put(new SEMIMessageID(SemiDialogID.objReg.longValue(), SemiSequenceID.accept.longValue()), new DataAcceptance());
		messageMap.put(new SEMIMessageID(SemiDialogID.objReg.longValue(), SemiSequenceID.receipt.longValue()), new DataReceipt());
		
		// object discovery trust establishment
		messageMap.put(new SEMIMessageID(SemiDialogID.objDisc.longValue(), SemiSequenceID.svcReq.longValue(), 1), new ServiceRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.objDisc.longValue(), SemiSequenceID.svcResp.longValue(), 1), new ServiceResponse());
		// object discovery message
		messageMap.put(new SEMIMessageID(SemiDialogID.objDisc.longValue(), SemiSequenceID.dataReq.longValue()), new ObjectDiscoveryDataRequest());
		messageMap.put(new SEMIMessageID(SemiDialogID.objDisc.longValue(), SemiSequenceID.data.longValue()), new ObjectDiscoveryData());
		messageMap.put(new SEMIMessageID(SemiDialogID.objDisc.longValue(), SemiSequenceID.accept.longValue()), new DataAcceptance());
		messageMap.put(new SEMIMessageID(SemiDialogID.objDisc.longValue(), SemiSequenceID.receipt.longValue()), new DataReceipt());
		
		initObjectMap();
	}
	
	public static AbstractData lookupMessage(ConnectedVehicleMessageID msgID) {
		return messageMap.get(msgID);
	}
	
	public static AbstractData lookupMessage(String objectName) {
		return objectMap.get(objectName);
	}
	
	public static List<String> getMessageList() {
		return objectMap.keySet().stream().sorted().collect(Collectors.toList());
	}
	
	private static void initObjectMap() {
		for( AbstractData object : objectList) {
			if ( object != null ) {
				String objectName = object.getClass().getSimpleName();
				objectMap.put(objectName, object);
			}
 
		}
	}
}
