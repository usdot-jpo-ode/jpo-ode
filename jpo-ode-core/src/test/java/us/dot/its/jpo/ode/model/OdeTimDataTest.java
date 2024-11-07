package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import us.dot.its.jpo.ode.util.JsonUtils;

import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class OdeTimDataTest {
    private static final String SCHEMA_VERSION = "7";
    private static final String ASN1_STRING = "005f498718cca69ec1a04600000100105d9b46ec5be401003a0103810040038081d4001f80d07016da410000000000000bbc2b0f775d9b0309c271431fa166ee0a27fff93f136b8205a0a107fb2ef979f4c5bfaeec97e4ad70c2fb36cd9730becdb355cc2fd2a7556b160b98b46ab98ae62c185fa55efb468d5b4000000004e2863f42cddc144ff7980040401262cdd7b809c509f5c62cdd35519c507b9062cdcee129c505cf262cdca5ff9c50432c62cdc5d3d9c502e3e62cdc13e79c501e9262cdbca2d9c5013ee62cdb80359c500e6a62cdb36299c500bc862cdaec1d9c50093c62cdaa2109c5006ea1080203091a859eeebb36006001830001aad27f4ff7580001aad355e39b5880a30029d6585009ef808332d8d9f80c3855151b38c772f765007967ec1170bcb7937f5cb880a25a52863493bcb87570dbcb5abc6bfb2faec606cfa34eb95a24790b2017366d3aabe7729e";

    private static final String json = String.format("{\"metadata\":{\"securityResultCode\":\"\",\"recordGeneratedBy\":\"RSU\",\"schemaVersion\":\"%s\",\"odePacketID\":\"\",\"sanitized\":\"false\",\"asn1\":\"%s\",\"recordType\":\"timMsg\",\"recordGeneratedAt\":\"\",\"maxDurationTime\":\"0\",\"odeTimStartDateTime\":\"\",\"receivedMessageDetails\":\"\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\":{\"recordId\":\"0\",\"serialNumber\":\"0\",\"streamId\":\"11ad5323-ec81-4694-8cd0-eb88ca08728e\",\"bundleSize\":\"1\",\"bundleId\":\"0\"},\"logFileName\":\"\",\"odeReceivedAt\":\"2022-12-24T02:24:38.248417Z\",\"originIp\":\"172.18.0.1\"},\"payload\":{\"data\":{\"MessageFrame\":{\"messageId\":\"31\",\"value\":{\"TravelerInformation\":{\"timeStamp\":\"449089\",\"packetID\":\"0000000000000BBC2B\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":{\"regions\":{\"GeographicalPath\":{\"closedPath\":{\"false\":\"\"},\"anchor\":{\"lat\":\"411269876\",\"long\":\"-1047269563\"},\"name\":\"westbound_I-80_366.0_365.0_RSU-10.145.1.100_RW_4456\",\"laneWidth\":\"32700\",\"directionality\":{\"both\":\"\"},\"description\":{\"path\":{\"offset\":{\"xy\":{\"nodes\":{\"NodeXY\":[{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047287423\",\"lat\":\"411264686\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047305390\",\"lat\":\"411260104\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047323629\",\"lat\":\"411256185\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047342080\",\"lat\":\"411252886\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047360706\",\"lat\":\"411250207\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047379480\",\"lat\":\"411248201\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047398354\",\"lat\":\"411246839\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047417290\",\"lat\":\"411246133\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047436246\",\"lat\":\"411245796\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047455202\",\"lat\":\"411245470\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047474159\",\"lat\":\"411245173\"}}}]}}},\"scale\":\"0\"}},\"id\":{\"id\":\"0\",\"region\":\"0\"},\"direction\":\"0000000000010000\"}},\"durationTime\":\"1440\",\"notUsed2\":\"0\",\"notUsed3\":\"0\",\"startYear\":\"2018\",\"msgId\":{\"roadSignID\":{\"viewAngle\":\"1111111111111111\",\"mutcdCode\":{\"warning\":\"\"},\"position\":{\"lat\":\"411269876\",\"long\":\"-1047269563\"}}},\"priority\":\"5\",\"content\":{\"advisory\":{\"SEQUENCE\":[{\"item\":{\"itis\":\"777\"}},{\"item\":{\"itis\":\"13579\"}}]}},\"url\":\"null\",\"notUsed\":\"0\",\"notUsed1\":\"0\",\"frameType\":{\"advisory\":\"\"},\"startTime\":\"448260\"}},\"msgCnt\":\"1\"}}}},\"dataType\":\"TravelerInformation\"}}", SCHEMA_VERSION, ASN1_STRING);

    //
    // Note that OdeTimData does not have annotations to support deserialization, so serialization/deserialization is not tested here.
    //

    @Test
    public void shouldValidateJson() throws Exception {
        // Load json schema from resource
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        final JsonSchema schema = factory.getSchema(getClass().getClassLoader().getResource("schemas/schema-tim.json").toURI());
        final JsonNode node = (JsonNode)JsonUtils.fromJson(json, JsonNode.class);
        Set<ValidationMessage> validationMessages = schema.validate(node);
        assertEquals(String.format("Json validation errors: %s", validationMessages), 0, validationMessages.size());
    }
}
