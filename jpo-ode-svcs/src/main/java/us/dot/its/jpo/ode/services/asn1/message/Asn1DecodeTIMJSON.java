package us.dot.its.jpo.ode.services.asn1.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.json.JSONObject;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeTimMetadata;
import us.dot.its.jpo.ode.uper.UperUtil;

@Slf4j
public class Asn1DecodeTIMJSON extends AbstractAsn1DecodeMessageJSON {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Asn1DecodeTIMJSON(OdeKafkaProperties odeKafkaProperties, String publishTopic) {
        super(
                publishTopic,
                new StringPublisher(odeKafkaProperties.getBrokers(), odeKafkaProperties.getProducerType(), odeKafkaProperties.getDisabledTopics()),
                UperUtil.getTimStartFlag()
        );}

    @Override
    protected OdeAsn1Data process(String consumedData) {
        OdeAsn1Data messageToPublish = null;
        try {
            JSONObject rawTimJsonObject = new JSONObject(consumedData);

            String jsonStringMetadata = rawTimJsonObject.get("metadata").toString();
            OdeTimMetadata metadata = objectMapper.readValue(jsonStringMetadata, OdeTimMetadata.class);

            Asn1Encoding unsecuredDataEncoding = new Asn1Encoding("unsecuredData", "MessageFrame", EncodingRule.UPER);
            metadata.addEncoding(unsecuredDataEncoding);

            String payloadHexString = ((JSONObject) ((JSONObject) rawTimJsonObject.get("payload")).get("data")).getString("bytes");
            payloadHexString = UperUtil.stripDot2Header(payloadHexString, super.payloadStartFlag);

			if (payloadHexString.equals("BAD DATA")) {
				log.error("NON-TIM DATA ENCOUNTERED IN THE ASN1DECODETIMJSON CLASS");
				return null;
			}

            OdeAsn1Payload payload = new OdeAsn1Payload(HexUtils.fromHexString(payloadHexString));

            messageToPublish = new OdeAsn1Data(metadata, payload);
			publishEncodedMessageToAsn1Decoder(messageToPublish);
        } catch (Exception e) {
            log.error("Error publishing to Asn1DecoderInput: {}", e.getMessage());
        }
        return messageToPublish;
    }
}
