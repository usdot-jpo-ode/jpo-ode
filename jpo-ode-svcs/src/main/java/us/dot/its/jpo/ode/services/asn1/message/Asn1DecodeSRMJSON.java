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
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeSrmMetadata.SrmSource;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeSrmMetadata;

public class Asn1DecodeSRMJSON extends AbstractAsn1DecodeMessageJSON {
    private static final String SRMContentType = "SrmMessageContent";

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	public Asn1DecodeSRMJSON(OdeProperties odeProps) {
		super(new StringPublisher(odeProps));
	}

	@Override
	protected Object process(String consumedData) {

		OdeData odeData = null;
		OdeMsgPayload payload = null;

		try {
			logger.info("Processing SRM data");
			logger.debug("SRM data: {}", consumedData);
			JSONObject rawJSONObject = new JSONObject(consumedData);
			Set<?> keys = rawJSONObject.keySet();
			for (Object key : keys) 
			{				
				if (key != null && key.toString().equals(SRMContentType)) {
					/**process consumed data
					 *  {"SrmMessageContent":[{"metadata":{"utctimestamp":
                     * "2020-11-30T23:45:24.913657Z", "originRsu":"172.250.250.77"},
                     * "payload":"001d2130000010090bd341080d00855c6c0c6899853000a534f7c24cb29897694759b7c0"}]}
					 */
					OdeSrmMetadata metadata = null;
					
					JSONArray rawSRMJsonContentArray = rawJSONObject.getJSONArray(SRMContentType);
					for(int i=0;i<rawSRMJsonContentArray.length();i++)
					{
						JSONObject rawSRMJsonContent = (JSONObject) rawSRMJsonContentArray.get(i);
						String encodedPayload = rawSRMJsonContent.get("payload").toString();
						JSONObject rawmetadata = (JSONObject) rawSRMJsonContent.get("metadata");

						logger.debug("RAW SRM: {}", encodedPayload);
						
						//construct payload
						payload = new OdeAsn1Payload(new OdeHexByteArray(encodedPayload));

						//construct metadata
						metadata = new OdeSrmMetadata(payload);
						metadata.setOdeReceivedAt(rawmetadata.getString("utctimestamp"));
						metadata.setOriginIp(rawmetadata.getString("originRsu"));
                        metadata.setRecordType(RecordType.srmTx);
						
						if (rawmetadata.getString("source").equals("RSU"))
							metadata.setSrmSource(SrmSource.RSU);
						
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
