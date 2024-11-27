package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.util.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class OdeTravelerInputDataTest {

    /**
     * Test method for converting pre-J2735-2016 ASN.1 to J2735-2024 ASN.1
     * Prior to J2735 2016, the following fields had different names:
     * - 'doNotUse1' was 'sspTimRights'
     * - 'doNotUse2' was 'sspLocationRights'
     * - 'doNotUse3' was 'sspMsgContent'
     * - 'doNotUse4' was 'sspMsgTypes'
     * - 'durationTime' was 'duratonTime'
     */
    @Test
    void testConvertPreJ2735_2016ToJ2735_2024() throws IOException, JsonUtils.JsonUtilsException {
        // prepare
        String timRequestPreJ2735_2016 = new String(Files.readAllBytes(Paths.get("src/test/resources/us/dot/its/jpo/ode/model/timRequest_pre-J2735-2016.json")));
        ObjectMapper mapper = new ObjectMapper();
        val inputTID = mapper.readValue(timRequestPreJ2735_2016, OdeTravelerInputData.class);

        // execute
//        TravelerMessageFromHumanToAsnConverter.convertTravelerInputDataToEncodableTim(inputTID);

        // verify
        String expectedTID = new String(Files.readAllBytes(Paths.get("src/test/resources/us/dot/its/jpo/ode/model/timRequest_pre-J2735-2016_ConvertedToJ2735-2024.json")));
        Assertions.assertEquals(expectedTID, inputTID.toString());
    }

}