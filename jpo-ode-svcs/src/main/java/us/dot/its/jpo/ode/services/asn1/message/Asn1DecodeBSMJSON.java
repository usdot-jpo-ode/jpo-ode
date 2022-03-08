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
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;		

/***
 * Encoded message Processor
 */
public class Asn1DecodeBSMJSON extends AbstractAsn1DecodeMessageJSON {
	private static final String BSMContentType = "BsmMessageContent";

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	public Asn1DecodeBSMJSON(OdeProperties odeProps) {
		super(new StringPublisher(odeProps));
	}

	@Override
	protected Object process(String consumedData) {

		OdeData odeData = null;
		OdeMsgPayload payload = null;

		try {
			logger.info("Processing BSM data");
			logger.debug("BSM data: {}", consumedData);
			JSONObject rawJSONObject = new JSONObject(consumedData);
			Set<?> keys = rawJSONObject.keySet();
			for (Object key : keys) 
			{				
				//Send encoded BSM content to Codec service to decode BSM
				if (key != null && key.toString().equals(BSMContentType)) {
					/**process consumed data { "BsmMessageContent": [{ "metadata": { "utctimestamp:"2020-11-30T23:45:24.913657Z" }, "payload":"001480CF4B950C400022D2666E923D1EA6D4E28957BD55FFFFF001C758FD7E67D07F7FFF8000000002020218E1C1004A40196FBC042210115C030EF1408801021D4074CE7E1848101C5C0806E8E1A50101A84056EE8A1AB4102B840A9ADA21B9010259C08DEE1C1C560FFDDBFC070C0222210018BFCE309623120FFE9BFBB10C8238A0FFDC3F987114241610009BFB7113024780FFAC3F95F13A26800FED93FDD51202C5E0FE17BF9B31202FBAFFFEC87FC011650090019C70808440C83207873800000000001095084081C903447E31C12FC0"}]}
					 */
					OdeBsmMetadata metadata = null;
          
					JSONArray rawBSMJsonContentArray = rawJSONObject.getJSONArray(BSMContentType);
					for (int i = 0; i < rawBSMJsonContentArray.length(); i++) {
						JSONObject rawBSMJsonContent = (JSONObject) rawBSMJsonContentArray.get(i);
						String encodedPayload = rawBSMJsonContent.get("payload").toString();
						JSONObject rawmetadata = (JSONObject) rawBSMJsonContent.get("metadata");
						logger.debug("RAW BSM: {}", encodedPayload);
						// construct payload
						payload = new OdeAsn1Payload(new OdeHexByteArray(encodedPayload));

						// construct metadata
						metadata = new OdeBsmMetadata(payload);
						metadata.setOdeReceivedAt(rawmetadata.getString("utctimestamp"));
						metadata.setRecordType(RecordType.bsmTx);
						metadata.setSecurityResultCode(SecurityResultCode.success);
						if (rawmetadata.has("originRsu"))
							metadata.setOriginIp(rawmetadata.getString("originRsu"));

						// construct metadata: receivedMessageDetails
						ReceivedMessageDetails receivedMessageDetails = new ReceivedMessageDetails();
						receivedMessageDetails.setRxSource(RxSource.RV);

						// construct metadata: locationData
						OdeLogMsgMetadataLocation locationData = new OdeLogMsgMetadataLocation();
						receivedMessageDetails.setLocationData(locationData);

						metadata.setReceivedMessageDetails(receivedMessageDetails);
						metadata.setBsmSource(BsmSource.RV);

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
