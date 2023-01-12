package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import us.dot.its.jpo.ode.util.JsonUtils;

import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;

public class OdeTimDataTest {
    final String json = "{\"metadata\":{\"securityResultCode\":\"\",\"recordGeneratedBy\":\"RSU\",\"schemaVersion\":\"6\",\"odePacketID\":\"\",\"sanitized\":\"false\",\"recordType\":\"timMsg\",\"recordGeneratedAt\":\"\",\"maxDurationTime\":\"0\",\"odeTimStartDateTime\":\"\",\"receivedMessageDetails\":\"\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\":{\"recordId\":\"0\",\"serialNumber\":\"0\",\"streamId\":\"11ad5323-ec81-4694-8cd0-eb88ca08728e\",\"bundleSize\":\"1\",\"bundleId\":\"0\"},\"logFileName\":\"\",\"odeReceivedAt\":\"2022-12-24T02:24:38.248417Z\",\"originIp\":\"172.18.0.1\"},\"payload\":{\"data\":{\"MessageFrame\":{\"messageId\":\"31\",\"value\":{\"TravelerInformation\":{\"timeStamp\":\"449089\",\"packetID\":\"0000000000000BBC2B\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":{\"regions\":{\"GeographicalPath\":{\"closedPath\":{\"false\":\"\"},\"anchor\":{\"lat\":\"411269876\",\"long\":\"-1047269563\"},\"name\":\"westbound_I-80_366.0_365.0_RSU-10.145.1.100_RW_4456\",\"laneWidth\":\"32700\",\"directionality\":{\"both\":\"\"},\"description\":{\"path\":{\"offset\":{\"xy\":{\"nodes\":{\"NodeXY\":[{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047287423\",\"lat\":\"411264686\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047305390\",\"lat\":\"411260104\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047323629\",\"lat\":\"411256185\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047342080\",\"lat\":\"411252886\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047360706\",\"lat\":\"411250207\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047379480\",\"lat\":\"411248201\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047398354\",\"lat\":\"411246839\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047417290\",\"lat\":\"411246133\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047436246\",\"lat\":\"411245796\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047455202\",\"lat\":\"411245470\"}}},{\"delta\":{\"node-LatLon\":{\"lon\":\"-1047474159\",\"lat\":\"411245173\"}}}]}}},\"scale\":\"0\"}},\"id\":{\"id\":\"0\",\"region\":\"0\"},\"direction\":\"0000000000010000\"}},\"duratonTime\":\"1440\",\"sspMsgRights1\":\"1\",\"sspMsgRights2\":\"1\",\"startYear\":\"2018\",\"msgId\":{\"roadSignID\":{\"viewAngle\":\"1111111111111111\",\"mutcdCode\":{\"warning\":\"\"},\"position\":{\"lat\":\"411269876\",\"long\":\"-1047269563\"}}},\"priority\":\"5\",\"content\":{\"advisory\":{\"SEQUENCE\":[{\"item\":{\"itis\":\"777\"}},{\"item\":{\"itis\":\"13579\"}}]}},\"url\":\"null\",\"sspTimRights\":\"1\",\"sspLocationRights\":\"1\",\"frameType\":{\"advisory\":\"\"},\"startTime\":\"448260\"}},\"msgCnt\":\"1\"}}}},\"dataType\":\"TravelerInformation\"}}";

    //
    // Note that OdeTimData does not have annotations to support deserialization, so serialization/deserialization is not tested here.
    //

    @Test
    public void shouldValidateJson() throws Exception {
        // Load json schema from resource
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        final JsonSchema schema = factory.getSchema(getClass().getClassLoader().getResource("schemas/schema-tim.json").toURI());
        final JsonNode node = (JsonNode)JsonUtils.fromJson(json, JsonNode.class);
        Set<ValidationMessage> validationMessages = schema.validate(node);
        assertEquals(String.format("Json validation errors: %s", validationMessages), 0, validationMessages.size());
    }
}
