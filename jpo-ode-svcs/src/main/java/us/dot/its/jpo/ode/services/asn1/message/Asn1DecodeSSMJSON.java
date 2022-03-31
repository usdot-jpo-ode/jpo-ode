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
import us.dot.its.jpo.ode.model.OdeSsmMetadata.SsmSource;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeHexByteArray;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeSsmMetadata;

public class Asn1DecodeSSMJSON extends AbstractAsn1DecodeMessageJSON {
    private static final String SSMContentType = "SsmMessageContent";

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	public Asn1DecodeSSMJSON(OdeProperties odeProps) {
		super(new StringPublisher(odeProps));
	}

	@Override
	protected Object process(String consumedData) {

		OdeData odeData = null;
		OdeMsgPayload payload = null;

		try {
			logger.info("Processing SSM data");
			logger.debug("SSM data: {}", consumedData);
			JSONObject rawJSONObject = new JSONObject(consumedData);
			Set<?> keys = rawJSONObject.keySet();
			for (Object key : keys) 
			{				
				if (key != null && key.toString().equals(SSMContentType)) {
					/**process consumed data
					 *  {"SsmMessageContent":[{"metadata":{"utctimestamp":
                     * "2020-11-30T23:45:24.913657Z", "originRsu":"172.250.250.77"},
                     * "payload":"001e120000000005e9c04071a26614c06000040ba0"}]}
					 */
					OdeSsmMetadata metadata = null;
					
					JSONArray rawSSMJsonContentArray = rawJSONObject.getJSONArray(SSMContentType);
					for(int i=0;i<rawSSMJsonContentArray.length();i++)
					{
						JSONObject rawSSMJsonContent = (JSONObject) rawSSMJsonContentArray.get(i);
						String encodedPayload = rawSSMJsonContent.get("payload").toString();
						JSONObject rawmetadata = (JSONObject) rawSSMJsonContent.get("metadata");

						logger.debug("RAW SSM: {}", encodedPayload);
						
						//construct payload
						payload = new OdeAsn1Payload(new OdeHexByteArray(encodedPayload));

						//construct metadata
						metadata = new OdeSsmMetadata(payload);
						metadata.setOdeReceivedAt(rawmetadata.getString("utctimestamp"));
						metadata.setOriginIp(rawmetadata.getString("originRsu"));
						metadata.setRecordType(RecordType.ssmTx);
						
						if (rawmetadata.getString("source").equals("RSU"))
							metadata.setSsmSource(SsmSource.RSU);
						
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
