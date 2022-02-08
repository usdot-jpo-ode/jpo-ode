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
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.model.OdeMapMetadata.MapSource;
import us.dot.its.jpo.ode.model.OdeMsgPayload;

/***
 * Encoded message Processor
 */
public class Asn1DecodeMAPJSON extends AbstractAsn1DecodeMessageJSON {
	private static final String MAPContentType = "MapMessageContent";

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	public Asn1DecodeMAPJSON(OdeProperties odeProps) {
		super(new StringPublisher(odeProps));
	}

	@Override
	protected Object process(String consumedData) {

		OdeData odeData = null;
		OdeMsgPayload payload = null;

		try {
			logger.info("Processing MAP data");
			logger.debug("MAP data: {}", consumedData);
			JSONObject rawJSONObject = new JSONObject(consumedData);
			Set<?> keys = rawJSONObject.keySet();
			for (Object key : keys) 
			{				
				//Send encoded MAP content to Codec service to decode MAP
				if (key != null && key.toString().equals(MAPContentType)) {
					/**process consumed data { "MapMessageContent": [{ "metadata": { "utctimestamp:"2020-11-30T23:45:24.913657Z" }, "payload":"00121E38041180000138044CD8EA0F3D3FC4A600000100000000030AE0C0615C18"}]}
					 */
					OdeMapMetadata metadata = null;
          
					JSONArray rawMAPJsonContentArray = rawJSONObject.getJSONArray(MAPContentType);
					for (int i = 0; i < rawMAPJsonContentArray.length(); i++) {
						JSONObject rawMAPJsonContent = (JSONObject) rawMAPJsonContentArray.get(i);
						String encodedPayload = rawMAPJsonContent.get("payload").toString();
						JSONObject rawmetadata = (JSONObject) rawMAPJsonContent.get("metadata");
						logger.debug("RAW MAP: {}", encodedPayload);
						// construct payload
						payload = new OdeAsn1Payload(new OdeHexByteArray(encodedPayload));

						// construct metadata
						metadata = new OdeMapMetadata(payload);
						metadata.setOdeReceivedAt(rawmetadata.getString("utctimestamp"));
						metadata.setOriginIp(rawmetadata.getString("originRsu"));
						metadata.setRecordType(RecordType.mapTx);
						metadata.setSecurityResultCode(SecurityResultCode.success);

						if (rawmetadata.getString("source").equals("RSU"))
							metadata.setMapSource(MapSource.RSU);
						else
							metadata.setMapSource(MapSource.V2X);

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
