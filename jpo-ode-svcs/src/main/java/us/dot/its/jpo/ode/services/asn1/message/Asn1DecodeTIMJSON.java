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
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeTimMetadata;

/***
 * Encoded message Processor
 */
public class Asn1DecodeTIMJSON extends AbstractAsn1DecodeMessageJSON {
	private static final String TIMContentType = "TimMessageContent";

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	public Asn1DecodeTIMJSON(OdeProperties odeProps) {
		super(new StringPublisher(odeProps));
	}

	@Override
	protected Object process(String consumedData) {

		OdeData odeData = null;
		OdeMsgPayload payload = null;

		try {
			logger.info("Processing TIM data");
			logger.debug("TIM data: {}", consumedData);
			JSONObject rawJSONObject = new JSONObject(consumedData);
			Set<?> keys = rawJSONObject.keySet();
			for (Object key : keys) 
			{				
				if (key != null && key.toString().equals(TIMContentType)) {
					/**process consumed data
					 *  {"TimMessageContent":[{"metadata":{"utctimestamp":"2020-11-30T23:45:24.913657Z", "originRsu":"172.250.250.77"},"payload":"001f5520100000000000564fb69082709b898aac59717eadfffe4fca1bf0a9d828407e137131558b2e2fd581f46ffff00118b2e3b3ee6e2702c18b2e34f4e6e269ec18b2e285426e2580598b2e23b6e6e254c80420005c48"}]}
					 */
					OdeTimMetadata metadata = null;
					
					JSONArray rawTIMJsonContentArray = rawJSONObject.getJSONArray(TIMContentType);
					for(int i=0;i<rawTIMJsonContentArray.length();i++)
					{
						JSONObject rawTIMJsonContent = (JSONObject) rawTIMJsonContentArray.get(i);
						String encodedPayload = rawTIMJsonContent.get("payload").toString();
						JSONObject rawmetadata = (JSONObject) rawTIMJsonContent.get("metadata");

						logger.debug("RAW TIM: {}", encodedPayload);
						
						//construct payload
						payload = new OdeAsn1Payload(new OdeHexByteArray(encodedPayload));

						//construct metadata
						metadata = new OdeTimMetadata(payload);
						metadata.setOdeReceivedAt(rawmetadata.getString("utctimestamp"));
						metadata.setOriginIp(rawmetadata.getString("originRsu"));
						metadata.setRecordType(RecordType.timMsg);
						metadata.setRecordGeneratedBy(GeneratedBy.RSU);
						
						Asn1Encoding unsecuredDataEncoding = new Asn1Encoding("unsecuredData", "MessageFrame",EncodingRule.UPER);
						metadata.addEncoding(unsecuredDataEncoding);
						
						//construct odeData
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
