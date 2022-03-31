package us.dot.its.jpo.ode.services.asn1.message;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeSpatMetadata;
import us.dot.its.jpo.ode.model.OdeSpatMetadata.SpatSource;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;

/***
 * Encoded message Processor
 */
public class Asn1DecodeSPATJSON extends AbstractAsn1DecodeMessageJSON {
	private static final String SPATContentType = "SpatMessageContent";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public Asn1DecodeSPATJSON(OdeProperties odeProps) {
		super(new StringPublisher(odeProps));
	}

	@Override
	protected Object process(String consumedData) {

		OdeData odeData = null;
		OdeMsgPayload payload = null;

		try {
			logger.info("Processing SPAT data");
			logger.debug("SPAT data: {}", consumedData);
			JSONObject rawJSONObject = new JSONObject(consumedData);
			Set<?> keys = rawJSONObject.keySet();
			for (Object key : keys) {
				
				if (key != null && key.toString().equals(SPATContentType)) {
					/**
					 * process consumed data { "SpatMessageContent": [{ "metadata": { "utctimestamp:
					 * "2020-11-30T23:45:24.913657Z" }
					 * "payload":"00131A604A380583702005837800080008100000040583705043002580"}]}
					 */
					
					OdeSpatMetadata metadata = null;
					JSONArray rawSPATJsonContentArray = rawJSONObject.getJSONArray(SPATContentType);
					for (int i = 0; i < rawSPATJsonContentArray.length(); i++) {
						JSONObject rawSPATJsonContent = (JSONObject) rawSPATJsonContentArray.get(i);
						String encodedPayload = rawSPATJsonContent.get("payload").toString();
						JSONObject rawmetadata = (JSONObject) rawSPATJsonContent.get("metadata");
						logger.debug("RAW SPAT: {}", encodedPayload);
						// construct payload
						payload = new OdeAsn1Payload(new OdeHexByteArray(encodedPayload));

						// construct metadata
						metadata = new OdeSpatMetadata(payload);
						metadata.setOdeReceivedAt(rawmetadata.getString("utctimestamp"));
						metadata.setOriginIp(rawmetadata.getString("originRsu"));
						metadata.setRecordType(RecordType.spatTx);
						metadata.setSecurityResultCode(SecurityResultCode.success);

						// construct metadata: receivedMessageDetails
						ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails();
						receivedMessageDetails.setRxSource(RxSource.NA);

						// construct metadata: locationData
						OdeLogMsgMetadataLocation locationData = new OdeLogMsgMetadataLocation();
						receivedMessageDetails.setLocationData(locationData);

						metadata.setReceivedMessageDetails(receivedMessageDetails);
						metadata.setSpatSource(SpatSource.V2X);

						Asn1Encoding unsecuredDataEncoding = new Asn1Encoding("unsecuredData", "MessageFrame",
								EncodingRule.UPER);
						metadata.addEncoding(unsecuredDataEncoding);

						// construct odeData
						odeData = new OdeAsn1Data(metadata, payload);

						publishEncodedMessageToAsn1Decoder(odeData);
					}
				} 
				else {
					logger.error("Error received invalid key from consumed message");
				}
			}
		} catch (Exception e) {
			logger.error("Error publishing to Asn1DecoderInput: {}", e.getMessage());
		}
		return null;
	}
	
}
